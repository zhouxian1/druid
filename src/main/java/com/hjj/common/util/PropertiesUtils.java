package com.hjj.common.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @ProjectNmae：util.
 * @ClassName：PropertysUtil
 * @Description： property配置文件工具类
 * @author：fanp
 * @crateTime：2013-7-3
 * @editor：
 * @editTime：
 * @editDescription：
 * @version 1.0.0
 */
public class PropertiesUtils extends PropertyPlaceholderConfigurer {

	private static Map<String, String> ctxPropertiesMap;

	@Override
	protected void processProperties(ConfigurableListableBeanFactory beanFactory, Properties props) throws BeansException {
		super.processProperties(beanFactory, props);
		// 读取propertys到ctxPropertiesMap属性中
		ctxPropertiesMap = new ConcurrentHashMap<String, String>();
		for (Object key : props.keySet()) {
			String keyStr = key.toString();
			String value = props.getProperty(keyStr);
			ctxPropertiesMap.put(keyStr, value);
		}
	}

	/**
	 * 获取propertys值
	 * 
	 * @param name
	 *            propertys的key值
	 * @return
	 * @author fanp
	 * @date 2013-7-3
	 */
	public static String getProperty(String name) {
		return ctxPropertiesMap.get(name);
	}

}
