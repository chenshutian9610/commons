package org.tree.commons.utils;

import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * @author er_dong_chen
 * @date 2018/12/26
 */
public class MybatisXmlUtils {
    /* 自定义处理规则 */
    public static InputStream deal(String configFile) throws IOException {
        InputStream inputStream = new ClassPathResource(configFile).getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));

        /* 1. 处理 ${name:defaultValue} 为 defaultValue */
        String currentLine, expression, ordinary;
        int start = 0, end;
        StringBuilder sb = new StringBuilder();
        while ((currentLine = reader.readLine()) != null) {
            end = 0;
            while ((start = currentLine.indexOf("${", start + 1)) != -1) {
                end = currentLine.indexOf("}", end + 1);
                expression = currentLine.substring(start + 2, end);
                ordinary = String.format("${%s}", expression);
                if (expression.contains(":"))
                    currentLine = currentLine.replace(ordinary, expression.substring(expression.indexOf(":") + 1));

            }
            sb.append(currentLine).append("\n");
        }

        /* 2. 添加插件配置 */
        String done = new String(sb);
        done = done.replace("<!--generator.define.plugins-->", getPlugins());

        return new ByteArrayInputStream(done.getBytes("utf-8"));
    }

    private static String getPlugins() {
        /* 读取 plugin 配置 */
        Properties properties = PropertiesUtils.getProperties("generator.properties");
        String prefix = properties.getProperty("mybatis.plugin.prefix");
        String names = properties.getProperty("mybatis.plugin.name");
        String otherSupport = properties.getProperty("other-support.plugin");

        /* 转化为 plugin list */
        List<String> plugins = new LinkedList<>();
        if (StringUtils.isValid(names)) {
            String[] temps = names.split(",");
            for (String temp : temps)
                plugins.add((StringUtils.isValid(prefix) ? prefix : "org.mybatis.generator.plugins") + "." + temp);
        }
        if (StringUtils.isValid(otherSupport)) {
            String[] temps = otherSupport.split(",");
            for (String temp : temps)
                plugins.add(temp);
        }

        /* 转化为有效格式 */
        StringBuffer sb = new StringBuffer();
        String template = "<plugin type=\"%s\"/>";
        for (String plugin : plugins)
            sb.append(String.format(template, plugin)).append('\n');
        return new String(sb);
    }
}
