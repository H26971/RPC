package com.backen.user.remote;

import java.util.List;

import javax.annotation.Resource;

import com.backen.netty.annotation.Remote;
import com.backen.netty.model.Response;
import com.backen.netty.util.ResponseUtil;
import com.backen.user.entity.User;
import com.backen.user.service.UserService;

@Remote
public class UserRemoteImpl implements UserRemote{
	@Resource
	private UserService userService;
	
	public Response saveUser(User user) {
		userService.save(user);
		return ResponseUtil.createSucessResult(user);
	}
	
	public Response saveUsers(List<User> users) {
		userService.saveList(users);
		return ResponseUtil.createSucessResult(users);
	}
}
