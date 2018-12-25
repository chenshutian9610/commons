package org.tree.commons.generate.generator;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.tree.commons.generate.annotation.Column;
import org.tree.commons.generate.annotation.Table;
import org.tree.commons.utils.PropertiesUtils;
import org.tree.commons.utils.StringUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * @author er_dong_chen
 * @date 2018/12/11
 * <p>
 * 可配置参数有 jdbc.driver, jdbc.url, jdbc.username, jdbc.password 和 packageToScan
 */
public class TableGenerator {
    private Map<String, String> tableMap = new HashMap<>();
    private List<String> scripts = new ArrayList<>();
    private Properties properties;

    public TableGenerator() throws Exception {
        properties = PropertiesUtils.getProperties("generator.properties");
        _init(properties.getProperty("packageToScan"));
    }

    public TableGenerator(String propertiesFile) throws Exception {
        properties = PropertiesUtils.getProperties(propertiesFile);
        _init(properties.getProperty("packageToScan"));
    }

    public void setPackageToScan(String packageToScan) throws Exception {
        _init(packageToScan);
    }

    public Map<String, String> getTableMap() {
        return tableMap;
    }

    public void generate() throws Exception {
        if (scripts.size() == 0)
            return;
        _execute(scripts);
    }

    private void _init(String packageToScan) throws Exception {
        if (packageToScan == null)
            return;
        scripts.clear();
        tableMap.clear();
        Resource resource = new ClassPathResource(packageToScan.replace(".", "/"));
        File dir = resource.getFile();
        if (!dir.exists())
            throw new Exception(String.format("路径 %s 不存在", dir.getCanonicalPath()));
        String[] fileNames = dir.list();
        Class clazz;
        Field[] fields;
        Table table;
        Column column;
        String tableName, columnName, type, comment, defaultValue, unique, id, columnDefinition;
        int length = 40;
        for (String name : fileNames) {
            List<String> columnDefinitions = new ArrayList<>();
            StringBuilder ddl = new StringBuilder();
            name = name.substring(0, name.indexOf("."));
            clazz = Class.forName(packageToScan + "." + name);
            fields = clazz.getDeclaredFields();
            table = (Table) clazz.getAnnotation(Table.class);
            if (table == null) continue;
            tableName = table.name().length() == 0 ? StringUtils.format(name, false) : table.name();
            for (Field field : fields) {
                column = (Column) field.getAnnotation(Column.class);
                columnDefinitions.add(_getColumnDefinition(field, column).trim());
            }

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
    }

    private String _getColumnDefinition(Field field, Column column) {
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

    private void _execute(List<String> scripts) throws ClassNotFoundException, SQLException {
        Class.forName(properties.getProperty("jdbc.driver"));
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
}
