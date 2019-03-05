package org.tree.commons.utils;

import java.util.Collection;

/**
 * @author er_dong_chen
 * @date 2018/12/22
 */
public class StringUtils {

    /* 是否有效 */
    public static boolean isValid(String... strings) {
        for (String string : strings)
            if (string == null || string.length() == 0)
                return false;
        return true;
    }

    /* 驼峰或下划线 */
    public static String format(String str, boolean camel) {
        StringBuilder sb = new StringBuilder();
        if (camel) {
            String[] array = str.split("_");
            if (array.length == 1)
                return str;
            sb.append(capital(array[0], true));
            for (int i = 1; i < array.length; i++) {
                sb.append(capital(array[i], true));
            }
        } else {
            char[] chars = str.toCharArray();
            sb.append(Character.toLowerCase(chars[0]));
            for (int i = 1; i < chars.length; i++) {
                if (Character.isUpperCase(chars[i]))
                    sb.append("_");
                sb.append(Character.toLowerCase(chars[i]));
            }
        }
        return new String(sb);
    }

    /* 开头大写或小写 */
    public static String capital(String str, boolean upper) {
        String pre = str.substring(0, 1);
        String last = str.substring(1);
        return (upper ? pre.toUpperCase() : pre.toLowerCase()) + last;
    }

    /* String::join */
    public static String join(String separator, String... str) {
        if (str.length == 0)
            return "";
        StringBuilder sb = new StringBuilder();
        for (String s : str) {
            sb.append(s).append(separator);
        }
        sb.delete(sb.length() - separator.length(), sb.length());
        return sb.toString();
    }

    public static String join(String separator, Collection<String> str) {
        if (str.size() == 0)
            return "";
        StringBuilder sb = new StringBuilder();
        for (String s : str) {
            sb.append(s).append(separator);
        }
        sb.delete(sb.length() - separator.length(), sb.length());
        return sb.toString();
    }
}
