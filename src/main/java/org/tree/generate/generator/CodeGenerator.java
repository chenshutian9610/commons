package org.tree.generate.generator;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.ModelType;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author er_dong_chen
 * @date 18-12-11
 */
public class CodeGenerator {
    public static void generate(String configFile, Map<String, String> tableMap) throws Exception {
        URL url = CodeGenerator.class.getClassLoader().getResource(configFile);
        File file = new File(url.getPath());
        List<String> warnings = new ArrayList<String>();
        Configuration config = new ConfigurationParser(warnings).parseConfiguration(file);
        DefaultShellCallback callback = new DefaultShellCallback(true);
        MyBatisGenerator generator = new MyBatisGenerator(config, callback, warnings);
        generator.setXmlMerge(false);
        generator.generate(_getTableConfigurations(tableMap));
    }

    /* 获取 TableConfiguration 列表 */
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
}
