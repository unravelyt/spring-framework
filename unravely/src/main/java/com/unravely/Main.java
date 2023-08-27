package com.unravely;

import com.unravely.config.AppConfig;
import com.unravely.service.UserService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;


public class Main {


	/*
	  DefaultListableBeanFactory
	  ClassPathXmlApplicationContext：它是从类的根路径下加载配置文件推荐使用这种
	  FileSystemXmlApplicationContext：它是从磁盘路径上加载配置文件，配置文件可以在磁盘的任意位置
	  AnnotationConfigApplicationContext:当我们使用注解配置容器对象时，需要使用此类来创建spring容器。它用来读取注解
	 */

	//blog.csdn.net/qq_36882793/article/details/106440723 Spring源码分析：全集整理
	//	深入学习spring源码，应该买什么书？ www.zhihu.com/question/400486014/answer/1275620442
	//https://segmentfault.com/a/1190000022372094
	public static void main(String[] args) {
		System.out.println("Hello world!");

		contextTest();

	}

	private static void contextTest() {
		//注解
		AnnotationConfigApplicationContext context2 = new AnnotationConfigApplicationContext(AppConfig.class);

		AnnotationConfigWebApplicationContext context1 = new AnnotationConfigWebApplicationContext();

		//xml
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("application.xml");
		UserService userService = context.getBean(UserService.class);
		userService.print();
	}


}