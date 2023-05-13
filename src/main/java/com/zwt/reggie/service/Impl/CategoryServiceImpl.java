package com.zwt.reggie.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zwt.reggie.entity.Category;
import com.zwt.reggie.mapper.CategoryMapper;
import com.zwt.reggie.service.CategoryService;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
}
