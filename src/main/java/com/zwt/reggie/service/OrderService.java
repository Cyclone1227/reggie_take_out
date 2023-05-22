package com.zwt.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zwt.reggie.entity.Orders;

public interface OrderService extends IService<Orders> {
    void submit(Orders orders);
}
