package org.tree.commons.generate.generator;

import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.ModelType;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.tree.commons.generate.annotation.Column;
import org.tree.commons.generate.annotation.Table;
import org.tree.commons.utils.MybatisXmlUtils;
import org.tree.commons.utils.PackageUtils;
import org.tree.commons.utils.PropertiesUtils;
import org.tree.commons.utils.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * @author er_dong_chen
 * @date 2018/12/28
 */
public class TableGenerator {
    /**
     * 默认的配置文件名
     * mybatis-generate.xml 可改
     * generator.properties 必须有
     */
    private static final String CONFIG_PROPERTIES = "generator.properties";
    private static final String CONFIG_XML = "mybatis-generate.xml";

    private Properties properties;
    private Map<String, String> tableMap;
    private List<String> scripts = new ArrayList<>();

    public TableGenerator(String packageToScan) throws Exception {
        tableMap = scanInit(packageToScan);
        properties = PropertiesUtils.getProperties(CONFIG_PROPERTIES);
    }

    /* 正向工程 */
    public void forward() throws ClassNotFoundException, SQLException {
        if (scripts.size() == 0)
            return;

        String driver = properties.getProperty("jdbc.driver");
        if (driver == null)
            driver = "com.mysql.jdbc.Driver";

        Class.forName(driver);
        String url = properties.getProperty("jdbc.url");
        String username = properties.getProperty("jdbc.username");
        String password = properties.getProperty("jdbc.password");
        Connection connection = DriverManager.getConnection(url, username, password);
        Statement statement = connection.createStatement();
        for (String script : scripts) {
            System.out.println(script + "\n");
            statement.execute(script);
        }
    }

    /* 逆向工程一号入口 */
    public void reverse() throws Exception {
        reverse(CONFIG_XML);
    }

    /* 逆向工程二号入口 */
    public void reverse(String mybatisConfig) throws Exception {
        if (tableMap.size() == 0)
            return;

        List<String> warnings = new ArrayList<String>();
        InputStream in = MybatisXmlUtils.deal(mybatisConfig);
        Configuration config = new ConfigurationParser(warnings).parseConfiguration(in);
        DefaultShellCallback callback = new DefaultShellCallback(true);

        /* 复制自 org.mybatis.generator.api.MyBatisGenerator，扩展了 xml 融合和表生成策略 */
        MyBatisGeneratorExtension generator = new MyBatisGeneratorExtension(config, callback, warnings);
        generator.setXmlMerge(false);
        generator.generate(getTableConfigurations(tableMap));
    }

    /* 初始化 tableMap 和 scripts */
    private Map<String, String> scanInit(String packageToScan) throws Exception {
        if (packageToScan == null)
            throw new Exception("packageToScan 无效！");

        Field[] fields;
        Table table;
        Column column;
        String tableName;
        Set<String> keyWords = getKeyWords();
        List<Class> clazzList = PackageUtils.scan(packageToScan);
        Map<String, String> tableMap = new HashMap<>();
        for (Class clazz : clazzList) {
            String name = clazz.getSimpleName();
            List<String> columnDefinitions = new ArrayList<>();
            StringBuilder ddl = new StringBuilder();
            fields = clazz.getDeclaredFields();
            table = (Table) clazz.getAnnotation(Table.class);
            if (table == null) continue;
            tableName = table.name().length() == 0 ? StringUtils.format(name, false) : table.name();
            for (Field field : fields) {
                column = (Column) field.getAnnotation(Column.class);
                if (keyWords.contains(field.getName().toUpperCase()))
                    System.err.println(String.format("%s#%s 是关键字或保留字, 可能导致创建表不成功", name, field.getName()));
                columnDefinitions.add(getColumnDefinition(field, column).trim());
            }
            System.out.println();

            if (table.generate()) {
                String drop = String.format("DROP TABLE IF EXISTS %s;", tableName);
                ddl.append(String.format("CREATE TABLE %s (\n", tableName));
                ddl.append(String.join(",\n", columnDefinitions));
                ddl.append(String.format("\n) %s COMMENT = '%s';", table.meta(), table.comment()));

                scripts.add(drop);
                scripts.add(new String(ddl));
            }

            tableMap.put(name, tableName);
        }
        return tableMap;
    }

    /* 读取 key-word.txt，获取 mysql 关键字（5.7） */
    private Set<String> getKeyWords() throws IOException {
        Resource resource = new ClassPathResource("key-word.txt");
        Scanner scanner = new Scanner(resource.getFile());
        Set<String> keyWords = new HashSet<>(666);
        while (scanner.hasNext())
            keyWords.add(scanner.next());
        return keyWords;
    }

    /* 获取字段定义信息 */
    private String getColumnDefinition(Field field, Column column) {
        String name = field.getName();
        String type = field.getType().getSimpleName();
        String defaultValue = "", comment = "", id = "", unique = "";
        int length = 40;

        if (column != null) {
            defaultValue = column.defaultValue();
            length = column.length();
            if (column.comment().length() != 0)
                comment = String.format("COMMENT '%s'", column.comment());
            if (column.id())
                id = "PRIMARY KEY" + (column.autoIncrement() ? " AUTO_INCREMENT" : "");
            if (column.unique())
                unique = "UNIQUE KEY";
        }

        switch (type.toLowerCase()) {
            case "long":
                type = "BIGINT";
                defaultValue = "DEFAULT '0'";
                break;
            case "int":
            case "integer":
                type = "INTEGER";
                defaultValue = "DEFAULT '0'";
                break;
            case "byte":
                type = "TINYINT";
                defaultValue = "DEFAULT '0'";
                break;
            case "boolean":
                type = "BIT";
                break;
            case "string":
                type = String.format("VARCHAR(%d)", length);
                defaultValue = String.format("DEFAULT '%s'", defaultValue);
                break;
            case "date":
                type = "DATETIME";
                break;
            case "char":
            case "character":
                type = "CHAR(1)";
                defaultValue = String.format("DEFAULT '%s'", defaultValue);
                break;
            case "bigdecimal":
                type = "DECIMAL(19,2)";
                defaultValue = String.format("DEFAULT '0'");
        }

        if (id.length() != 0 || unique.length() != 0)
            defaultValue = "";

        return String.format("\t%s %s %s %s %s %s", name, type, defaultValue, unique, id, comment);
    }

    /* 辅助方法, 用于获取 TableConfiguration 列表 */
    private List<TableConfiguration> getTableConfigurations(Map<String, String> map) {
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
