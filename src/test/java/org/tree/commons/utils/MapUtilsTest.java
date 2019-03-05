package org.tree.commons.utils;

import org.testng.annotations.Test;

import java.util.Map;

public class MapUtilsTest {

    @Test
    public void testPut() {
        Map<String, String> greet = MapUtils.put("china", "nihao").put("america", "hello").build();
        greet.forEach((k, v) -> System.out.printf("country : %s, greeting: %s%n", k, v));
    }
}