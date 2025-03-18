package com.backen.netty.medium;
import com.backen.netty.annotation.Remote;
import com.backen.netty.annotation.RemoteInvoke;

import java.lang.reflect.Method;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class InitialMedium implements BeanPostProcessor{

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean.getClass().isAnnotationPresent(Remote.class)) {
	        Method[] method = bean.getClass().getDeclaredMethods();
	        for (Method m:method) {
	        	Map<String, BeanMethod>beanMap = Media.beanMap;
	        	String key = bean.getClass().getInterfaces()[0].getName()+"."+ m.getName();	  
	        	BeanMethod beanMethod = new BeanMethod();
	        	beanMethod.setMethod(m);
	        	beanMethod.setBean(bean);
	        	beanMap.put(key, beanMethod);
	        }
	    }
		System.out.println("BeanPostProcessor 执行中：" + beanName);
		return bean;
	}
}
