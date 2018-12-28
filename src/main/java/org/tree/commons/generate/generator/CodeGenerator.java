package org.tree.commons.generate.generator;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.ModelType;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author er_dong_chen
 * @date 18-12-11
 * <p> 需要提供 generator.properties 文件或标准的 mybatis-generator 配置文件
 *
 * <p> generator.properties 可配置参数有
 * <p> jdbc.driver, jdbc.url, jdbc.username, jdbc.password
 * <p> java.target, xml.target, model.package, mapper.package
 */
public class CodeGenerator {

    /* 入口一, 需要 generator.properties */
    public static void generate(Map<String, String> tableMap) throws Exception {
        generate("mybatis-generate.xml", tableMap);
    }

    /* 入口二, 需要 mybatis-generator.xml */
    public static void generate(String mybatisConfig, Map<String, String> tableMap) throws Exception {
        List<String> warnings = new ArrayList<String>();
        Resource resource = new ClassPathResource(mybatisConfig);
        Configuration config = new ConfigurationParser(warnings).parseConfiguration(resource.getFile());
        DefaultShellCallback callback = new DefaultShellCallback(true);
        MyBatisGenerator generator = new MyBatisGenerator(config, callback, warnings);
        generator.setXmlMerge(false);
        generator.generate(_getTableConfigurations(tableMap));
    }

    /* 辅助方法, 用于获取 TableConfiguration 列表 */
    private static List<TableConfiguration> _getTableConfigurations(Map<String, String> map) {
        List<TableConfiguration> tableConfigurations = new ArrayList<>();
        Context context = new Context(ModelType.CONDITIONAL);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            TableConfiguration tc = new TableConfiguration(context);
            tc.setDomainObjectName(entry.getKey());
            tc.setMapperName(entry.getKey() + "Mapper");
            tc.setTableName(entry.getValue());
            tableConfigurations.add(tc);
        }
        return tableConfigurations;
    }

    private static Reader dealExpression(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
        String currentLine, expression;
        int start = 0, end = 0;
        while ((currentLine = reader.readLine()) != null) {
            while ((start = currentLine.indexOf("${", start)) != -1) {
                end = currentLine.indexOf("}", end);
                expression = currentLine.substring(start + 1, end);
                if (expression.contains(":")) {
                    currentLine.replace(String.format("${%s}", expression), expression.substring(expression.indexOf(":") + 1, end));
                }
            }
        }
        return reader;
    }
}
