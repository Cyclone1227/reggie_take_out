package com.zwt.reggie.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sun.org.apache.xpath.internal.operations.Or;
import com.zwt.reggie.common.BaseContext;
import com.zwt.reggie.common.CustomException;
import com.zwt.reggie.entity.*;
import com.zwt.reggie.mapper.OrderMapper;
import com.zwt.reggie.service.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    /**
     * 订单支付
     * @param orders
     */
    @Resource
    private OrderDetailService orderDetailService;
    @Resource
    private UserService userService;
    @Resource
    private ShoppingCartService shoppingCartService;
    //用户设置地址
    @Resource
    private AddressBookService addressBookService;

    @Override
    @Transactional
    public void submit(Orders orders) {
        //获得当前用户id
        Long userId = BaseContext.getCurrentId();
        //查询用户购物车数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        //购物车的多条数据
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(queryWrapper);
        if (shoppingCartList==null||shoppingCartList.size()==0){
            throw new CustomException("购物车没有物品，不能下单");
        }
        //查询用户数据
        User user = userService.getById(userId);
        //查询用户地址数据
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        //查不到相关地址
        if(addressBook==null){
            throw new CustomException("地址信息有误，不能下单");
        }

        Long ordersId = IdWorker.getId();//订单号
        //参数对象
        orders.setId(ordersId);
        //进行购物车的金额数据计算 顺便把订单明细给计算出来
        AtomicInteger amount = new AtomicInteger(0);//使用原子类来保存计算的金额结果
        //这个item是集合中的每一个shoppingCarts对象,是在变化的
        List<OrderDetail> orderDetails = shoppingCartList.stream().map((item)->{
            //每对item进行一次遍历就产生一个新的orderDetail对象,然后对orderDetail进行设置,然后返回被收集,被封装成一个集合
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(ordersId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());//单份的金额
            //addAndGet进行累加 item.getAmount()单份的金额  multiply乘  item.getNumber()份数
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());

        //向订单插入数据,一条数据  因为前端传过来的数据太少了,所以我们需要对相关的属性进行填值

        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        //Amount是指订单总的金额
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(ordersId));

        if (user.getName() != null){
            orders.setUserName(user.getName());
        }
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        //向订单表添加一条数据
        this.save(orders);
        //向订单详细表添加多个菜品
        orderDetailService.saveBatch(orderDetails);
        //清空购物车
        shoppingCartService.remove(queryWrapper);
    }
}
