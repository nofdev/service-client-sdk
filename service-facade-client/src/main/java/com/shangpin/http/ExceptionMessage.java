package com.shangpin.http;

/**
 * Created by Qiang on 7/31/14.
 */
public class ExceptionMessage {
    private String name;
    private String msg;
    private ExceptionMessage cause;
    private String stack;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getStack() {
        return stack;
    }

    public void setStack(String stack) {
        this.stack = stack;
    }

    public ExceptionMessage getCause() {
        return cause;
    }

    public void setCause(ExceptionMessage cause) {
        this.cause = cause;
    }
}
