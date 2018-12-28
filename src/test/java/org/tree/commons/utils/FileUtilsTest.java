package org.tree.commons.utils;

import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtilsTest {
    @Test
    public void testListFiles() {
        List<File> files = FileUtils.listFiles(new File(System.getProperty("user.dir")), new ArrayList<>());
        files.forEach(System.err::println);
    }
}