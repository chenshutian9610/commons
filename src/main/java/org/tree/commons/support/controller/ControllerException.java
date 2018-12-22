package org.tree.commons.support.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author er_dong_chen
 * @date 2018/12/18
 */
@ControllerAdvice
public class ControllerException {
    @Autowired
    private ControllerConfig config;

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Map deal(Exception e) {
        Map map = new LinkedHashMap(3);
        map.put("code", -1);
        map.put("message", config.isDebugEnable() ? e.getMessage() : "未知错误");
        map.put("success", false);
        return map;
    }
}
