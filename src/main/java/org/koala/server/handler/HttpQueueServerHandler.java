package org.koala.server.handler;
import static io.netty.buffer.Unpooled.copiedBuffer;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import org.apache.log4j.Logger;
import org.koala.server.core.Controller;
import org.koala.server.core.EasyNettyConfig;
import org.koala.util.UriUtils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

public class HttpQueueServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> implements Runnable {
	private static ArrayBlockingQueue<HttpPoJo> HttpPoJoQueue = null;
	private static Logger logger = Logger.getLogger(HttpQueueServerHandler.class);
	
    public static void init(int userQueueSize, int workerThreadNum) {
		logger.info("HttpQueueServerHandler init user pojo queue. size: " + userQueueSize);
		HttpPoJoQueue = new ArrayBlockingQueue<HttpPoJo>(userQueueSize);
		logger.info("HttpQueueServerHandler init thread workers. size: " + workerThreadNum);
		for(int i=0; i < workerThreadNum; i++) {
			Thread t = new Thread(new HttpQueueServerHandler());
			t.start();
		}
	}
	@Override
	protected void messageReceived(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
		String uri = msg.uri();
		String body = msg.content().toString(io.netty.util.CharsetUtil.UTF_8);
		HttpPoJoQueue.put(new HttpPoJo(ctx,uri,body));
	}

	public void run() {
		while(true) {
			StringBuilder responseContent = new StringBuilder();
			HttpResponseStatus httpResponseStatus = HttpResponseStatus.OK;
			// 获取消息
			HttpPoJo httppojo = null;
			ChannelHandlerContext ctx = null;
			try {
				httppojo = HttpPoJoQueue.take();
				ctx = httppojo.getCtx();
			} catch (Exception e1) {
				logger.error("worker thread canceled.");
				break;
			}
			String uri = null;
			String body = null;
			try {
				// 解析消息
				uri = httppojo.getUri();
				body = httppojo.getBody();
				if( uri == null || body == null ) {
					httpResponseStatus = HttpResponseStatus.BAD_REQUEST;
					responseContent.append("400 BAD REQUEST");
					continue;
				}
				Map<String, String> paras = UriUtils.getPara(uri);
				String path = paras.get("__prv");
				Controller controller = EasyNettyConfig.getController(path);
				if (controller == null) {
					responseContent.append("404 NOT FOUND");
					httpResponseStatus = HttpResponseStatus.NOT_FOUND;
				} else {
					String result = controller.process(paras, body);
					responseContent.append(result);
					httpResponseStatus = HttpResponseStatus.OK;
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("worker thread exception: " + e.toString());
				httpResponseStatus = HttpResponseStatus.INTERNAL_SERVER_ERROR;
				responseContent.append("INTERNAL SERVER ERROR");
			} finally {
				render(ctx, httpResponseStatus, responseContent.toString());
			}
		}
	}
	
	/**
	 * 返回结果
	 * @param ctx
	 * @param httpResponseStatus
	 * @param result
	 */
	private void render(ChannelHandlerContext ctx, HttpResponseStatus httpResponseStatus, String result) {
		// 写消息
		if (ctx.channel().isWritable()) {
			ByteBuf buf = copiedBuffer(result, CharsetUtil.UTF_8);
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
					httpResponseStatus, buf);
			response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
			response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=utf-8");
			response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
			ctx.write(response);
		}
		ctx.flush();
	}
}