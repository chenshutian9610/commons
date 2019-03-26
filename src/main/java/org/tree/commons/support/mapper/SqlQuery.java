package org.tree.commons.support.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author er_dong_chen
 * @date 2019/3/5
 */
@Lazy
@Repository
public class SqlQuery {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public String deal(String sql, Object... params) {
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

    /* 一般用于连表查询，使用的是 spring jdbc */
    public List<Map<String, String>> query(String sql, Object... params) {
        return jdbcTemplate.query(deal(sql, params), (resultSet) -> {
            List<Map<String, String>> result = new ArrayList<>();
            int count = resultSet.getMetaData().getColumnCount();
            ResultSetMetaData metaData = resultSet.getMetaData();
            String key, value;
            /* 只取第一条记录 */
            while (resultSet.next()) {
                Map<String, String> map = new HashMap<>();
                for (int i = 0; i < count; i++) {
                    value = resultSet.getString(i + 1);
                    if (value == null)
                        continue;
                    key = toCamel(metaData.getColumnLabel(i + 1));
                    map.put(key, value);
                }
                result.add(map);
            }
            return result;
        });
    }

    /* 一般用于连表查询，使用的是 spring jdbc */
    public Map<String, String> queryForObject(String sql, Object... params) {
        return jdbcTemplate.query(deal(sql, params), (resultSet) -> {
            Map<String, String> map = new HashMap<>();
            int count = resultSet.getMetaData().getColumnCount();
            ResultSetMetaData metaData = resultSet.getMetaData();
            String key, value;
            /* 只取第一条记录 */
            if (resultSet.next()) {
                for (int i = 0; i < count; i++) {
                    value = resultSet.getString(i + 1);
                    if (value == null)
                        continue;
                    key = toCamel(metaData.getColumnLabel(i + 1));
                    map.put(key, value);
                }
            }
            return map;
        });
    }

    public int execute(String sql, Object... params) {
        return jdbcTemplate.update(deal(sql, params));
    }

    public Number executeAndReturnKey(String sql, Object... params) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update((connection) -> {
            PreparedStatement preparedStatement = connection.prepareStatement(deal(sql, params), PreparedStatement.RETURN_GENERATED_KEYS);
            return preparedStatement;
        }, keyHolder);
        return keyHolder.getKey();
    }

    /****************************** 私有方法 *******************************/

    /* 日期格式转化 */
    private String getDateString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }

    /* 下划线转驼峰 */
    private String toCamel(String str) {
        String[] array = str.split("_");
        StringBuilder sb = new StringBuilder(array[0]);
        for (int i = 1; i < array.length; i++) {
            sb.append(array[i].substring(0, 1).toUpperCase());
            sb.append(array[i].substring(1, array[i].length()));
        }
        return new String(sb);
    }
}
