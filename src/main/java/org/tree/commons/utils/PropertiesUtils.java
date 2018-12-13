package org.tree.commons.utils;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author er_dong_chen
 * @date 18-12-11
 */
public class PropertiesUtils {
    /* 读取指定 properties 文件 */
    public static Properties getProperties(String path) throws IOException {
        Properties properties = new Properties();
        InputStream in = new ClassPathResource(path).getInputStream();
        properties.load(in);
        return properties;
    }
}
