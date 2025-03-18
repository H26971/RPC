package com.backen.netty_rpc;

import org.junit.jupiter.api.Test;

import com.backen.netty.client.TCPClient;
import com.backen.netty.model.ClientRequest;
import com.backen.netty.model.Response;
import com.backen.user.entity.User;

public class TestTCP {
	@Test
	public void testGetResponse() {
		ClientRequest request = new ClientRequest();
		request.setContent("测试请求");
		Response resp = TCPClient.send(request);
		System.out.println(resp.getResult());
	
	}
	
	@Test
	public void testServerUser() {
		ClientRequest request = new ClientRequest();
		User u = new User();
		u.setName("A");
		u.setId(1);
		
		request.setContent(u);
		request.setCommand("com.backen.user.controller.UserController.saveUser");
		
		Response resp = TCPClient.send(request);
		System.out.println(resp.getResult());
	
	}

}
