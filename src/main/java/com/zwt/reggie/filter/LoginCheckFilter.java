package com.zwt.reggie.filter;

//检查用户是否登录

import com.alibaba.fastjson.JSON;
import com.zwt.reggie.common.BaseContext;
import com.zwt.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    //路径比较,路径匹配器，支持通配符
    public static final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest  request =  (HttpServletRequest)servletRequest;
        HttpServletResponse response =  (HttpServletResponse) servletResponse;
        //获取本次的URI
        String requestURI = request.getRequestURI();
        log.info("本次获取路径：{}",requestURI);
//        放行的url
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**"
        };
//        2、判断本次请求是否需要处理
        boolean check = check(urls, requestURI);
        if(check){
            log.info("本次请求不处理{}",requestURI);
            filterChain.doFilter(request,response);
            return;
        }
        //判断登录状态，已登录就放行
        if (request.getSession().getAttribute("employee")!=null){
            log.info("获取用户id:{}",request.getSession().getAttribute("employee"));
            //获取id
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request,response);
            return;
        }
        log.info("用户未登录");
//        未登录的话返回登录界面,通过输出流方式向客户端响应数据

        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;

    }

    //        判断本次请求是否放行
    public boolean check(String[] urls,String requestURI){
        for(String url:urls){
            boolean match = pathMatcher.match(url, requestURI);
            if(match){
                return true;
            }
        }
        return false;
    }

}
