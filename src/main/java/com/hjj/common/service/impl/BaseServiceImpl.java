package com.hjj.common.service.impl;

import com.dbs.common.domain.Example;
import com.dbs.common.domain.Page;
import com.dbs.common.domain.Tree;
import com.dbs.common.service.BaseService;
import com.dbs.common.util.SpringUtils;
import com.dbs.epzh.wqzb.domain.QbTjwjqkWqzbZhpbqk;
import com.dbs.epzh.wqzb.mapper.QbTjwjqkWqzbZhpbqkMapper;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.internal.util.JavaBeansUtil;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Id;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseServiceImpl<T> implements BaseService<T> {

    public int insertSelective(T record) throws Exception {
        return getMapper().insertSelective(record);
    }

    public List<T> selectAll() throws Exception {
        return getMapper().selectAll();
    }

    public T selectByPrimaryKey(Object key) throws Exception {
        return getMapper().selectByPrimaryKey(key);
    }

    public int selectCount(T record) throws Exception {
        return getMapper().selectCount(record);
    }

    public List<T> select(T record) throws Exception {
        return getMapper().select(record);
    }

    public T selectOne(T record) throws Exception {
        return getMapper().selectOne(record);
    }

    @Transactional
    public int updateByPrimaryKey(T record) throws Exception {
        return getMapper().updateByPrimaryKey(record);
    }

    @Transactional
    public int updateByPrimaryKeySelective(T record) throws Exception {
        return getMapper().updateByPrimaryKeySelective(record);
    }

    @Transactional
    public List<T> insertList(List<T> ts) throws Exception {
        for (T recode : ts) {
            getMapper().insertSelective(recode);
        }
        return ts;
    }

    @Transactional
    public int deleteByPrimaryKey(Object key) throws Exception {
        return getMapper().deleteByPrimaryKey(key);
    }

    @Transactional
    public int delete(T record) throws Exception {
        return getMapper().delete(record);
    }

    @Transactional
    public int delete(T[] ts) throws Exception {
        int i = 0;
        if (null != ts) {
            for (T t : ts) {
                i += this.delete(t);
            }
        }
        return i;
    }

    @Transactional
    public int delete(List<T> ts) throws Exception {
        int i = 0;
        if (null != ts) {
            for (T t : ts) {
                i += this.delete(t);
            }
        }
        return i;
    }

    @Transactional
    public int deleteByPrimaryKeys(Object[] keys) throws Exception {
        int i = 0;
        if (null != keys) {
            for (Object key : keys) {
                i += this.deleteByPrimaryKey(key);
            }
        }
        return i;
    }

    @Transactional
    public int deleteByPrimaryKeys(List<Object> keys) throws Exception {
        int i = 0;
        if (null != keys) {
            for (Object key : keys) {
                i += this.deleteByPrimaryKey(key);
            }
        }
        return i;
    }
    @Override
    public List<T> selectList(T t, Page page) throws Exception {
        Object example = getExample(t);
        if (null == example) {
            example = new Example(t.getClass());
        }

        if (StringUtils.isNotBlank(page.getSort())) {
            String[] sorts = page.getSort().split(",");
            String[] orders = null;
            if (StringUtils.isNotBlank(page.getOrder())) {
                orders = page.getOrder().split(",");
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < sorts.length; i++) {
                sb.append(sorts[i]);
                if (null != orders && orders.length > i) {
                    sb.append(" ").append(orders[i]);
                    if (i < (sorts.length - 1)) {
                        sb.append(",");
                    }
                }
            }
            if (example instanceof Example) {
                ((Example) example).setOrderByClause(sb.toString());
            } else {
                Method m = example.getClass().getMethod("setOrderByClause", String.class);
                m.invoke(example, sb.toString());
            }
        }

        if (StringUtils.isNotBlank(page.getColumns())) {
            if (example instanceof Example) {
                ((Example) example).selectProperties(page.getColumns().split(","));
            }
        }

        com.github.pagehelper.Page p = null;
        if(null != page.getPage() && null != page.getRows()) {
            p = PageHelper.startPage(page.getPage(), Integer.parseInt(String.valueOf(page.getRows())));
        }
        List<T> list = selectByExample(example);
        if(null != p)
        	page.setTotal(p.getTotal());
        return list;
    }
    @Override
    public Page select(T t, Page page) throws Exception {
//        page.setTotal(selectCountByExample(queryByExample(t)));
        page.setRows(selectList(t, page));
        return page;
    }

    public List<T> selectByExample(Object example) {
        return getMapper().selectByExample(example);
    }

    public int selectCountByExample(Object example) {
        return getMapper().selectCountByExample(example);
    }

    @Override
    public List<T> selectSelective(T t) throws Exception {
        return getMapper().selectByExample(getExample(t));
    }

    @Override
    public List<T> queryByExample(T t) {
        Object example = getExample(t);
        if (null == example) {
            example = new Example(t.getClass());
        }
        return getMapper().selectByExample(example);
    }

    @Override
    public List<Tree> tree(T record, String xhProperty, String mcProperty) throws Exception {

        String xhGetMethodName;
        String mcGetMethodName;
        if (StringUtils.isNotBlank(xhProperty)) {
            xhGetMethodName = "get" + xhProperty.substring(0, 1).toUpperCase() + xhProperty.substring(1);
        } else {
            xhGetMethodName = "getXh";
        }
        if (StringUtils.isNotBlank(mcProperty)) {
            mcGetMethodName = "get" + mcProperty.substring(0, 1).toUpperCase() + mcProperty.substring(1);
        } else {
            mcGetMethodName = "getMc";
        }

        int minLength = 999;
        List<T> dataList = this.queryByExample(record);

        for (T t : dataList) {
            Method _m = t.getClass().getMethod(xhGetMethodName);
            String xh = (String) _m.invoke(t);
            if (StringUtils.isNotBlank(xh) && xh.length() < minLength) {
                //获取序号最小位数
                minLength = xh.length();
            }
        }

        List<Tree> treeNodeList = new ArrayList<Tree>();
        for (T t : dataList) {
            Method _m = t.getClass().getMethod(xhGetMethodName);
            String xh = (String) _m.invoke(t);
            if (StringUtils.isNotBlank(xh) && xh.length() == minLength) {
                _m = t.getClass().getMethod(mcGetMethodName);
                String mc = (String) _m.invoke(t);

                String nm = getNm(t);

                //添加一级节点
                Tree tree = new Tree(nm,xh, mc);
                appendTreeNode(dataList, tree, xh, xhGetMethodName, mcGetMethodName);
                treeNodeList.add(tree);
            }
        }
        return treeNodeList;
    }

    /**
     * 根据实体类获取 nm 数据
     *
     * @param t
     * @return
     * @throws Exception
     */
    public String getNm(T t) throws Exception {
        String nm = "";
        //获取内码字段 每个实体内码字段都不同
        Field[] fiels = t.getClass().getDeclaredFields();
        for (Field f : fiels) {
            if (null != f.getAnnotation(Id.class)) {
                Method m = t.getClass().getMethod(JavaBeansUtil.getGetterMethodName(f.getName(), FullyQualifiedJavaType.getStringInstance()));
                nm = m.invoke(t, new Object[0]).toString();
            }
        }
        return nm;
    }

    /**
     * 递归追加子节点
     *
     * @param dataList
     * @param parent
     * @param parentXh
     * @throws Exception
     */
    public void appendTreeNode(List<T> dataList, Tree parent, String parentXh, String xhGetMethodName, String mcGetMethodName) throws Exception {
        List<Tree> children = new ArrayList<>();
        for (T data : dataList) {
            Method m = data.getClass().getMethod(xhGetMethodName);
            String xh = (String) m.invoke(data);

            if (StringUtils.isNotBlank(xh) &&  xh.length() == (parentXh.length() + 2) && xh.startsWith(parentXh)) {
                Method _m = data.getClass().getMethod(mcGetMethodName);
                String mc = (String) _m.invoke(data);

                String nm = getNm(data);

                Tree tree = new Tree(nm,xh, mc);
                appendTreeNode(dataList, tree, xh, xhGetMethodName, mcGetMethodName);
                children.add(tree);
            }
        }
        if (children.size() > 0) {
            parent.setChildren(children);
        }
    }
 /**
     * 武器装备 将父级 典型配备方案数据导入 子表
     * @param t
     * @param parentZbnm(父级zbnm)
     * @param zbnm (子表zbnm)
     * @param otherPK (子表主键)
     * @throws Exception
     */
    @Transactional
	public void updateByParentZbnm(T t,String parentZbnm,String zbnm,String otherPK,String otherPKName) throws Exception{
		if(StringUtils.isNotBlank(parentZbnm)&& StringUtils.isNotBlank(otherPK)){
				//根据父级的zbnm和联合主键，查询出父级的子表数据
				String pk1[]=(String[])otherPK.split(",");
				for (String pk2 : pk1) {
					T t2=(T)t.getClass().newInstance();
					Class c1=t2.getClass();
					//设置父级的zbnm
					Method me1=c1.getMethod("setZbnm", String.class);
					me1.invoke(t2,parentZbnm);
					//设置父级除了zbnm的其他关联主键
					String setPk="set"+otherPKName.substring(0,1).toUpperCase()+otherPKName.substring(1, otherPKName.length());
					Method me2=c1.getMethod(setPk, String.class);
					me2.invoke(t2,pk2);
					
					T t3=getMapper().selectOne((T) t2);
					//将查询出的父级子表数据，新增给子级的子表
					if(null!=t3){
						 Method m1 = t.getClass().getMethod("setZbnm", String.class);
						 m1.invoke(t3, zbnm); //设置子表zbnm值
						 
						 /**查询子表数据是否已经存在，如果存在直接覆盖数据*/
						 T t4=(T)t.getClass().newInstance();
						 Class class4=t2.getClass();
						 Method method4=class4.getMethod(setPk, String.class);
						 method4.invoke(t4,pk2);
						 
						 Method method5=class4.getMethod("setZbnm", String.class);
						 method5.invoke(t4,zbnm);
						 List t4List=getMapper().select(t4);
						 if(null!=t4List && t4List.size()>0){
							 getMapper().updateByPrimaryKeySelective(t3); //修改子表
						 }else{
							 getMapper().insertSelective(t3); //新增子表
						 }
					     //如果导入“典型配备方案”需级联导入“载荷配备情况”数据
						 if(t.getClass().getName().equals("com.dbs.epzh.wqzb.domain.QbTjwjqkWqzbDxpbfa")){
							 //查询父级“载荷配备情况”数据
							 Class wqzbZhpbqkClass=QbTjwjqkWqzbZhpbqk.class;
							 QbTjwjqkWqzbZhpbqk wqzbZhpbqk=(QbTjwjqkWqzbZhpbqk)wqzbZhpbqkClass.newInstance();
								//设置父级的zbnm
								Method me3=wqzbZhpbqkClass.getMethod("setZbnm", String.class);
								me3.invoke(wqzbZhpbqk,parentZbnm);
								//设置父级的Pbfanm
								Method me4=wqzbZhpbqkClass.getMethod("setPbfanm", String.class);
								me4.invoke(wqzbZhpbqk,pk2);
								QbTjwjqkWqzbZhpbqkMapper zhpbqkMapper=SpringUtils.getBean(QbTjwjqkWqzbZhpbqkMapper.class);
								
								List list=zhpbqkMapper.select(wqzbZhpbqk);
								if(list.size()>0){
									for (int i = 0; i < list.size(); i++) {
										 QbTjwjqkWqzbZhpbqk z=(QbTjwjqkWqzbZhpbqk) list.get(i);
										 Method m2 = z.getClass().getMethod("setZbnm", String.class);
										 m2.invoke(z, zbnm); 
										 //如果存在数据则已经导入过了，则不需要再做导入
										 List zhpbqkList=zhpbqkMapper.select(z);
										 if(null!=zhpbqkList&&zhpbqkList.size()==0){
											 zhpbqkMapper.insertSelective(z); 
										 }
									}
								}
						 }
					}
				}
		}
	} 
}

