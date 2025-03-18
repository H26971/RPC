//package com.backen.netty.client;
//
//import io.netty.channel.EventLoopGroup;
//import io.netty.channel.nio.NioEventLoopGroup;
//
//import com.backen.netty.handler.SimpleClientHandler;
//
//import io.netty.bootstrap.Bootstrap;
//import io.netty.channel.ChannelFuture;
//import io.netty.channel.ChannelInitializer;
//import io.netty.channel.ChannelOption;
//import io.netty.channel.socket.SocketChannel;
//import io.netty.channel.socket.nio.NioSocketChannel;
//import io.netty.handler.codec.DelimiterBasedFrameDecoder;
//import io.netty.handler.codec.Delimiters;
//import io.netty.handler.codec.string.StringDecoder;
//import io.netty.handler.codec.string.StringEncoder;
//import io.netty.util.AttributeKey;
//
//public class NettyClient {
//	
//	public static void main(String[] args) throws InterruptedException {
//		String host = "localhost";
//        int port = 9090;
//        
//        EventLoopGroup workerGroup = new NioEventLoopGroup();
//        
//        try {
//            Bootstrap b = new Bootstrap(); 
//            b.group(workerGroup); 
//            b.channel(NioSocketChannel.class); 
//            b.option(ChannelOption.SO_KEEPALIVE, true); 
//            b.handler(new ChannelInitializer<SocketChannel>() {
//                @Override
//                public void initChannel(SocketChannel ch) throws Exception {
//                	ch.pipeline().addLast(new DelimiterBasedFrameDecoder(65535, Delimiters.lineDelimiter()[0]));
//                	ch.pipeline().addLast(new StringDecoder());
//                	ch.pipeline().addLast(new SimpleClientHandler());
//                	ch.pipeline().addLast(new StringEncoder());
//                }
//            });
//            
//            ChannelFuture f = b.connect(host, port).sync(); 
//            f.channel().writeAndFlush("Hello Server\r\n");
//
//            f.channel().closeFuture().sync();
//            Object result =f.channel().attr(AttributeKey.valueOf("hhh")).get();
//            System.out.println("获取返回数据==="+result.toString());
//            
//            
//            
//        } finally {
//            workerGroup.shutdownGracefully();
//        }
//    }
//}
//
//
