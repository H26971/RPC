package com.backen.netty.initial;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.backen.netty.constant.Constants;
import com.backen.netty.factory.ZookeeperFactory;
import com.backen.netty.handler.ServerHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

@Component
public class NettyInitial implements ApplicationListener<ContextRefreshedEvent>{
	public void start() {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workGroup);
			bootstrap.option(ChannelOption.SO_BACKLOG, 128) //允许128个通道进行排队
					.childOption(ChannelOption.SO_KEEPALIVE, false).channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(new DelimiterBasedFrameDecoder(65535, Delimiters.lineDelimiter()));
							ch.pipeline().addLast(new StringDecoder());
							ch.pipeline().addLast(new IdleStateHandler(60,45,20,TimeUnit.SECONDS));
							ch.pipeline().addLast(new ServerHandler());
							ch.pipeline().addLast(new StringEncoder());
						}
					});
			
			int port = 9090;
			ChannelFuture future = bootstrap.bind(port).sync();
			
			CuratorFramework client = ZookeeperFactory.create();
			InetAddress netAddress = InetAddress.getLocalHost();
			String serverPath = Constants.SERVER_PATH+netAddress.getHostAddress()+"#"+port+"#";

			if (client.checkExists().forPath(serverPath) == null) { 
				System.out.println("成功");
			    client.create().withMode(CreateMode.EPHEMERAL).forPath(serverPath);
			} else {
			    System.out.println("Zookeeper 节点已存在: " + serverPath);
			}
			
			System.out.println("Server started on port 9090");
			future.channel().closeFuture().sync();
		} catch (Exception e) {
			e.printStackTrace();
			bossGroup.shutdownGracefully();
		    workGroup.shutdownGracefully();
		} 
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		this.start();
		
	}
}
