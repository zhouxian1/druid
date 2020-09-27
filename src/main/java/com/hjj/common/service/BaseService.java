package com.hjj.common.service;


import com.hjj.common.domain.Page;
import com.hjj.common.domain.Tree;
import com.hjj.common.mapper.BaseMapper;

import java.util.List;

/**
 * Created by Administrator on 2015/4/22.
 */
public interface BaseService<T> {

    /**
     * 查询全部结果
     * @return
     * @throws Exception
     */
    List<T> selectAll() throws Exception;

    /**
     * 根据主键进行查询，必须保证结果唯一
     * 单各字段做主键，可以直接写主键的值
     * 联合主键，需要写实体
     * @param key
     * @return
     * @throws Exception
     */
    T selectByPrimaryKey(Object key) throws Exception;

    /**
     * 根据实体类中不为null的字段查询总数
     * @param record
     * @return
     * @throws Exception
     */
    int selectCount(T record) throws Exception;

    /**
     * 根据实体类中不为null的字段进行查询，查询条件为等号，精确查询
     * @param record
     * @return
     * @throws Exception
     */
    List<T> select(T record) throws Exception;

    /**
     * 根据实体类中不为null的字段进行查询，必须保证结果唯一
     * 有多个结果会抛出异常
     * @param record
     * @return
     * @throws Exception
     */
    T selectOne(T record) throws Exception;
    
    /**
     * 根据不为空的属性查询
     * @param t
     * @return
     * @throws Exception
     */
    List<T> selectSelective(T t) throws Exception;
    
    /**
     * 保存一个实体，null的属性也会保存，不会使用数据库默认值
     * @param record
     * @return
     * @throws Exception
     */
    int insertSelective(T record) throws Exception;
    
    /**
     * 保存集合，循环调用insertSelective方法
     * @param t
     * @return
     * @throws Exception
     */
    List<T> insertList(List<T> t) throws Exception;

    /**
     * 根据主键更新实体全部字段，null值会被更新
     * @param record
     * @return
     * @throws Exception
     */
    int updateByPrimaryKey(T record) throws Exception;

    /**
     * 根据主键进行更新属性不为null的属性
     * @param record
     * @return
     * @throws Exception
     */
    int updateByPrimaryKeySelective(T record) throws Exception;

    
    /**
     * 根据主键删除实体
     * @param key
     * @return
     * @throws Exception
     */
    int deleteByPrimaryKey(Object key) throws Exception;

    /**
     * 根据 实体参数查询
     * @param record
     * @return
     * @throws Exception
     */
    int delete(T record) throws Exception;

    int delete(T[] ts) throws Exception;
    int delete(List<T> ts) throws Exception;
    int deleteByPrimaryKeys(Object[] keys) throws Exception;
    int deleteByPrimaryKeys(List<Object> keys) throws Exception;

    /**
     * 分页查询
     * @param t
     * @param page
     * @return
     * @throws Exception
     */
    Page select(T t, Page page) throws Exception;
    
    /**
     * 根据分页查询条件，返回List数据值,没有分页信息
     * @param t
     * @param page
     * @return
     * @throws Exception
     */
    List<T> selectList(T t, Page page) throws Exception ; 
    

    BaseMapper<T> getMapper();
    Object getExample(T t);
    
    /**
     * 根据Example 生成查询条件 进行查询
     * @param t
     * @return
     * @throws Exception
     */
    List<T> queryByExample(T t) throws Exception;

    /**
     * 自动查询字典表组装tree方法
     * @param record  实体参数
     * @param xhProperty 序号列  字段名称
     * @param mcProperty 名称列  字段名称
     * @return
     * @throws Exception
     */
    List<Tree> tree(T record, String xhProperty, String mcProperty) throws Exception;

	/**
	 * 武器装备 将父级 典型配备方案数据导入 子表
	 */
	void updateByParentZbnm(T t, String parentZbnm, String zbnm, String otherPK, String otherPKName) throws Exception;
}

