package com.pinyougou.entity;

import java.io.Serializable;

public class ReturnResult implements Serializable {
    /**
     * 操作状态
     */
    private boolean success;
    /**
     * 操作返回信息
     */
    private String message;

    public ReturnResult() {
    }

    public ReturnResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

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
}
