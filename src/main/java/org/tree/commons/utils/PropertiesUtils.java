package org.tree.commons.utils;

import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author er_dong_chen
 * @date 18-12-11
 */
public class PropertiesUtils {
    /* 读取指定 properties 文件 */
    public static Properties getProperties(String path) {
        Properties properties = new Properties();
        try{
            InputStream in = new ClassPathResource(path).getInputStream();
            properties.load(in);
        }catch (IOException e){
        }
        return properties;
    }

    public static File getFile(String path) {
        try {
            return new ClassPathResource(path).getFile();
        } catch (IOException e) {
            return null;
        }
    }
}
