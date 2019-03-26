package org.tree.commons.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author er_dong_chen
 * @date 2019/3/4
 */
public class BucketUtils {

    public static InnerBucket<String, Object> stringKeys() {
        return new InnerBucket<>();
    }

    public static <K, V> InnerBucket<K, V> put(K key, V value) {
        return new InnerBucket<>(key, value);
    }


    /****************************** 内部类：构造桶的中间类 *******************************/

    public static class InnerBucket<K, V> {
        private Map<K, List<V>> bucket = new HashMap<>();

        public InnerBucket() {
        }

        private InnerBucket(K key, V value) {
            bucket.put(key, CollectionUtils.listOf(value));
        }

        public InnerBucket<K, V> add(K key, V value) {
            if (bucket.get(key) == null) {
                bucket.put(key, CollectionUtils.listOf(value));
                return this;
            }
            bucket.get(key).add(value);
            return this;
        }

        public Map<K, List<V>> build() {
            return bucket;
        }
    }
}
