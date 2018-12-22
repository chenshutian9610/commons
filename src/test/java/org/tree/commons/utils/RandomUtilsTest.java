package org.tree.commons.utils;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class RandomUtilsTest {

    @Test
    public void testNumber() {
        System.out.println(RandomUtils.number(4));
        System.out.println(RandomUtils.number(4));
        System.out.println(RandomUtils.number(4));
        System.out.println(RandomUtils.number(4));

        System.out.println();

        System.out.println(RandomUtils.number(6));
        System.out.println(RandomUtils.number(6));
        System.out.println(RandomUtils.number(6));
        System.out.println(RandomUtils.number(6));
    }
}