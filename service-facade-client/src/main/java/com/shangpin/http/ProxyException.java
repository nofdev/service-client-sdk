package com.shangpin.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Qiang on 6/4/14.
 */
public class ProxyException extends RuntimeException{

    public ProxyException(String msg){
        super(msg);

    }

    public String printMsg(){
        return super.getMessage();
    }
}
