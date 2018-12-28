package org.tree.commons.utils;

import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author er_dong_chen
 * @date 2018/12/28
 */
public class PackageUtils {
    public static List<Class> scan(String packageToScan) throws IOException, ClassNotFoundException {
        File dir = new ClassPathResource(packageToScan.replace(".", "/")).getFile();
        List<String> list = new ArrayList<>();
        scan0(dir, packageToScan, list);
        List<Class> clazzList = new ArrayList<>();
        for (String str : list)
            clazzList.add(Class.forName(str));
        return clazzList;
    }

    private static void scan0(File dir, String packageToScan, List<String> clazzList) {
        String packageName = packageToScan;
        if (dir.isDirectory()) {
            if (!packageName.endsWith(dir.getName()))
                packageName = packageName + "." + dir.getName();
            File[] files = dir.listFiles();
            for (File file : files)
                scan0(file, packageName, clazzList);
            return;
        }
        clazzList.add(packageName + "." + dir.getName().substring(0, dir.getName().indexOf(".")));
        return;
    }
}
