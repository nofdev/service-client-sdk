package com.shangpin.http;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    public static Class<?> isExistClass(String error){
        Class<?> cl = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(error);
            cl = Class.forName(jsonNode.get("name").textValue());
            Class[] params = {String.class};
            Constructor constructor = cl.getConstructor(params);//找到异常类中带有一个String参数的 构造函数
            Throwable thowable = (Throwable)constructor.newInstance(new Object[]{jsonNode.get("msg").textValue()});
        } catch(Exception e) {
            e.printStackTrace();
        }
        return cl;
    }

}
