package org.tree.commons.utils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author er_dong_chen
 * @date 2019/1/27
 */
public class MapUtils {
    /* 将 Map 转为 T */
    public static <T> T parse(Map<?, ?> map, Class<T> clazz) {
        try {
            Method[] methods = clazz.getDeclaredMethods();
            T obj = (T) clazz.newInstance();
            for (Method method : methods) {
                if (!method.getName().matches("set.*"))
                    continue;
                String name = StringUtils.capital(method.getName().replace("set", ""), false);
                if (map.get(name) != null)
                    method.invoke(obj, map.get(name));
                name = StringUtils.format(name, false);
                if (map.get(name) != null)
                    method.invoke(obj, map.get(name));
            }
            return obj;
        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
    }

    /* 通过内部类构建一个映射 */
    public static <K, V> InnerMap<K, V> put(K key, V value) {
        return new InnerMap<>(key, value);
    }

    /* 构建一个 Map<K, V> 对象 */
    public static class InnerMap<K, V> {
        private Map<K, V> map = new HashMap<>();

        private InnerMap(K key, V value) {
            map.put(key, value);
        }

        public InnerMap<K, V> put(K key, V value) {
            map.put(key, value);
            return this;
        }

        public Map<K, V> build() {
            return map;
        }
    }
}
