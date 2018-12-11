package org.tree.common.utils;

import org.apache.commons.beanutils.PropertyUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * @author er_dong_chen
 * @date 18-12-11
 */
public class PropertiesUtils {
    /* 读取指定 properties 文件 */
    public static Properties getProperties(String path) throws IOException {
        Properties properties = new Properties();
        properties.load(PropertyUtils.class.getClassLoader().getResourceAsStream(path));
        return properties;
    }
}
