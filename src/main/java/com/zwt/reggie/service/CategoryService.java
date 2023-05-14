package com.zwt.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zwt.reggie.entity.Category;

public interface CategoryService extends IService<Category>  {
    void remove(Long ids);

}
