package com.zwt.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zwt.reggie.dto.SetmealDto;
import com.zwt.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    public void saveWithDish(SetmealDto setmealDto);

    void deleteWithDish(List<Long> ids);
}
