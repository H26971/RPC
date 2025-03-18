package com.backen.user.remote;

import com.backen.netty_consumer.param.Response;
import com.backen.user.entity.User;

public interface TestRemote {
	public Response testUser(User user);
}
