package com.oreo.base;

import java.util.List;

/**
 * @param <T> 实体类，如 {@link User}
 * @param <F> 实体类Condition，如 {@link User.Condition}
 */
public interface BaseService<T, F> {

    T findById(Integer id) throws Exception;

    List<T> findByCondition(F condition, Integer start, Integer end) throws Exception;

    long countByCondition(F condition) throws Exception;

    int add(T t) throws Exception;

    int update(T t) throws Exception;

    int delete(Integer id) throws Exception;

}
