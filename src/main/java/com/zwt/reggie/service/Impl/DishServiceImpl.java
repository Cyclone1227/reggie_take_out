package com.zwt.reggie.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zwt.reggie.dto.DishDto;
import com.zwt.reggie.entity.Dish;
import com.zwt.reggie.entity.DishFlavor;
import com.zwt.reggie.mapper.DishMapper;
import com.zwt.reggie.service.DishFlavorService;
import com.zwt.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    //新增菜品，同时保存口味
    @Transactional //保证数据一致性
    public void saveWithFlavors(DishDto dishDto) {
        //保存菜品表dish
        this.save(dishDto);
        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        //处理集合，遍历每一项item 对应一个实体
        flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList()); //有加工为一个列表，其中包含dishId
        //保存口味数据到菜品口味表dish_flavors
        dishFlavorService.saveBatch(flavors);
    }

    public DishDto getByIdWithFlavors(Long id) {

        //查询dish中id
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        //查询flavors
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getId, dish.getId());
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);
        //为dto加入flavors的相关值
        dishDto.setFlavors(list);
        return dishDto;
    }

    @Transactional
    public void updateWithFlavors(DishDto dishDto) {
        //更新dish基本信息
        this.updateById(dishDto);
        //删除原来口味
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        //添加当前的口味 dish_flavors
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.stream().map(item -> {
            //将dish设置dishId从而获得flavors
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);


    }
}
