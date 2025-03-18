package com.backen.netty.medium;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson2.JSONObject;
import com.backen.netty.model.Response;
import com.backen.netty.model.ServerRequest;

public class Media {
	
	public  static Map<String, BeanMethod> beanMap;
	static {
		beanMap = new HashMap<String, BeanMethod>();
	}
	
	private  static Media m= null;
	private Media() {
		
	}
	
	public static Media newInstance() {
		if (m==null) {
			m = new Media();
		}
		return m;
		
	}

	//反射处理业务
	public Response process(ServerRequest request) {
		Response result = null;
		try {
			String command = request.getCommand();
			BeanMethod beanMethod =	beanMap.get(command);
			if (beanMethod ==null) {
				return null;
			}
			
			Object bean = beanMethod.getBean();
			Method method = beanMethod.getMethod();
			
			Class paramType = method.getParameterTypes()[0];
			Object content = request.getContent();
			Object args = JSONObject.parseObject(JSONObject.toJSONString(content), paramType);
			
			System.out.println("准备反射调用：" + method.getName() + "，参数：" + args);
			result = (Response) method.invoke(bean,args);
			result.setId(request.getId());
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return result;
	}
}
	
