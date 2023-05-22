package com.zwt.reggie.dto;

import com.zwt.reggie.entity.Setmeal;
import com.zwt.reggie.entity.SetmealDish;
import lombok.Data;

import java.util.List;
//用于多表连接
@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
