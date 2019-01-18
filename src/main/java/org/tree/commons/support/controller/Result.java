package org.tree.commons.support.controller;

import java.util.HashMap;
import java.util.Map;

/**
 * @author er_dong_chen
 * @date 2019/1/14
 */
public class Result {
    private boolean success;
    private String message;
    private Map response = new HashMap();

    public Result() {
    }

    public Result(boolean success) {
        this.success = success;
    }

    public Result(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public Result(boolean success, String message, Map response) {
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

    public Map getResponse() {
        return response;
    }

    public void addResponseData(String key, Object response) {
        this.response.put(key, response);
    }
}
