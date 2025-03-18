package com.backen.netty_consumer.handler;

import com.alibaba.fastjson2.JSONObject;
import com.backen.netty_consumer.core.DefaultFuture;
import com.backen.netty_consumer.param.Response;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class SimpleClientHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg.toString().equals("ping")) {
			System.out.println("收到读写空闲ping,向服务端发送pong");
			ctx.channel().writeAndFlush("pong\r\n");
		}
		
		Response response = JSONObject.parseObject(msg.toString(), Response.class);
		//通过response的ID可以在map中找到对应的Request,并为相应的request设置response,使得调用get()客户端得到结果
		DefaultFuture.receive(response);
	}
	
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("服务器连接已关闭");
    }

}
