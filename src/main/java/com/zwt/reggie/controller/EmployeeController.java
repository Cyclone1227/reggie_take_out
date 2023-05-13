package com.zwt.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zwt.reggie.common.BaseContext;
import com.zwt.reggie.common.R;
import com.zwt.reggie.entity.Employee;
import com.zwt.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        //加密
        String password = employee.getPassword();
        String password1 = DigestUtils.md5DigestAsHex(password.getBytes());
//        查询username,查询对象
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        //数据库查询获取的信息
        Employee emp = employeeService.getOne(queryWrapper);


        if (emp == null) {
            return R.error("登录失败");
        }
        if (!emp.getPassword().equals(password1)) {
            return R.error("密码错误");
        }
        if (emp.getStatus() == 0) {
            return R.error("账号已禁用");
        }
//        登录成功后，将id存入session并返回登陆成功结果
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        //清除session中保存的员工id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        // 当前记录创建时间
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
        //获用户登录id
        Long empId = (Long) request.getSession().getAttribute("employee");
        //创建该用户的id
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);
        employeeService.save(employee);
        return R.success("添加用户信息成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {

        log.info("page:{},pageSize:{},name:{}", page, pageSize, name);

        //构造分页构造器
        Page pageInfo = new Page(page, pageSize);

        //条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();

        //添加过滤条件,模糊查询
//        if (name!=null)
//        queryWrapper.like(Employee::getName,name);
        queryWrapper.like(StringUtils.hasText(name), Employee::getName, name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //查询
        employeeService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        Long empId = (Long) request.getSession().getAttribute("employee");
        //已设置公共字段填充
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(empId);
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        if (employee != null) {
            return R.success(employee);
        } else
            return R.error("没有查询到用户信息");
    }


}
