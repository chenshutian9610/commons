package org.tree.commons.utils;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

/**
 * @author er_dong_chen
 * @date 2019/1/27
 */
public class MapUtils {
    /* 将映射转为对象 */
    public static <T> T toObject(Map<?, ?> map, Class<T> clazz) {
        return JSON.parseObject(JSON.toJSONString(map), clazz);
    }

    /* 将对象转为映射 */
    public static Map toMap(Object object) {
        return JSON.parseObject(JSON.toJSONString(object), Map.class);
    }

    /* 通过内部类构建一个映射 */
    public static <K, V> InnerMap<K, V> put(K key, V value) {
        return new InnerMap<>(key, value);
    }

    public static InnerMap<String, Object> stringKeys() {
        return new InnerMap<>();
    }

    /* 构建一个 Map<K, V> 对象 */
    public static class InnerMap<K, V> {
        private Map<K, V> map = new HashMap<>();

        public InnerMap() {
        }

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
