package org.koala.server.handler;

import io.netty.channel.ChannelHandlerContext;
public class HttpPoJo {
	ChannelHandlerContext ctx;
	String uri;
	String body;
	
	public HttpPoJo(ChannelHandlerContext ctx, String uri, String body) {
		this.ctx = ctx;
		this.uri = uri;
		this.body = body;
	}
	
	public void setContext(ChannelHandlerContext ctx, String uri, String body) {
		this.ctx = ctx;
		this.uri = uri;
		this.body = body;
	}
	
	public ChannelHandlerContext getCtx() {
		return this.ctx;
	}
	
	public String getBody() {
		return this.body;
	}
	
	public String getUri() {
		return this.uri;
	}
}
