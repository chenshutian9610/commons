package org.tree.commons.support.mapper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author er_dong_chen
 * @date 2018/11/28
 */
public class OldSql {
    private Set<String> columns = new HashSet<>();
    private Set<String> tables = new HashSet<>();
    private Set<String> conditions = new HashSet<>();
    private Set<String> last = new LinkedHashSet<>();

    public OldSql() {
    }

    public OldSql(String tableName) {
        tables.add(tableName);
    }

    public OldSql(Class clazz) {
        tables.add(getTableName(clazz));
    }


    public Set<String> getColumns() {
        return columns;
    }

    public void addColumns(String... columns) {
        Collections.addAll(this.columns, columns);
    }

    public void addColumns(Set<String> columns) {
        this.columns.addAll(columns);
    }

    public OldSql addColumn(String column) {
        columns.add(column);
        return this;
    }

    public OldSql addTable(String table) {
        tables.add(table);
        return this;
    }

    public void addTables(String... tables) {
        Collections.addAll(this.tables, tables);
    }

    public OldSql addCondition(String condition, Object... args) {
        boolean add = true;
        for (Object obj : args) {
            if (obj == null) {
                add = false;
                break;
            }
        }
        if (add) conditions.add("(" + deal(condition, args) + ")");
        return this;
    }

    public OldSql addLast(String last, Object... args) {
        boolean add = true;
        for (Object obj : args) {
            if (obj == null) {
                add = false;
                break;
            }
        }
        if (add) this.last.add(deal(last, args));
        return this;
    }

    public static String getTableName(Class clazz) {
        return toUnderLine(clazz.getSimpleName());
    }

    @Override
    public String toString() {
        if (tables.size() == 0)
            return null;
        String space = " ";
        StringBuilder sb = new StringBuilder("select");
        sb.append(space);
        sb.append(columns.size() == 0 ? "*" : String.join(",", columns));
        sb.append(space);
        sb.append("from");
        sb.append(space);
        sb.append(String.join(",", tables));
        sb.append(space);
        if (conditions.size() == 0)
            return new String(sb);
        sb.append("where");
        sb.append(space);
        sb.append(String.join("and", conditions));
        sb.append(space);
        if (last.size() == 0)
            return new String(sb);
        sb.append(String.join(" ", last));
        sb.append(space);
        return new String(sb);
    }

    /* 预处理 sql 语句 */
    public static String deal(String sql, Object... params) {
        if (params.length == 0)
            return sql;
        String[] array = sql.split("\\?");
        StringBuilder sb = new StringBuilder();
        int length = sql.lastIndexOf('?') == sql.length() - 1 ? array.length : array.length - 1;
        for (int n = 0; n < length; n++) {
            if (params[n] instanceof String)
                params[n] = "'" + params[n] + "'";
            if (params[n] instanceof Date)
                params[n] = "'" + getDateString((Date) params[n]) + "'";
            sb.append(array[n] + params[n]);
        }
        if (length != array.length)
            sb.append(array[array.length - 1]);
        return new String(sb);
    }

    /* 日期格式转化 */
    private static String getDateString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }

    /* 下划线转驼峰 */
    public static String toCamel(String str) {
        String[] array = str.split("_");
        StringBuilder sb = new StringBuilder(array[0]);
        for (int i = 1; i < array.length; i++) {
            sb.append(array[i].substring(0, 1).toUpperCase());
            sb.append(array[i].substring(1, array[i].length()));
        }
        return new String(sb);
    }

    /* 驼峰转下划线 */
    public static String toUnderLine(String str) {
        char[] chars = str.toCharArray();
        StringBuilder sb = new StringBuilder();
        sb.append(Character.toLowerCase(chars[0]));
        for (int i = 1; i < chars.length; i++) {
            if (Character.isUpperCase(chars[i]))
                sb.append('_');
            sb.append(Character.toLowerCase(chars[i]));
        }
        return new String(sb);
    }

    public static String in(List list) {
        if (list.size() == 0)
            return "(-1)";
        StringBuilder sb = new StringBuilder();
        for (Object object : list) {
            if (object instanceof String)
                sb.append("'" + object + "',");
            else if (object instanceof Date)
                sb.append("'" + getDateString((Date) object) + "',");
            else
                sb.append(object + ",");
        }
        sb.delete(sb.length() - 1, sb.length());
        return "(" + new String(sb) + ")";
    }

    public static String in(List list, Object exclude) {
        if (list.size() == 0)
            return "(-1)";
        StringBuilder sb = new StringBuilder();
        for (Object object : list) {
            if (object.equals(exclude))
                continue;
            if (object instanceof String)
                sb.append("'" + object + "',");
            else if (object instanceof Date)
                sb.append("'" + getDateString((Date) object) + "',");
            else
                sb.append(object + ",");
        }
        if (sb.length() == 0)
            return "(-1)";
        sb.delete(sb.length() - 1, sb.length());
        return "(" + new String(sb) + ")";
    }
}
