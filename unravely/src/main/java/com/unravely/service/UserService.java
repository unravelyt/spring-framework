package com.unravely.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//@Service
public class UserService {


//    @Autowired
    private OrderService orderService;


    public void print() {
        System.out.println("=== UserService start ===");
        System.out.println("===orderService===" + orderService.toString());
        System.out.println("=== UserService end ===");
    }

	public void setOrderService(OrderService orderService) {
		//https://blog.csdn.net/Sora_key/article/details/112425195
		this.orderService = orderService;
	}
}
