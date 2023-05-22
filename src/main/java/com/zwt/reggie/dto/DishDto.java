package com.zwt.reggie.dto;

import com.zwt.reggie.entity.Dish;
import com.zwt.reggie.entity.DishFlavor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data //数据传输对象
public class DishDto extends Dish {
    //添加菜品缺少唯一字段 flavors
    private List<DishFlavor> flavors;
     //展示菜品分类
    private String categoryName;

    private Integer copies;
}
