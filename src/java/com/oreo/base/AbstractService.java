package com.oreo.base;

public abstract class AbstractService<T, F> implements BaseService<T, F> {

    protected abstract <E> SqlMapper<T, E> getSqlMapper();

    @Override
    public T findById(Integer id) throws Exception {
        if (id == null)
            throw new RuntimeException();
        return getSqlMapper().selectByPrimaryKey(id);
    }

    @Override
    public int add(T t) throws Exception {
        if (t == null)
            throw new RuntimeException();
        return getSqlMapper().insertSelective(t);
    }

    @Override
    public int update(T t) throws Exception {
        if (t == null)
            throw new RuntimeException();
        return getSqlMapper().updateByPrimaryKeySelective(t);
    }

}