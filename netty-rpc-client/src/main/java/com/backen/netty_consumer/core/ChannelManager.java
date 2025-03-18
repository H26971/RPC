package com.backen.netty_consumer.core;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import io.netty.channel.ChannelFuture;

public class ChannelManager {
	static Set<String> realServerPath = new HashSet<String>();
	static AtomicInteger position = new AtomicInteger(0);
	public static CopyOnWriteArrayList<ChannelFuture> channelFutures = new CopyOnWriteArrayList<>();
	
	public static void removeChannel(ChannelFuture channel) {
		channelFutures.remove(channel);
	}
	
	public static void addChannel(ChannelFuture channel) {
		channelFutures.add(channel);
	}
	
	public static void clearChannel() {
		channelFutures.clear();
	}
	
	public static ChannelFuture get(AtomicInteger i) {
	    if (channelFutures.isEmpty()) {
	        throw new IllegalStateException("连接池为空！没有可用的 ChannelFuture！");
	    }
	    
	    int size = channelFutures.size();
	    int index = i.get();
	    ChannelFuture channelFuture;

	    if (index >= size) {
	        channelFuture = channelFutures.get(0);
	        i.set(1);  
	    } else {
	        channelFuture = channelFutures.get(i.getAndIncrement());
	    }
	    return channelFuture;
	}
	
}
