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

    public static void main(String[] args) throws IOException {
        String str = "{\"name\":\"com.shangpin.http.ProxyException\",\"msg\":\"测试是否成功\"}";
        try {
            isExistClass(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Class<?> isExistClass(String error) throws Exception{
        Class<?> cl = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(error);
            cl = Class.forName(jsonNode.get("name").textValue());
            Class[] params = {String.class};
            Constructor constructor = cl.getConstructor(params);//找到异常类中带有一个String参数的 构造函数
            Throwable thowable = (Throwable)constructor.newInstance(new Object[]{jsonNode.get("msg").textValue()});
            //String result = (String)cl.getDeclaredMethod("printMsg").invoke(thowable);
            //assert !"1测试是123123否成功".equals(result);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return cl;
    }

}
