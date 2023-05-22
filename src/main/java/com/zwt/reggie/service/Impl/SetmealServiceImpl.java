package com.zwt.reggie.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zwt.reggie.common.CustomException;
import com.zwt.reggie.dto.SetmealDto;
import com.zwt.reggie.entity.Setmeal;
import com.zwt.reggie.entity.SetmealDish;
import com.zwt.reggie.mapper.SetmealMapper;
import com.zwt.reggie.service.SetmealDishService;
import com.zwt.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Resource
    private SetmealDishService setmealDishService;

    //新增套餐，用于Setmeal和SetmealDish两表的连接查询

   @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //添加基本信息在setmeal中
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //包含多种菜品，进行遍历
        setmealDishes.stream().map((item)->{
          item.setSetmealId(String.valueOf(setmealDto.getId()));
           return item;
        }).collect(Collectors.toList());
        //添加基本信息在setmeal_Dish中,多种菜品
        setmealDishService.saveBatch(setmealDishes);

    }

    @Transactional
    public void deleteWithDish(List<Long> ids) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);
        //状态为1的起售
        int count = this.count(queryWrapper);
        if(count>0) {
            throw new CustomException("该套餐已经启售，不能删除");
        }
        //删除setmeal表数据
        this.removeByIds(ids);
        //删除setmealDish表数据
        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(SetmealDish::getDishId,ids);
        setmealDishService.remove(queryWrapper1);
    }
}
