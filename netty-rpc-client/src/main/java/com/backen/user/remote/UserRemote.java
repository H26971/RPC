package com.backen.user.remote;

import java.util.List;

import com.backen.netty_consumer.param.Response;
import com.backen.user.entity.User;

public interface UserRemote {
	public Response saveUser(User user);
	public Response saveUsers(List<User> users);
	
}
