package com.hjj.common.domain;

import java.io.Serializable;

public class Result implements Serializable {
    private static final long serialVersionUID = 6288374846131788743L;

    private boolean success;
    private String msg;

    public Result(boolean success, String msg) {
        this.success = success;
        this.msg = msg;
    }

    public Result(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
