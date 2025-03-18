package com.backen.netty_consumer.zk;

import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.BoundedExponentialBackoffRetry;

public class ZookeeperFactory {
	
	public static CuratorFramework client;
	
	public static CuratorFramework create() {
		if (client == null) {
            BoundedExponentialBackoffRetry retryPolicy = new BoundedExponentialBackoffRetry(1000, 10000, 3);
            client = CuratorFrameworkFactory.newClient("127.0.0.1:2181", retryPolicy);
            client.start();
            
            try {
                client.blockUntilConnected(5, TimeUnit.SECONDS);
                System.out.println("ZooKeeper 连接成功！");
            } catch (InterruptedException e) {
                System.err.println("ZooKeeper 连接失败！");
                e.printStackTrace();
            }
        }
        return client;
    }

    public static void main(String[] args) throws Exception {
        CuratorFramework client = create();
        if (client != null) {
            client.create().forPath("/netty", "Hello Zookeeper!".getBytes());
            System.out.println("节点 `/netty` 创建成功！");
        } else {
            System.err.println("Zookeeper 客户端未能正确启动！");
        }
    }
}
