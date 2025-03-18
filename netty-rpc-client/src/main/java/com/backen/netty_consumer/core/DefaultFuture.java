package com.backen.netty_consumer.core;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.backen.netty_consumer.param.ClientRequest;
import com.backen.netty_consumer.param.Response;


public class DefaultFuture {
	public final static ConcurrentHashMap<Long, DefaultFuture> allDefaultFuture = new ConcurrentHashMap<>();
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private Response response;

    private final long requestId;
    private final long startTimeMillis;
    private final long timeoutMillis;  // 每个请求的超时时间

    // Getter 方法
    public long getStartTimeMillis() {
        return startTimeMillis;
    }

    public long getTimeoutMillis() {
        return timeoutMillis;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    // 构造器：带超时参数
    public DefaultFuture(ClientRequest request, long timeoutSeconds) {
        this.requestId = request.getId();
        this.startTimeMillis = System.currentTimeMillis();
        this.timeoutMillis = timeoutSeconds * 1000L;  // 转毫秒
        allDefaultFuture.put(request.getId(), this);
    }

    // get 方法：等待响应或超时
    public Response get() {
        lock.lock();
        try {
            while (!done()) {
                long elapsed = System.currentTimeMillis() - startTimeMillis;
                if (elapsed >= timeoutMillis) {
                    System.out.println("Future中的请求超时");
                    this.response = buildTimeoutResponse();
                    allDefaultFuture.remove(this.requestId);
                    break;
                }
                long remaining = timeoutMillis - elapsed;
                condition.await(remaining, TimeUnit.MILLISECONDS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return this.response;
    }

    // 检查是否已完成
    private boolean done() {
        return this.response != null;
    }

    // 构建超时响应
    private Response buildTimeoutResponse() {
        Response resp = new Response();
        resp.setId(this.requestId);
        resp.setStatus("00408");  // 408 Request Timeout
        resp.setMsg("请求超时");
        return resp;
    }

    // 响应到达，唤醒线程
    public static void receive(Response response) {
        DefaultFuture df = allDefaultFuture.get(response.getId());
        if (df != null) {
            Lock lock = df.lock;
            lock.lock();
            try {
                df.setResponse(response);
                df.condition.signal();
                allDefaultFuture.remove(response.getId());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

    // 检查是否超时（用于异常清理）
    public boolean isTimeout() {
        long now = System.currentTimeMillis();
        return (response == null) && ((now - startTimeMillis) >= timeoutMillis);
    }

    // 异常请求清理线程
    static class FutureThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Set<Long> ids = new HashSet<>(allDefaultFuture.keySet());  // 遍历副本
                    for (Long id : ids) {
                        DefaultFuture df = allDefaultFuture.get(id);
                        if (df != null && df.isTimeout()) {
                            Response res = new Response();
                            res.setId(id);
                            res.setStatus("00408");
                            res.setMsg("链路超时");
                            receive(res);  // 唤醒线程或清理资源
                        }
                    }
                    Thread.sleep(1000);  // 每秒扫描一次
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 静态代码块：类加载时启动清理线程
    static {
        FutureThread futureThread = new FutureThread();
        futureThread.setDaemon(true);
        futureThread.start();
    }

	
}
