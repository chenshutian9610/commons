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
    private static Properties properties = PropertiesUtils.getProperties("generator.properties");

    /* 自定义处理规则 */
    public static InputStream deal(String configFile) throws Exception {
        InputStream inputStream = new ClassPathResource(configFile).getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));


        String currentLine, expression, ordinary, value;
        int start = 0, end;
        StringBuilder sb = new StringBuilder();
        String rootPackage = properties.getProperty("generate.root.package");
        String module = properties.getProperty("generate.module");
        while ((currentLine = reader.readLine()) != null) {
            /* 1. 如果 root.package 不为空，则忽略 model.package 和 mapper.package */
            if (StringUtils.isValid(rootPackage)) {
                if (currentLine.contains("${generate.model.package}"))
                    currentLine = currentLine.replace("${generate.model.package}",
                            rootPackage + ".model");
                else if (currentLine.contains("${generate.mapper.package}"))
                    currentLine = currentLine.replace("${generate.mapper.package}",
                            rootPackage + ".mapper");
            }

            /* 2. 如果 generate.module 不为空，则忽略 model.target 等变量 */
            if (StringUtils.isValid(module)) {
                // TODO: 2018/12/29
                if (currentLine.matches(".*\\$\\{generate\\.java\\.target.*\\}.*"))
                    currentLine = currentLine.replaceFirst("\\$\\{generate\\.java\\.target.*\\}",
                            String.format("./%s/src/main/java", module));
                else if(currentLine.matches(".*\\$\\{generate\\.xml\\.target.*\\}.*"))
                    currentLine= currentLine.replaceFirst("\\$\\{generate\\.xml\\.target.*\\}",
                            String.format("./%s/src/main/resources", module));
            }

            /* 3. 处理 ${variable} 和 ${variable:defaultValue} */
            end = 0;
            while ((start = currentLine.indexOf("${", start + 1)) != -1) {
                end = currentLine.indexOf("}", end + 1);
                expression = currentLine.substring(start + 2, end);
                ordinary = String.format("${%s}", expression);
                if (expression.contains(":")) {
                    value = properties.getProperty(expression.substring(0, expression.indexOf(":")));
                    currentLine = currentLine.replace(ordinary, value == null ? expression.substring(expression.indexOf(":") + 1) : value);
                } else {
                    value = properties.getProperty(expression);
                    if (value != null)
                        currentLine = currentLine.replace(ordinary, value);
                    else throw new Exception(expression + " 没有配置");
                }
            }
            sb.append(currentLine).append("\n");
        }

        /* 4. 添加插件配置 */
        String done = new String(sb);
        done = done.replace("<!--generator.define.plugins-->", getPlugins());

        return new ByteArrayInputStream(done.getBytes("utf-8"));
    }

    private static String getPlugins() {
        /* 读取 plugin 配置 */
        String prefix = properties.getProperty("generate.mybatis.plugin.prefix");
        String names = properties.getProperty("generate.mybatis.plugin");
        String otherSupport = properties.getProperty("generate.other-support.plugin");

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
