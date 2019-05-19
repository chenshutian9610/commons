package org.tree.commons.exception;

/**
 * @author er_dong_chen
 * @date 2019/5/19
 */
public class ErrorMessage extends RuntimeException {
    public ErrorMessage(String msg){
        super(msg);
    }
}
