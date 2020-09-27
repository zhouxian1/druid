package com.hjj.common.util;

import java.io.File;
import java.net.URISyntaxException;

/**
 * 文件路径工具类
 * 
 * 
 * @author taosq
 * 
 */
public final class FilePathUtils {

	private FilePathUtils() {
	};

	private static String getTempDir(String dir) {
		return getWEBINFDir() + File.separator
				+ PropertiesUtils.getProperty(dir);
	}

	/**
	 * @return 获取web-info目录
	 */
	public static String getWEBINFDir() {
		String path = null;
		try {
			path = FilePathUtils.class.getResource("").toURI().getPath();
			path = path.substring(0, path.indexOf("classes"));
			return path;
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * @return 获取web-info目录
	 */
	public static String getWebContentDir() {
		File classesFile = new File(Thread.currentThread().getContextClassLoader().getResource("").getFile());
    	return classesFile.getParentFile().getParent().replace("%20", " ");
	}
	
}
