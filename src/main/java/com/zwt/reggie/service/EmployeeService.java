package com.zwt.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zwt.reggie.entity.Employee;

//继承了mybatis-plus的父接口IService，内嵌save 方法
public interface EmployeeService extends IService<Employee> {
}
