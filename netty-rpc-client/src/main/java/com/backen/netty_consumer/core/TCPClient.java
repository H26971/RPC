package com.backen.netty_consumer.core;

import io.netty.channel.EventLoopGroup;

import io.netty.channel.nio.NioEventLoopGroup;

import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;

import com.alibaba.fastjson2.JSONObject;
import com.backen.netty_consumer.constant.Constants;
import com.backen.netty_consumer.handler.SimpleClientHandler;
import com.backen.netty_consumer.param.ClientRequest;
import com.backen.netty_consumer.param.Response;
import com.backen.netty_consumer.zk.ZookeeperFactory;

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
		String host = "localhost";
		int port = 9090;
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
		
		CuratorFramework client = ZookeeperFactory.create();
		try {
			List<String>serverPaths=client.getChildren().forPath(Constants.SERVER_PATH);
			System.out.println("ZK获取到服务节点：" + serverPaths);
			CuratorWatcher watcher = new ServerWatcher();
			//加上ZK监听服务器变化
			client.getChildren().usingWatcher(watcher ).forPath(Constants.SERVER_PATH);
			for (String serverPath:serverPaths) {
				String []str = serverPath.split("#");
				ChannelManager.realServerPath.add(str[0]+"#"+str[1]);
				ChannelFuture channelFuture= TCPClient.b.connect(str[0],Integer.valueOf(str[1]));
				ChannelManager.addChannel(channelFuture);
			}
			
			if(ChannelManager.realServerPath.size()>0) {
				String[] netMessageArray = ChannelManager.realServerPath.toArray()[0].toString().split("#");
				host = netMessageArray[0];
				port = Integer.valueOf(netMessageArray[1]);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

//		try {
//			f = b.connect(host, port).sync();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	}

	// send method
	public static Response send(ClientRequest request) {
		String msg = JSONObject.toJSONString(request) + "\r\n";
		DefaultFuture df = new DefaultFuture(request, 60); // 秒
		f = ChannelManager.get(ChannelManager.position); //轮询
		f.channel().writeAndFlush(msg);
		return df.get();
	}
}
