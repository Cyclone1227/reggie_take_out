package com.zwt.reggie.common;

//获取用户当前保存的id
public class BaseContext {

    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();
     public static void setCurrentId(Long id){
         threadLocal.set(id);
    }

    static Long getCurrentId(){
         return threadLocal.get();
    }
}
