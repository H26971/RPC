package com.backen.netty_consumer.proxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.stereotype.Component;

import com.backen.netty_consumer.annotation.RemoteInvoke;
import com.backen.netty_consumer.core.TCPClient;
import com.backen.netty_consumer.param.ClientRequest;
import com.backen.netty_consumer.param.Response;

@Component
public class InvokeProxy implements BeanPostProcessor{

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		Field[] fields = bean.getClass().getDeclaredFields();
		for (Field field :fields) {
			if (field.isAnnotationPresent(RemoteInvoke.class)) {
				field.setAccessible(true);
				
				final Map<Method, Class> methodClassMap = new HashMap<Method, Class>();
				putMetodClass(methodClassMap,field);
				
				Enhancer enhancer = new Enhancer();
				enhancer.setInterfaces(new Class[] {field.getType()});
				enhancer.setCallback(new MethodInterceptor() {
					@Override
					public Object intercept(Object instance, Method method, Object[] args, MethodProxy proxy) throws Throwable {
						//调用服务器
						ClientRequest request = new ClientRequest();
						request.setContent(args[0]);
						request.setCommand(methodClassMap.get(method).getName()+"."+ method.getName());
						System.out.println("InvokeProxy 正在处理: " + bean.getClass().getName());
						Response resp = TCPClient.send(request);
						return resp;
					}
				});
				
				try {
					field.set(bean, enhancer.create());
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				} 
			}
		}
		return bean;
	}

	private void putMetodClass(Map<Method, Class> methodClassMap, Field field) {
		Method [] methods = field.getType().getDeclaredMethods();
		for (Method m:methods) {
			methodClassMap.put(m, field.getType());
		}
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

}
