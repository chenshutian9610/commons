package org.tree.commons.support.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.tree.commons.support.BaseConfig;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author er_dong_chen
 * @date 2018/12/18
 */
@ControllerAdvice
public class ControllerException extends BaseConfig {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result deal(Exception e) {
        e.printStackTrace();
        return new Result(false, debugEnable ? e.getMessage() : "未知错误");
    }
}
