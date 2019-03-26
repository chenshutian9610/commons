package org.tree.commons.utils;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author er_dong_chen
 * @date 2018/11/4
 */
public class FileUtils {

    /**
     * 在指定目录中寻找指定文件
     * ps: 自动忽略 . 开头的文件, 如 .git
     *
     * @param dir      目录
     * @param name     指定文件的名字
     * @param excludes 忽略目录
     * @return
     */
    public static File findDirectory(File dir, String name, Set<String> excludes) throws IOException {
        System.out.println("经过: " + dir.getCanonicalPath());
        if (dir.getName().startsWith("."))
            return null;
        if (dir.isDirectory() == false)
            return null;
        if (dir.getName().equals(name))
            return dir;
        File[] files = dir.listFiles();
        File result = null;
        for (File file : files) {
            file = findDirectory(file, name, excludes);
            if (file == null)
                continue;
            result = file;
            break;
        }
        return result;
    }

    /**
     * 在指定目录中寻找指定文件
     * ps: 自动忽略 . 开头的文件, 如 .git
     *
     * @param dir      目录
     * @param name     指定文件的名字
     * @param excludes 忽略目录
     * @return
     */
    public static File findFile(File dir, String name, Set<String> excludes) {
        System.out.println("经过: " + dir.getAbsolutePath());
        File result = null;
        if (dir.getName().startsWith("."))
            return null;
        if (excludes.contains(dir.getName()))
            return null;
        if (dir.isDirectory() == true) {
            File[] files = dir.listFiles();
            for (File file : files) {
                file = findFile(file, name, excludes);
                if (file == null)
                    continue;
                result = file;
                break;
            }
        }
        if (dir.getName().equals(name))
            return dir;
        return result;
    }

    /* 递归列出文件名 */
    public static List<String> listFileNames(File dir){
        List<File> files=listFiles(dir,new ArrayList<>());
        List<String> fileNames=new ArrayList<>(files.size());
        for(File file:files)
            fileNames.add(file.getName());
        return fileNames;
    }

    /* 递归列出文件对象 */
    public static List<File> listFiles(File dir, List<File> list) {
        if (dir.isDirectory()) {
            System.out.println(String.format("扫描 %s", dir.getAbsolutePath()));
            File[] files = dir.listFiles();
            for (File file : files)
                list = listFiles(file, list);
            return list;
        }
        list.add(dir);
        return list;
    }

    public static void main(String args[]) throws IOException {
        System.out.println("寻找 controller 目录");
        System.out.println("结果: " + findDirectory(new File(System.getProperty("user.dir")), "controller", new HashSet<>(0)));
        System.out.println();
        System.out.println("寻找 Comment.java 文件");
        Set<String> excludes = new HashSet<>(1);
        Collections.addAll(excludes, "target");
        System.out.println("结果: " + findFile(new File(System.getProperty("user.dir")), "Comment.java", excludes));
    }
}
