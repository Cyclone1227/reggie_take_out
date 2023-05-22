package com.zwt.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zwt.reggie.common.R;
import com.zwt.reggie.dto.DishDto;
import com.zwt.reggie.entity.Category;
import com.zwt.reggie.entity.Dish;
import com.zwt.reggie.entity.DishFlavor;
import com.zwt.reggie.service.CategoryService;
import com.zwt.reggie.service.DishFlavorService;
import com.zwt.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;


    @PostMapping //新增菜品
    public R<String> save(@RequestBody DishDto dishDto) {
        //dish和dishFlavor多表连接
        dishService.saveWithFlavors(dishDto);
        return R.success("菜品新增成功");
    }

//    prop="categoryName"，返回json数据只有categoryId

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        //分页构造器
        Page<Dish> dishPage = new Page<>(page, pageSize);
        //展示菜品分类
        Page<DishDto> dishDtoPage = new Page<>(page, pageSize);
        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //过滤条件
        queryWrapper.like(name != null, Dish::getName, name);

        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //执行分页查询
        dishService.page(dishPage, queryWrapper);
        //对象拷贝 records是一个list类型
        BeanUtils.copyProperties(dishPage,dishDtoPage,"records");

        List<Dish> records = dishPage.getRecords();

        List<DishDto> list = records.stream().map(item->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);//item包含dish的信息
            Long categoryId = item.getCategoryId();
            //分类对象获得分类名称
            Category category = categoryService.getById(categoryId);
            if(category!=null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList()); //把对象转成list集合

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    @GetMapping("/{id}")     //id在请求的url中
    public R<DishDto> update(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavors(id);
        return R.success(dishDto);
    }

    @PutMapping //修改菜品
    public R<String> update(@RequestBody DishDto dishDto) {
        //dish和dishFlavor多表连接
        dishService.updateWithFlavors(dishDto);
        return R.success("菜品修改成功");
    }

    /**
    //根据条件查询菜品数据,返回多个菜品，构造list集合
    @GetMapping("/list")
    public R<List<Dish>> list(Dish dish){

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //构造查询
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        //添加条件
        queryWrapper.eq(Dish::getStatus,1);
        //排序
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        //查询结果有多个菜品放入list集合
        List<Dish> list = dishService.list(queryWrapper);
        return R.success(list);
    }
    **/

    /**
     * 前端选择规格
     * @param dishDto
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //构造查询
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        //添加条件
        queryWrapper.eq(Dish::getStatus,1);
        //排序
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        //查出菜品基本信息的数据
        List<Dish> list = dishService.list(queryWrapper);
        //DishDto包含flavors和categoryName
        List<DishDto> dishDtoList = list.stream().map(item->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);//item包含dish的信息
            Long categoryId = item.getCategoryId();
            //分类id获得分类对象
            Category category = categoryService.getById(categoryId);
            if(category!=null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            //当前菜品id
            Long itemId = item.getId();
            LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(DishFlavor::getDishId,itemId);
            //根据id查询出相应的口味
            List<DishFlavor> dishFlavorList = dishFlavorService.list(queryWrapper1);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList()); //把对象转成list集合

        return R.success(dishDtoList);
    }




}
