package com.backen.netty_rpc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.backen.netty.annotation.RemoteInvoke;
import com.backen.user.entity.User;
import com.backen.user.remote.UserRemote;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RemoteInvokeTest.class)
@ComponentScan("com.backen.netty_consumer")

public class RemoteInvokeTest {
	
	@RemoteInvoke
	private UserRemote userRemote;
	
	@Test
	public void testServerUser() {
		User u = new User();
		u.setName("A");
		u.setId(1);
		userRemote.saveUser(u);
	
	}
}
