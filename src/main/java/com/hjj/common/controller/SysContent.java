package com.hjj.common.controller;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SysContent {
	 	private static ThreadLocal<HttpServletRequest> requestThreadLocal = new ThreadLocal<HttpServletRequest>();
	    private static ThreadLocal<HttpServletResponse> responseThreadLocal = new ThreadLocal<HttpServletResponse>();
	    private static ThreadLocal<HttpSession> sessionThreadLocal = new ThreadLocal<HttpSession>();
	    private static ThreadLocal<ServletContext> contextThreadLocal = new ThreadLocal<ServletContext>();

	    public static HttpServletRequest getRequest(){
	    	return requestThreadLocal.get();
	    }
	    public static void setRequest(HttpServletRequest request){
	    	requestThreadLocal.set(request);
	    }
	    public static HttpServletResponse getResponse(){
	    	return responseThreadLocal.get();
	    }
	    public static void setResponse(HttpServletResponse response){
	    	responseThreadLocal.set(response);
	    }
	    public static HttpSession getSession(){
	    	return sessionThreadLocal.get();
	    }
	    public static void setSession(HttpSession session){
	    	sessionThreadLocal.set(session);
	    }
	    public static ServletContext getContext(){
	    	return contextThreadLocal.get();
	    }
	    public static void setContext(ServletContext context){
	    	contextThreadLocal.set(context); 
	    }
}
