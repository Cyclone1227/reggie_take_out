package com.zwt.reggie.controller;

import com.zwt.reggie.common.R;
import com.zwt.reggie.entity.Orders;
import com.zwt.reggie.service.AddressBookService;
import com.zwt.reggie.service.OrderDetailService;
import com.zwt.reggie.service.OrderService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Resource
    private OrderService orderService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        orderService.submit(orders);
        return R.success("订单支付成功");
    }
}
