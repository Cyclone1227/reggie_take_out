package com.zwt.reggie.controller;

import com.zwt.reggie.service.OrderDetailService;
import com.zwt.reggie.service.OrderService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/orderDetail")
public class OrderDetailController {

    @Resource
    private OrderDetailService orderDetailService ;
}
