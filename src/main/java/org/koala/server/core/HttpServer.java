package org.koala.server.core;

import org.apache.log4j.Logger;
import org.koala.server.handler.HttpQueueServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class HttpServer {
	private static Logger logger = Logger.getLogger(HttpServer.class);

	public static ChannelPromise promise;
	
	private int port = 8088;
	private int pgnum = 2;
	private int cgnum = 20;

	public HttpServer(int port, int pgnum, int cgnum) {
		this.port = port;
		this.pgnum = pgnum;
		this.cgnum = cgnum;
	}

	public void run() {

		NioEventLoopGroup bossGroup = new NioEventLoopGroup(pgnum);
		NioEventLoopGroup workGroup = new NioEventLoopGroup(cgnum);
		System.out.println("bossGroupSize: " + pgnum + ", workGroupSize: " + cgnum);
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workGroup);
			bootstrap.channel(NioServerSocketChannel.class)
			.option(ChannelOption.SO_BACKLOG, 100);
			bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
				protected void initChannel(SocketChannel ch) {
					ChannelPipeline pipeline = ch.pipeline();
					pipeline.addLast("decoder", new HttpRequestDecoder());
					pipeline.addLast("aggregator", new HttpObjectAggregator(65535));
					pipeline.addLast("encoder", new HttpResponseEncoder());
					pipeline.addLast("handler", new HttpQueueServerHandler());
				}
			})
			.childOption(ChannelOption.TCP_NODELAY, true)
			.childOption(ChannelOption.SO_RCVBUF, 65535)
			.childOption(ChannelOption.SO_KEEPALIVE, true);
			ChannelFuture future = bootstrap.bind(port).sync();
			logger.info("Server started. Listen port: " + port + " :)");
			future.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			workGroup.shutdownGracefully();
			workGroup.shutdownGracefully();
		}

	}
}