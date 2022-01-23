package com.bcnetech.hyphoto.data;

/**
 * Created by wenbin on 16/6/7.
 */
public class HttpBaseData {
    private int state;
    private String msg;

    private Object data;
    private boolean success;
    private String message;
    private int code;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "HttpBaseData{" +
                "state=" + state +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                ", success=" + success +
                ", message='" + message + '\'' +
                ", code=" + code +
                '}';
    }
}
