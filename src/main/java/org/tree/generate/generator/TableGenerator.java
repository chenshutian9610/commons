package org.tree.generate.generator;

import org.tree.common.utils.PropertiesUtils;
import org.tree.generate.annotation.Column;
import org.tree.generate.annotation.Table;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * @author er_dong_chen
 * @date 2018/12/11
 */
public class TableGenerator {
    private static Map<String, String> tableMap = new HashMap<>();

    public static Map<String, String> getTableMap(String packageToScan) throws Exception {
        return getTableMap(packageToScan, "");
    }

    public static Map<String, String> getTableMap(String packageToScan, String module) throws Exception {
        if (tableMap.size() == 0)
            _generate(packageToScan, module, false);
        return tableMap;
    }

    public static void generate(String packageToScan) throws Exception {
        _generate(packageToScan, "", true);
    }

    public static void generate(String packageToScan, String module) throws Exception {
        _generate(packageToScan, module, true);
    }

    private static void _generate(String packageToScan, String module, boolean generate) throws Exception {
        String path = "./" + module + "/src/main/java/" + packageToScan.replace(".", "/");
        File dir = new File(path);
        if (!dir.exists())
            throw new Exception(String.format("路径 %s 不存在", dir.getCanonicalPath()));
        String[] fileNames = dir.list();
        Class clazz;
        Field[] fields;
        Table table;
        Column column;
        String columnName, type, comment, defaultValue, unique, id, columnDefinition;
        int length = 40;
        List<String> scripts = new ArrayList<>();
        for (String name : fileNames) {
            List<String> columnDefinitions = new ArrayList<>();
            StringBuilder ddl = new StringBuilder();
            name = name.substring(0, name.indexOf("."));
            clazz = Class.forName(packageToScan + "." + name);
            fields = clazz.getDeclaredFields();
            table = (Table) clazz.getAnnotation(Table.class);
            if (table == null) continue;
            String drop = String.format("DROP TABLE IF EXISTS %s;", table.name());
            ddl.append(String.format("CREATE TABLE %s (\n", table.name()));
            for (Field field : fields) {
                column = (Column) field.getAnnotation(Column.class);
                columnDefinitions.add(_getColumnDefinition(field, column).trim());
            }
            ddl.append(String.join(",\n", columnDefinitions));
            ddl.append(String.format("\n) %s COMMENT = '%s';", table.meta(), table.comment()));
            System.out.println(drop + "\n" + new String(ddl) + "\n");

            if (generate) {
                scripts.add(drop);
                scripts.add(new String(ddl));
            }

            tableMap.put(name, table.name());
        }

        try {
            if (generate)
                _execute(scripts);
        } catch (SQLException e) {
            System.err.println("------------------");
            System.out.println(e.getMessage());
            System.err.println("------------------");
        }
    }

    private static String _getColumnDefinition(Field field, Column column) {
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
        }

        if (id.length() != 0 || unique.length() != 0)
            defaultValue = "";

        return String.format("\t%s %s %s %s %s %s", name, type, defaultValue, unique, id, comment);
    }

    private static void _execute(List<String> scripts) throws IOException, ClassNotFoundException, SQLException {
        Properties properties = PropertiesUtils.getProperties("conf.properties");
        Class.forName(properties.getProperty("jdbc.driver"));
        String url = properties.getProperty("jdbc.url");
        String username = properties.getProperty("jdbc.username");
        String password = properties.getProperty("jdbc.password");
        Connection connection = DriverManager.getConnection(url, username, password);
        Statement statement = connection.createStatement();
        for (String script : scripts)
            statement.execute(script);
    }

    public static void main(String args[]) throws Exception{
        Properties properties = PropertiesUtils.getProperties("conf.properties");
        _generate(properties.getProperty("packageToScan"), "", false);
    }
}
