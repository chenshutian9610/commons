package org.tree.commons.utils;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class StringUtilsTest {

    @Test
    public void testFormat() {
        String str="hello_world_andHelloChina";
        assertEquals(StringUtils.format(str, true),"HelloWorldAndHelloChina");
        assertEquals(StringUtils.format(str, false),"hello_world_and_hello_china");
    }
}