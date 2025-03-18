package com.backen.netty.util;

import com.backen.netty.model.Response;

public class ResponseUtil {
	
	public static Response createSuccessResult(){
		return new Response();
	}
	
	public static Response createFailResult(String status, String msg) {
		Response response = new Response();
		response.setStatus(status);
		response.setMsg(msg);
		return response;
		
	}
	
	public static Response createSucessResult(Object content) {
		Response response = new Response();
		response.setResult(content);
		return response;
	}
		
}
