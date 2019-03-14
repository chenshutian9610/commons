package org.tree.commons.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

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

    /* 返回 supplier 返回的对象，可以是 HashSet，LinkedList 等的 Collection 子类 */
    public static <E, T extends Collection<E>> T of(Supplier<T> supplier, E... values) {
        T collection = supplier.get();
        Collections.addAll(collection, values);
        return collection;
    }
}
