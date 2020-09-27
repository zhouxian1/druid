package com.hjj.common.aop;

import com.dbs.common.util.*;
import com.dbs.ep.sys.domain.SysBehaviorTrace;
import com.dbs.ep.sys.domain.User;
import com.dbs.ep.sys.service.SysBehaviorTraceArgService;
import com.dbs.ep.sys.service.SysBehaviorTraceService;
import com.dbs.ep.sys.service.UserService;
import com.dbs.epzh.qbyjcg.domain.QbTjwjqkQbyjcg;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.internal.util.JavaBeansUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * AOP 检测用户 对数据的 增删改 记录
 *
 * @author NPF 2016-05-16
 */
@Aspect
@Component
public class LogAspect {

    private final Logger logger = Logger.getLogger(this.getClass());
    @Resource
    private SysBehaviorTraceService sysBehaviorTraceService;
    @Resource
    private SysBehaviorTraceArgService sysBehaviorTraceArgService;
    @Resource
    private UserService userService;
    /**
     * epzh数据层mapper切入点 com.dbs.epzh.*.service 新增、修改、删除 打头方法
     */
       @Pointcut(value="execution(* com.dbs.epzh.*.service.*.insert*(..))||"
    		         + "execution(* com.dbs.epzh.*.service.*.save*(..))||"
    		         + "execution(* com.dbs.epzh.*.service.*.add*(..))||"
                     + "execution(* com.dbs.epzh.*.service.*.update*(..))||"
                     + "execution(* com.dbs.epzh.*.service.*.delete*(..))")
        public void epzhAspectMethod() {
    }

    /**
     * epzh数据层mapper切入点 com.dbs.common.service  新增、修改、删除 打头方法
     */
    @Pointcut("execution(* com.dbs.common.service.*.insert*(..)) ||"
    		+ "execution(* com.dbs.common.service.*.save*(..)) ||"
    		+ "execution(* com.dbs.common.service.*.add*(..)) ||"
    		+ "execution(* com.dbs.common.service.*.update*(..)) ||"
    		+ "execution(* com.dbs.common.service.*.delete*(..))")
    public void commonAspectMethod() {
    }

    /**
     * epzh方法执行后
     */
    @After("epzhAspectMethod()")
    public void epzhSerivceAfter(JoinPoint point) {
        if (SysBehaviorTraceEnable.isEnable()) {
            saveLog(point);
        }
    }

    /**
     * epzh方法执行后
     */
    @After("commonAspectMethod()")
    public void commonSerivceAfter(JoinPoint point) {
        if (SysBehaviorTraceEnable.isEnable()) {
            saveLog(point);
        }
    }

    /**
     * 保存日志
     * @param point
     */
    public void saveLog(JoinPoint point) {
        try {
            // 拦截类的接口路径
            String classPath = point.getTarget().getClass().getInterfaces()[0].getName();
            if (classPath.contains("ZzbzS") || !classPath.startsWith("com.dbs.epzh")) {
                return;
            }

            // 拦截的方法名称
            String methodName = point.getSignature().getName();
            if (!methodName.startsWith("insert") && !methodName.startsWith("update") && !methodName.startsWith("delete")) {
                return;
            }
            //获取"用户名"和"用户Id"
            String tempUsername=UserUtils.getUsername();
            String userId="";
            String userName="";
            if(StringUtils.isNotBlank(tempUsername)){
                User user=userService.selectByUsername(tempUsername);
                userId=String.valueOf(user.getId());
                userName=(String) user.getUsername();
             }else{
            	 /**
                  * 如果“情报成果信息管理系统”增、删、改, ep单独部署时，epzh不能获取不到用户名，从ep页面中接收用户信息
                  * 其他增、删、改，从utils中获取用户信息
                  */
                 if(classPath.equals("com.dbs.epzh.qbyjcg.service.QbTjwjqkQbyjcgService")){
						QbTjwjqkQbyjcg qbTjwjqkQbyjcg = null;
						if(null!=point.getArgs() && point.getArgs().length>0){
							String className=point.getArgs()[0].getClass().getName();
							if(className.equals("com.dbs.epzh.qbyjcg.domain.QbTjwjqkQbyjcg")){
								qbTjwjqkQbyjcg=(QbTjwjqkQbyjcg)point.getArgs()[0];
							}else if(className.equals("java.util.ArrayList")){
								ArrayList list=(ArrayList)point.getArgs()[0];
								qbTjwjqkQbyjcg=(QbTjwjqkQbyjcg)list.get(0);
							}
						}
						if(null!=qbTjwjqkQbyjcg){
							userId=qbTjwjqkQbyjcg.getUserId();
							userName=qbTjwjqkQbyjcg.getUsername();
						}else{
							 userId="1";
						     userName="admin";
						}
                 }else{
                	 //赋默认值
                	 userId="1";
                     userName="admin";
                 }
             }
            // spring 容器中的唯一标示
            String springCode = classPath.substring(classPath.lastIndexOf(".") + 1, classPath.length());
            springCode = springCode.substring(0, 1).toLowerCase() + springCode.substring(1, springCode.length());

            // 获取表名
            String tablePath = classPath.replace("service", "domain").replace("Service", "");
            Class domainClass = Class.forName(tablePath);
            Table table = (Table) domainClass.getAnnotation(Table.class);
            String tableName = table.name();
            // 方法参数
            Object[] pargs = point.getArgs();
            // 在参数中获取 NM
            if (pargs.length > 0) {
            	//获取参数
                Object paramObj = pargs[0];
                
                //接收参数 实体类class
                Class entityClass = Class.forName(paramObj.getClass().getName());
                
                /**
                 * 判断参数类型,
                 * 如果是List循环取出实体对象，并调用日志新增方法；
                 * 否则，直接当实体对象处理，并调用日志新增方法
                 * 
                 */
                String paramClassName=paramObj.getClass().getName();
                if(paramObj instanceof List){
                	/**接收参数类型，且当做List循环处理，添加数据到日志表*/
                	List list=(List)paramObj;
                	for (int i = 0; i < list.size(); i++) {
                		 SysBehaviorTrace sbt = commonGetSysBehaviorTrace(userId,userName,tableName,classPath,
                                 springCode,methodName);
                         
                		Object obj=list.get(i);
                		Class tempClass =obj.getClass();
                		commonInsertSysBehaviorTrace(tempClass,tableName,obj,sbt);
					}
                }else{
                	/**接收参数类型，且当实体类处理，直接添加到日志表*/
                	SysBehaviorTrace sbt = commonGetSysBehaviorTrace(userId,userName,tableName,classPath,
                             springCode,methodName);
                	commonInsertSysBehaviorTrace(entityClass,tableName,paramObj,sbt);
                }        
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
    //提取公共方法，新增日志表功能(“用户操作记录表”)
    public void commonInsertSysBehaviorTrace(Class entityClass,String tableName,Object paramObj,SysBehaviorTrace sbt){
    	//获取主键值nm，如果不存在则不是主表，不需要保存
        String primaryProperty = PropertiesUtils.getProperty("primary-property." + tableName);
        if (StringUtils.isNotBlank(primaryProperty)) {
			try {
	            try {
	            	Method m = entityClass.getMethod(JavaBeansUtil.getGetterMethodName(primaryProperty, FullyQualifiedJavaType.getStringInstance()));
					String nm = m.invoke(paramObj).toString();
		            sbt.setNm(nm);
		            
		            Field[] fiels = entityClass.getDeclaredFields();
	                for (Field f : fiels) {
	                    String fieldName = f.getName();
	                    if (fieldName.toLowerCase().indexOf("mc") > -1 || fieldName.toLowerCase().indexOf("xm") > -1 ||
	                            fieldName.toLowerCase().indexOf("wjm") > -1 || fieldName.toLowerCase().indexOf("bt") > -1) {
	                        Method method2 = entityClass.getMethod(JavaBeansUtil.getGetterMethodName(f.getName(), FullyQualifiedJavaType.getStringInstance()));
	                        if(null!=method2.invoke(paramObj)){
	 	                         sbt.setMc(method2.invoke(paramObj).toString());
	 	                         break;
	                        }
	                    }
	                }
	                
	                if(tableName.equals("QB_TJWJQK_JTZZQK")){ //组织机构联合主键、特殊处理
	                	if(StringUtils.isNotBlank(sbt.getMc())){
	                		sbt.setNm(nm+"_"+sbt.getMc());
	                	}
	                }
	                
					sysBehaviorTraceService.insertSelective(sbt);
		            new Thread(new Runnable() {
		                @Override
		                public void run() {
		                    try {
		                        ResponseWrap response = HttpUtils.get(PropertiesUtils.getProperty("ep.url")+"/search/refresh.html").execute();
		                        logger.error("数据已同步到ep中 :"+response.getString());
		                    } catch (Exception e) {
		                        logger.error(e.getMessage(), e);
		                    }
		                }
		            }).start();
	            } catch (Exception e) {
	            	e.printStackTrace();
	            }
			} catch (SecurityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }
    }
    //提取的公共方法，获取“用户操作记录表”实例化对象
    public SysBehaviorTrace commonGetSysBehaviorTrace(String userId,String userName,String tableName,String classPath,
    		                                              String springCode,String methodName){
    	 SysBehaviorTrace sbt = new SysBehaviorTrace();
         sbt.setUserId(userId);
         sbt.setUserName(userName);
         sbt.setOperationTable(tableName);
         sbt.setSjsj(new Date());
         sbt.setType(1);
         sbt.setOperationModule(PropertiesUtils.getProperty(tableName));
         sbt.setClasspath(classPath);
         sbt.setSpringcode(springCode);
         sbt.setMethodname(methodName);
         //当前操作类型
         String operationType="";
         if (methodName.startsWith("insert")) {
         	operationType="insert";
         	
         } else if (methodName.startsWith("update")) {
         	operationType="update";
         	
         } else if (methodName.startsWith("delete")) {
         	operationType="delete";
         }
         sbt.setOperationType(operationType);
         
         return sbt;
    }

}
