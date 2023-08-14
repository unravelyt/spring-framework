package com.unravely;

import com.unravely.config.AppConfig;
import com.unravely.service.UserService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

/**
 * <p>description</p>
 *
 * @author : name
 * @date : 2023-08-14 10:41
 */
public class Main {

	public static void main(String[] args) {
		System.out.println("Hello world!");

		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
		UserService userService = context.getBean(UserService.class);

		userService.print();

	}



}