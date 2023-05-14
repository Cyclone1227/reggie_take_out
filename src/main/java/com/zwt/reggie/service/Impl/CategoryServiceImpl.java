package com.zwt.reggie.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zwt.reggie.common.CustomException;
import com.zwt.reggie.entity.Category;
import com.zwt.reggie.entity.Dish;
import com.zwt.reggie.entity.Setmeal;
import com.zwt.reggie.mapper.CategoryMapper;
import com.zwt.reggie.service.CategoryService;
import com.zwt.reggie.service.DishService;
import com.zwt.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    @Override
    public void remove(Long ids) {
        //添加查询ids
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //看是否关联菜品
        queryWrapper.eq(Dish::getCategoryId, ids);
        //查询相关的的数量
        int count1 = dishService.count(queryWrapper);
        //已关联，无法删除
        if (count1 > 0) {
            throw new CustomException("当前分类已关联菜品，无法删除");
        }

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, ids);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);
        if (count2 > 0) {
            throw new CustomException("当前分类已关联套餐，无法删除");
        }

        super.removeById(ids);
    }
}
