package com.shangpin.http;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import sun.security.jca.GetInstance;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuyang on 2014/6/11.
 */
public class ExceptionUtil {

    public static Boolean isExistClass(String name){
        Boolean flag = false;
        try {
            Class<?> cl = Class.forName(name);
            flag = true;
        } catch(Exception e) {}
        return flag;
    }

    public static Throwable getThrowableInstance(String name,String msg){
        try {
            Class<?> cl = Class.forName(name);
            Class[] params = {String.class};
            Constructor constructor = cl.getConstructor(params);//找到异常类中带有一个String参数的 构造函数
            return (Throwable)constructor.newInstance(new Object[]{msg});
        } catch (Exception e) {
            e.printStackTrace();
            return e;
        }
    }
}
