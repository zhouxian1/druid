package com.hjj.common.util;

import java.io.File;

public class PathUtil {
	
	public static String getWebRoot(){
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		String classpath = cl.getResource("").getFile();
		String webRoot = new File(classpath).getParentFile().getParentFile().getAbsolutePath();
		return webRoot;
	}

	public static String getTempDir(){
		return System.getProperty("java.io.tmpdir");
	}
	
}
