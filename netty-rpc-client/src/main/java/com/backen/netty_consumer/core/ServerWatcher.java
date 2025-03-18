package com.backen.netty_consumer.core;

import java.util.HashSet;
import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.WatchedEvent;

import com.backen.netty_consumer.zk.ZookeeperFactory;

import io.netty.channel.ChannelFuture;


public class ServerWatcher implements CuratorWatcher {

	@Override
	public void process(WatchedEvent event) throws Exception {
		System.out.println("process------------------------");
		
		CuratorFramework client = ZookeeperFactory.create();
		String path = event.getPath();
		client.getChildren().usingWatcher(this);
		
		List<String> newServerPaths = client.getChildren().forPath(path);
		System.out.println(newServerPaths);
		ChannelManager.realServerPath.clear();
		for(String p :newServerPaths){
			String[] str = path.split("#");
			ChannelManager.realServerPath.add(str[0]+"#"+str[1]);//去重
		} 
		
		ChannelManager.clearChannel();
		for (String realServer: ChannelManager.realServerPath) {
			String []str = realServer.split("#");	
			ChannelFuture channelFuture= TCPClient.b.connect(str[0],Integer.valueOf(str[1]));
			ChannelManager.addChannel(channelFuture);
			
		}
	}	
}
