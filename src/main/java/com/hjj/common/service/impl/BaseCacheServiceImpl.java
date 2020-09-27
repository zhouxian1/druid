package com.hjj.common.service.impl;

import com.dbs.common.domain.CommonContent;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by zzy on 2015/10/1 0001.
 */
@Service
public abstract class BaseCacheServiceImpl<T> extends BaseServiceImpl<T> {

    protected final String CACHE_KEY = "#root.targetClass";

    @Override
    @Cacheable(value =  CommonContent.CACHE_NAMES, key = CACHE_KEY)
    public List selectAll() throws Exception {
        return super.selectAll();
    }

    @Override
    @CacheEvict(value =  CommonContent.CACHE_NAMES, key = CACHE_KEY)
    public int deleteByPrimaryKey(Object key) throws Exception {
        return super.deleteByPrimaryKey(key);
    }

    @Override
    @CacheEvict(value =  CommonContent.CACHE_NAMES, key = CACHE_KEY)
    public int delete(T record) throws Exception {
        return super.delete(record);
    }

    @Override
    @CacheEvict(value =  CommonContent.CACHE_NAMES, key = CACHE_KEY)
    public int insertSelective(T record) throws Exception {
        return super.insertSelective(record);
    }

    @Override
    @CacheEvict(value =  CommonContent.CACHE_NAMES, key = CACHE_KEY)
    public int updateByPrimaryKey(T record) throws Exception {
        return super.updateByPrimaryKey(record);
    }

    @Override
    @CacheEvict(value =  CommonContent.CACHE_NAMES, key = CACHE_KEY)
    public int updateByPrimaryKeySelective(T record) throws Exception {
        return super.updateByPrimaryKeySelective(record);
    }

    @Override
    @CacheEvict(value =  CommonContent.CACHE_NAMES, key = CACHE_KEY)
    public int delete(T[] ts) throws Exception {
        return super.delete(ts);
    }

    @Override
    @CacheEvict(value =  CommonContent.CACHE_NAMES, key = CACHE_KEY)
    public int deleteByPrimaryKeys(Object[] keys) throws Exception {
        return super.deleteByPrimaryKeys(keys);
    }
}
