package com.backen.netty.future;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.backen.netty.model.ClientRequest;
import com.backen.netty.model.Response;

public class DefaultFuture {
	public final static ConcurrentHashMap<Long, DefaultFuture> allDefaultFuture = new ConcurrentHashMap<Long, DefaultFuture>();
	final Lock lock = new ReentrantLock();
	private Condition condition = lock.newCondition();
	private Response response;
	
	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}
	
	public DefaultFuture(ClientRequest request){
		allDefaultFuture.put(request.getId(), this);
	}
	
	public Response get(Long timeInSeconds) {
	    long startTime = System.currentTimeMillis();
	    long timeoutMillis = timeInSeconds * 1000L;
	    lock.lock();
	    try {
	        while (!done()) {
	            long elapsed = System.currentTimeMillis() - startTime;
	            if (elapsed >= timeoutMillis) {
	                System.out.println("Future中的请求超时");
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

	private boolean done() {
		return this.response != null;
	}
	
	
}
