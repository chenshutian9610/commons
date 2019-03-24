package org.tree.commons.support.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @author er_dong_chen
 * @date 2019/3/5
 */
public interface UnionSearchMapper {
    @Select("${queryString}")
    List<Map<?, ?>> query(@Param("queryString") String queryString);


    @Select("${queryString}")
    Long count(@Param("queryString") String queryString);
}
