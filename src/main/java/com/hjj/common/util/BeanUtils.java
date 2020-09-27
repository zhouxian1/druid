package com.hjj.common.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * Created by zzy on 2015/9/30 0030.
 */
public class BeanUtils {

    public static Map<String, String> toMap(Object bean) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        return org.apache.commons.beanutils.BeanUtils.describe(bean);
    }

    public static <T> T toBean(Class<T> clazz, Map map) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        T object = clazz.newInstance();
        org.apache.commons.beanutils.BeanUtils.populate(object, map);
        return object;
    }

    public static Object toBean(Object obj, Map map) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        org.apache.commons.beanutils.BeanUtils.populate(obj, map);
        return obj;
    }
}
