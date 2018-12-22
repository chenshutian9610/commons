package org.tree.commons.utils;

/**
 * @author er_dong_chen
 * @date 2018/12/22
 */
public class StringUtils {

    /* camel & underline */
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

    /* upper & down */
    public static String capital(String str, boolean upper) {
        String pre = str.substring(0, 1);
        String last = str.substring(1);
        return (upper ? pre.toUpperCase() : pre.toLowerCase()) + last;
    }
}
