package org.tree.commons.support.controller;

import java.util.Map;

/**
 * @author er_dong_chen
 * @date 2019/1/14
 */
public class Result {
    public static final String PARAMETER_MISSING = "参数缺失";
    public static final String PASSWORD_ERROR = "密码错误";
    public static final String AUTH_CODE_ERROR="验证码错误";

    private boolean success;
    private String message;
    private Map<String, ?> response;

    public Result() {
    }

    public Result(boolean success) {
        this.success = success;
    }

    public Result(String message) {
        this.message = message;
    }

    public Result(Map response) {
        this.success = true;
        this.response = response;
    }

    public Result(boolean success, String message, Map<String, ?> response) {
        this.success = success;
        this.message = message;
        this.response = response;
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

    public Map<String, ?> getResponse() {
        return response;
    }
}
