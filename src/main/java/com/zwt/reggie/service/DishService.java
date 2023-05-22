package com.zwt.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zwt.reggie.dto.DishDto;
import com.zwt.reggie.entity.Dish;

import java.time.LocalDateTime;

public interface DishService extends IService<Dish> {

    //保存接口，用于dish和dishId联合
    public void saveWithFlavors(DishDto dishDto);

    public DishDto getByIdWithFlavors(Long id);

    public void updateWithFlavors(DishDto dishDto);
}
