package org.tree.commons.support.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * @author er_dong_chen
 * @date 2018/10/31
 */
@Repository
public class MapperTemplate {
    @Autowired
    private MapperMap map;
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
                params[n] = "'" + _getDateString((Date) params[n]) + "'";
            sb.append(array[n] + params[n]);
        }
        if (length != array.length)
            sb.append(array[array.length - 1]);
        return new String(sb);
    }

    public <T> int batchInsert(List<T> list) {
        if (list == null || list.size() == 0)
            return -1;
        BaseMapper baseMapper = _getMapper(list.get(0).getClass());
        return baseMapper.insertBatchSelective(list);
    }

    public <T> T queryForObject(Class<T> clazz, String sql, Object... params) {
        List<T> result = query(clazz, sql, params);
        return result.size() == 0 ? null : result.get(0);
    }

    public <T> List<T> query(Class<T> clazz, String sql, Object... params) {
        BaseMapper baseMapper = _getMapper(clazz);
        sql = deal(sql, params);
        if (baseMapper != null)
            return baseMapper.querySelective(sql);
        else
            return jdbcTemplate.queryForList(sql, clazz);
    }

    public <T> T queryByExampleForObject(Args args, Example<T> example) {
        example.setArgs(args.toString());
        return queryByExampleForObject(example);
    }

    public <T> T queryByExampleForObject(Example<T> example) {
        List<T> result = queryByExample(example);
        if (result == null || result.size() == 0)
            return null;
        return result.get(0);
    }

    public <T> List<T> queryByExample(Args args, Example<T> example) {
        example.setArgs(args.toString());
        return queryByExample(example);
    }

    public <T> List<T> queryByExample(Example<T> example) {
        BaseMapper baseMapper = _getMapperByExample(example);
        if (baseMapper != null)
            return baseMapper.querySelectiveByExample(example);
        return null;
    }

    /* 一般用于连表查询，使用的是 spring jdbc */
    public List<Map<String, String>> query(String sql, Object... params) {
        return jdbcTemplate.query(deal(sql, params), new ResultSetExtractor<List<Map<String, String>>>() {
            @Override
            public List<Map<String, String>> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
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
                        key = _toCamel(metaData.getColumnLabel(i + 1));
                        map.put(key, value);
                    }
                    result.add(map);
                }
                return result;
            }
        });
    }

    /* 一般用于连表查询，使用的是 spring jdbc */
    public Map<String, String> queryForObject(String sql, Object... params) {
        return jdbcTemplate.query(deal(sql, params), new ResultSetExtractor<Map<String, String>>() {
            @Override
            public Map<String, String> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
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
                        key = _toCamel(metaData.getColumnLabel(i + 1));
                        map.put(key, value);
                    }
                }
                return map;
            }
        });
    }

    public int execute(String sql, Object... params) {
        return jdbcTemplate.update(deal(sql, params));
    }

    public Number executeAndReturnKey(String sql, Object... params) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement preparedStatement = connection.prepareStatement(deal(sql, params), PreparedStatement.RETURN_GENERATED_KEYS);
                return preparedStatement;
            }
        }, keyHolder);
        return keyHolder.getKey();
    }

    /****************************** commons *******************************/

    public <T> int deleteByPrimaryKey(Class<T> clazz, Long id) {
        return _getMapper(clazz).deleteByPrimaryKey(id);
    }

    public <T> int insert(T record) {
        return _getMapper(record).insert(record);
    }

    public <T> int insertSelective(T record) {
        return _getMapper(record).insertSelective(record);
    }

    public <T> T selectByPrimaryKey(Class<T> clazz, Long id) {
        return _getMapper(clazz).selectByPrimaryKey(id);
    }

    public <T> int updateByPrimaryKeySelective(T record) {
        return _getMapper(record).updateByPrimaryKeySelective(record);
    }

    public <T> int updateByPrimaryKey(T record) {
        return _getMapper(record).updateByPrimaryKey(record);
    }

    public <T> long countByExample(Example<T> example) {
        return _getMapperByExample(example).countByExample(example);
    }

    public <T> int deleteByExample(Example<T> example) {
        return _getMapperByExample(example).deleteByExample(example);
    }

    public <T> int updateByExampleSelective(T record, Example<T> example) {
        return _getMapperByExample(example).updateByExampleSelective(record, example);
    }

    public <T> int updateByExample(T record, Example<T> example) {
        return _getMapperByExample(example).updateByExample(record, example);
    }

    public <T> T selectByExampleForObject(Example<T> example) {
        List<T> result=selectByExample(example);
        return result.size()==0?null:result.get(0);
    }

    public <T> List<T> selectByExample(Example<T> example) {
        return _getMapperByExample(example).selectByExample(example);
    }

    /****************************** 私有方法 *******************************/

    /* 获取 BaseMapper */
    private <T> BaseMapper<T> _getMapper(Class<T> clazz) {
        String[] array = clazz.getName().split("\\.");
        String domain = array[array.length - 1];
        BaseMapper<T> baseMapper = map.get(domain);
        return baseMapper;
    }

    /* 获取 BaseMapper */
    private <T> BaseMapper<T> _getMapper(T obj) {
        String[] array = obj.getClass().getName().split("\\.");
        String domain = array[array.length - 1];
        BaseMapper<T> baseMapper = map.get(domain);
        return baseMapper;
    }

    /* 获取 BaseMapper */
    private <T> BaseMapper<T> _getMapperByExample(Example<T> example) {
        String exampleName = example.getClass().getSimpleName();
        BaseMapper mapper = map.get(exampleName.substring(0, exampleName.indexOf("Example")));
        return mapper;
    }

    /* 日期格式转化 */
    private String _getDateString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }

    /* 下划线转驼峰 */
    private String _toCamel(String str) {
        String[] array = str.split("_");
        StringBuilder sb = new StringBuilder(array[0]);
        for (int i = 1; i < array.length; i++) {
            sb.append(array[i].substring(0, 1).toUpperCase());
            sb.append(array[i].substring(1, array[i].length()));
        }
        return new String(sb);
    }
}