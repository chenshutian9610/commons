package org.tree.commons.utils;

import java.util.Collection;
import java.util.StringJoiner;
import java.util.function.Function;

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


    /**
     * String::join 只能针对 String 对象，这里相当于对 join 的扩展
     * 可以对对象中的某一字段进行 join，也可以普通地对 String 等原始类型进行 join（原始类型指的是包装类）
     *
     * @param separator
     * @param objects
     * @param transition 转换规则，可选参数，不使用则使用对象的 toString 进行 join
     * @param <T>
     * @return
     */
    public static <T> String join(String separator, T[] objects, Function<T, String>... transition) {
        StringJoiner joiner = new StringJoiner(separator);
        for (T object : objects)
            joiner.add(transition.length == 0 ? object.toString() : transition[0].apply(object));
        return joiner.toString();
    }

    public static <T> String join(String separator, Collection<T> objects, Function<T, String>... transition) {
        StringJoiner joiner = new StringJoiner(separator);
        for (T object : objects)
            joiner.add(transition.length == 0 ? object.toString() : transition[0].apply(object));
        return joiner.toString();
    }
}
