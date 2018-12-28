package org.tree.commons.utils;

import org.testng.annotations.Test;

import java.util.List;

public class PackageUtilsTest {
    @Test
    public void testScan() throws Exception {
        List<Class> classes = PackageUtils.scan(PackageUtils.class.getPackage().getName());
        for (Class clazz : classes)
            System.out.println(clazz.getName());
    }
}