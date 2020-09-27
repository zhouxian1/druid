package com.hjj.common.util;

import org.apache.shiro.SecurityUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class UserUtils {

	public static String getUsername() throws UnsupportedEncodingException{
		Object username = SecurityUtils.getSubject().getPrincipal();
		
		if(null != username){
			//解决中文乱码问题
			String casUserName = URLDecoder.decode(username.toString(), "UTF-8");
			return (String)casUserName;
		}
		return null;
	}
}
