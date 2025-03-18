package com.backen.netty.handler;

import org.apache.zookeeper.server.Request;

import com.alibaba.fastjson2.JSONObject;
import com.backen.netty.model.Response;
import com.backen.netty.model.ServerRequest;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class SimpleServerHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {	
		ServerRequest request = JSONObject.parseObject(msg.toString(),ServerRequest.class);

		Response response = new Response();
		response.setId(request.getId());
		response.setResult("Server OK \r\n");
		ctx.channel().writeAndFlush(JSONObject.toJSONString(response));
		ctx.channel().writeAndFlush("\r\n");
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent event = (IdleStateEvent)evt;
			if (event.state().equals(IdleState.READER_IDLE)) {
				System.out.println("读空闲");
				ctx.channel().close();
			} else if (event.state().equals(IdleState.WRITER_IDLE)){
				System.out.println("写空闲");
			} else if (event.state().equals(IdleState.ALL_IDLE)){
				ctx.channel().writeAndFlush("Ping\r\n");
			}
		}
	}
}
