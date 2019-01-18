package org.tree.commons.utils;

import org.tree.commons.utils.security.MD5;

/**
 * @author er_dong_chen
 * @date 2019/1/14
 */
public class PasswordUtils {
    public static String deal(String password) {
        return password.length() == 32 ? password : MD5.getMD5(password);
    }

    public static void main(String[] args) {
        System.out.println(deal("hello world"));
        System.out.println(deal("5eb63bbbe01eeed093cb22bb8f5acdc3"));
    }
}
