package org.tree.commons.support.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author er_dong_chen
 * @date 2018/10/31
 */
@Lazy
@Repository
public class IntegratedMapper {

    @Autowired
    private MapperMap map;

    public <T> T selectForFirst(Example<T> example) {
        List<T> result = selectByExample(example);
        return result.size() == 0 ? null : result.get(0);
    }

    public <T> T querySelectiveForFirst(Args<T> args, Example<T> example) {
        List<T> result = getMapperByExample(example).querySelective(args, example);
        return result.size() == 0 ? null : result.get(0);
    }

    public <T> List<T> querySelective(Args<T> args, Example<T> example) {
        return getMapperByExample(example).querySelective(args, example);
    }

    public <T> int insertSelectiveWithGeneratedKey(T record) {
        return getMapper(record).insertSelectiveWithGeneratedKey(record);
    }

    public <T> int insertBatchSelective(List<T> list) {
        if (list == null || list.size() == 0)
            return 0;
        BaseMapper baseMapper = getMapper(list.get(0).getClass());
        return baseMapper.insertBatchSelective(list);
    }

    /* mybatis 标配的接口方法 */

    public <T> int deleteByPrimaryKey(Class<T> clazz, Long id) {
        return getMapper(clazz).deleteByPrimaryKey(id);
    }

    public <T> int insert(T record) {
        return getMapper(record).insert(record);
    }

    public <T> int insertSelective(T record) {
        return getMapper(record).insertSelective(record);
    }

    public <T> T selectByPrimaryKey(Class<T> clazz, Long id) {
        return getMapper(clazz).selectByPrimaryKey(id);
    }

    public <T> int updateByPrimaryKeySelective(T record) {
        return getMapper(record).updateByPrimaryKeySelective(record);
    }

    public <T> int updateByPrimaryKey(T record) {
        return getMapper(record).updateByPrimaryKey(record);
    }

    public <T> long countByExample(Example<T> example) {
        return getMapperByExample(example).countByExample(example);
    }

    public <T> int deleteByExample(Example<T> example) {
        return getMapperByExample(example).deleteByExample(example);
    }

    public <T> int updateByExampleSelective(T record, Example<T> example) {
        return getMapperByExample(example).updateByExampleSelective(record, example);
    }

    public <T> int updateByExample(T record, Example<T> example) {
        return getMapperByExample(example).updateByExample(record, example);
    }

    public <T> List<T> selectByExample(Example<T> example) {
        return getMapperByExample(example).selectByExample(example);
    }

    /****************************** 私有方法 *******************************/

    /* 获取 BaseMapper */
    private <T> BaseMapper<T> getMapper(Class<T> clazz) {
        String[] array = clazz.getName().split("\\.");
        String domain = array[array.length - 1];
        BaseMapper<T> baseMapper = map.get(domain);
        return baseMapper;
    }

    /* 获取 BaseMapper */
    private <T> BaseMapper<T> getMapper(T obj) {
        String[] array = obj.getClass().getName().split("\\.");
        String domain = array[array.length - 1];
        BaseMapper<T> baseMapper = map.get(domain);
        return baseMapper;
    }

    /* 获取 BaseMapper */
    private <T> BaseMapper<T> getMapperByExample(Example<T> example) {
        String exampleName = example.getClass().getSimpleName();
        BaseMapper mapper = map.get(exampleName.substring(0, exampleName.indexOf("Example")));
        return mapper;
    }
}