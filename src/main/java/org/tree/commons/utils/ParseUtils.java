package org.tree.commons.utils;

import java.util.Optional;

/**
 * @author er_dong_chen
 * @date 2019/3/25
 */
public class ParseUtils {

    /* 将字符串转为数字 */
    public static <T> Optional<T> string2number(String value, Class<T> clazz) {
        try {
            if (clazz.equals(Integer.class) || clazz.equals(int.class))
                return (Optional<T>) Optional.of(Integer.parseInt(value));
            if (clazz.equals(Long.class) || clazz.equals(long.class))
                return (Optional<T>) Optional.of(Long.parseLong(value));
            if (clazz.equals(Short.class) || clazz.equals(short.class))
                return (Optional<T>) Optional.of(Short.parseShort(value));
            if (clazz.equals(Byte.class) || clazz.equals(byte.class))
                return (Optional<T>) Optional.of(Byte.parseByte(value));
        } catch (NumberFormatException e) {
        }
        return Optional.empty();
    }

    /* 将数字转为其他类型的数字，如 long2byte 之类的 */
    public static <T> Optional<T> number2number(Object value, Class<T> clazz) {
        return string2number(String.valueOf(value), clazz);
    }
}
