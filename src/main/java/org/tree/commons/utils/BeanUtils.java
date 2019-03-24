package org.tree.commons.utils;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * @author er_dong_chen
 * @date 2018/12/17
 */
public class BeanUtils {

    /* 将属性按顺序转化为字符串 */
    public static String[] getPropertiesValue(Object obj) throws IllegalAccessException {
        Class clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        String[] array = new String[fields.length];
        int i = 0;
        for (Field field : fields) {
            field.setAccessible(true);
            Object value = field.get(obj);
            array[i++] = value == null ? "" : String.valueOf(value);
        }
        return array;
    }

    /* 只有全部非空的时候才返回 true */
    public static boolean assertPropertiesNotNull(Object obj, Set<String> excludes) throws IllegalAccessException {
        Class clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        boolean result = true;
        for (Field field : fields) {
            field.setAccessible(true);
            if (excludes.contains(field.getName()))
                continue;
            if (field.get(obj) == null) {
                result = false;
                break;
            }
        }
        return result;
    }

    /* 只有全部为空才返回 true */
    public static boolean assertPropertiesNull(Object obj, Set<String> excludes) throws IllegalAccessException {
        Class clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        boolean result = true;
        for (Field field : fields) {
            field.setAccessible(true);
            if (excludes.contains(field.getName()))
                continue;
            if (field.get(obj) != null) {
                result = false;
                break;
            }
        }
        return result;
    }
}
