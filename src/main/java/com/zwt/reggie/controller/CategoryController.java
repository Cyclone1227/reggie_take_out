package com.zwt.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zwt.reggie.common.R;
import com.zwt.reggie.entity.Category;
import com.zwt.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody Category category) {
        categoryService.save(category);
        return R.success("新增菜品分类成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize) {
        //分页构造器
        Page<Category> pageInfo = new Page(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //排序
        queryWrapper.orderByAsc(Category::getSort);
        //查询
        categoryService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }
    //根据id删除分类
    @DeleteMapping
    public R<String> delete(Long ids){
        boolean b = categoryService.removeById(ids);
        if(!b){
            return R.error("删除失败");
        }
        return R.success("分类信息删除成功");
    }

}
