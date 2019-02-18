package com.oreo.base;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @param <T> 实体类，如 {@link User}
 * @param <F> 实体类Example，如 {@link User.Example}
 */
public interface SqlMapper<T, F> {

    long countByExample(F example);

    int deleteByExample(F example);

    int deleteByPrimaryKey(Integer id);

    int insert(T record);

    int insertSelective(T record);

    T selectOnlyOneByExample(F example);

    List<T> selectByExample(F example);

    T selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") T record, @Param("example") F example);

    int updateByExample(@Param("record") T record, @Param("example") F example);

    int updateByPrimaryKeySelective(T record);

    int updateByPrimaryKey(T record);

}
