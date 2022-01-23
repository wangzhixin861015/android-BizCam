package com.bcnetech.hyphoto.task;

import java.io.Serializable;

/**
 * Created by wenbin on 16/5/4.
 */
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1336383322394823709L;
    private boolean success;
    private String message;
    private T value;
    private int code;

    public Result() {
    }

    public Result(boolean var1, String var2) {
        this.success = var1;
        this.message = var2;
    }

    public Result(boolean var1, String var2, T var3) {
        this.success = var1;
        this.message = var2;
        this.value = var3;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public void setSuccess(boolean var1) {
        this.success = var1;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String var1) {
        this.message = var1;
    }

    public T getValue() {
        return this.value;
    }

    public void setValue(T var1) {
        this.value = var1;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "Result{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", value=" + value +
                '}';
    }
}
