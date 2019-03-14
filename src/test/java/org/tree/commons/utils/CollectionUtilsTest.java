package org.tree.commons.utils;

import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CollectionUtilsTest {

    @Test
    public void testOf() {
        List<Integer> list = CollectionUtils.listOf(1, 3, 5, 7, 9);
        list.forEach(System.out::println);

        Set<Integer> set = CollectionUtils.of(HashSet::new, 100, 200, 300, 400);
        set.forEach(System.out::println);
    }

}