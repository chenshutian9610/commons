package org.tree.commons.utils;

import java.util.*;
import java.util.function.Function;

/**
 * @author er_dong_chen
 * @date 2019/1/27
 */
public class CollectionUtils {

    /* 返回一个 ArrayList 对象 */
    public static <E> List<E> listOf(E... values) {
        List<E> list = new ArrayList<>(values.length);
        Collections.addAll(list, values);
        return list;
    }

    /* 可以是 HashSet，LinkedList 等的 Collection 子类 */
    public static <E, T extends Collection<E>> T of(Function<Integer, T> function, E... values) {
        T collection = function.apply(values.length);
        Collections.addAll(collection, values);
        return collection;
    }
}
