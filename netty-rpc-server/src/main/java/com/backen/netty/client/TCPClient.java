package com.backen.netty.client;

import io.netty.channel.EventLoopGroup;

import io.netty.channel.nio.NioEventLoopGroup;


import com.alibaba.fastjson2.JSONObject;
import com.backen.netty.future.DefaultFuture;
import com.backen.netty.handler.SimpleClientHandler;
import com.backen.netty.model.ClientRequest;
import com.backen.netty.model.Response;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class TCPClient {
	
	static final Bootstrap b = new Bootstrap();
	static ChannelFuture f = null;
	static {
		 EventLoopGroup workerGroup = new NioEventLoopGroup();
		 b.group(workerGroup); 
         b.channel(NioSocketChannel.class); 
         b.option(ChannelOption.SO_KEEPALIVE, true); 
         b.handler(new ChannelInitializer<SocketChannel>() {
             @Override
             public void initChannel(SocketChannel ch) throws Exception {
             	ch.pipeline().addLast(new DelimiterBasedFrameDecoder(65535, Delimiters.lineDelimiter()[0]));
             	ch.pipeline().addLast(new StringDecoder());
             	ch.pipeline().addLast(new SimpleClientHandler());
             	ch.pipeline().addLast(new StringEncoder());
             }
         });
         String host = "localhost";
         int port = 9090;
         try {
			f = b.connect(host, port).sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}  
	}
	
	//send method
	public static Response send(ClientRequest request) {
		String msg = JSONObject.toJSONString(request) + "\r\n";
		f.channel().writeAndFlush(msg);
		DefaultFuture df = new DefaultFuture(request);
		return df.get(60L);
	}
}
	
	
	


