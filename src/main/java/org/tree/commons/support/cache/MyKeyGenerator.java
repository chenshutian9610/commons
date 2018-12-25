package org.tree.commons.support.cache;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

/**
 * @author er_dong_chen
 * @date 2018/12/25
 */
@Component("myKeyGenerator")
public class MyKeyGenerator implements KeyGenerator {
    public static final String NO_PARAM_KEY = "(none)";
    public static final String NULL_PARAM_KEY = "(null)";

    @Override
    public Object generate(Object target, Method method, Object... params) {
        StringBuilder key = new StringBuilder();
        key.append(target.getClass().getSimpleName()).append(".").append(method.getName()).append(":");
        if (params.length == 0) {
            return key.append(NO_PARAM_KEY).toString();
        }
        for (Object param : params) {
            if (param == null) {
                key.append(NULL_PARAM_KEY);
            } else if (ClassUtils.isPrimitiveArray(param.getClass())) {
                int length = Array.getLength(param);
                for (int i = 0; i < length; i++) {
                    key.append('(').append(Array.get(param, i)).append(',').append(')');
                }
            } else if (ClassUtils.isPrimitiveOrWrapper(param.getClass()) || param instanceof String) {
                key.append(param);
            } else key.append(param.hashCode());
            key.append('-');
        }

        String finalKey = key.toString();
        System.out.println("-------- using cache key={}" + finalKey);
        return finalKey;
    }
}
