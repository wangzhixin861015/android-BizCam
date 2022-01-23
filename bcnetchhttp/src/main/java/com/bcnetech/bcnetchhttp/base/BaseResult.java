package com.bcnetech.bcnetchhttp.base;

import java.io.Serializable;

/**
 * Created by yhf on 18/9/13.
 */
public class BaseResult<T> implements Serializable {
    private static final long serialVersionUID = 1336383322394823709L;
    private boolean success;
    private String message;
    private T data;
    private int code;


    public BaseResult() {
    }

    public BaseResult(boolean var1, String var2) {
        this.success = var1;
        this.message = var2;
    }

    public BaseResult(boolean var1, String var2, T var3) {
        this.success = var1;
        this.message = var2;
        this.data=var3;
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
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
                ", value=" + data +
                '}';
    }
}
