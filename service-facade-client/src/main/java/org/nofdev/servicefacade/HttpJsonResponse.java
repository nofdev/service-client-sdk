package org.nofdev.servicefacade;

import org.nofdev.http.ExceptionMessage;

import java.io.Serializable;

/**
 * Created by Qiang on 7/30/14.
 */
//@JsonTypeInfo(use=JsonTypeInfo.Id.NONE, include=JsonTypeInfo.As.WRAPPER_OBJECT)
public class HttpJsonResponse<T> implements Serializable{
    private static final long serialVersionUID = 5393610697317077173L;

    private T val;
    private String callId;
    private ExceptionMessage err;

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public T getVal() {
        return val;
    }

    public void setVal(T val) {
        this.val = val;
    }

    public ExceptionMessage getErr() {
        return err;
    }

    public void setErr(ExceptionMessage err) {
        this.err = err;
    }
}
