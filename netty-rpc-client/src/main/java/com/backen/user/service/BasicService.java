package com.backen.user.service;

import org.springframework.stereotype.Service;

import com.backen.netty_consumer.core.TCPClient;
import com.backen.netty_consumer.param.ClientRequest;


@Service
public class BasicService {
	public void test(){
		ClientRequest request = new ClientRequest();
	    TCPClient.send(request);
		System.out.println("调用了TestService.test");
	}
}
