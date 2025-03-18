package com.backen.user.Controller;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.backen.user.service.BasicService;

@Configuration
@ComponentScan("com.backen")
public class BasicController {
	public static void main(String[] args) {
		ApplicationContext context = 
				new AnnotationConfigApplicationContext(BasicController.class);
		BasicService basicService = context.getBean(BasicService.class);
		basicService.test();;
	}
}
