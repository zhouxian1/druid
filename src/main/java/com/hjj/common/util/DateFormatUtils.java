package com.hjj.common.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @ProjectNmae：util.date
 * @ClassName：DateFormatUtil
 * @Description： (日期格式化转换)
 * @author：Chl
 * @crateTime：2013-5-14 下午1:44:13
 * @editor：
 * @editTime：
 * @editDescription：
 * @version 1.0.0
 */
public class DateFormatUtils {

	/**
	 * 格式化  2015年2月7日 为 2015-02-07
	 * @param dateStr
	 * @return
	 */
	public static String FormatDate(String dateStr){
		String str = "";
		Date date = new Date();
		int day = 0;
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
		DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
		try {
			if(dateStr.indexOf("年")>-1 && dateStr.indexOf("月")>-1 && dateStr.indexOf("日")>-1){
				date = dateFormat.parse(dateStr);
				str =  dateFormat2.format(date);
			}else if(dateStr.indexOf("天前")>-1){
				day = Integer.parseInt(dateStr.substring(0,dateStr.indexOf("天前")).trim());
				Date tempdate = new Date(date.getTime()-(day*24*60*60*1000));
				str =  dateFormat2.format(tempdate);
			}else if(dateStr.indexOf("小时前")>-1){
				day = Integer.parseInt(dateStr.substring(0,dateStr.indexOf("小时前")).trim());
				Date tempdate = new Date(date.getTime()-(day*60*60*1000));
				str =  dateFormat2.format(tempdate);
			}
			
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return str;
	}
	
	/**
	 * 格式化 
	 * @param dateStr
	 * @return
	 */
	public static String FormatDate(Date date,String dateFormatStr){
		String str = "";
		DateFormat dateFormat = new SimpleDateFormat(dateFormatStr);
		try {
			str =  dateFormat.format(date);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return str;
	}
	
	/**
	 * 将一个日期字符串dateStr按format的形式进行设置
	 * 
	 * @param dateStr
	 *            日期字符串
	 * @param format
	 *            日期的格式字符串
	 * @return format形式的日期
	 */
	public static Date StringToDate(String dateStr, String format) {
		if (dateStr == null || dateStr.length() == 0)
			return null;

		DateFormat dateFormat = new SimpleDateFormat(format);

		try {
			return dateFormat.parse(dateStr);
		} catch (ParseException ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 比较两个 字符串时间的大小  
	 * @param dateStr1
	 * @param dateStr2
	 * @return
	 * @throws ParseException 
	 */
	public static boolean compareDate(String dateStr1,String dateStr2){
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		
		try {
			Date td1 = dateFormat.parse(dateStr1);
			Date td2 = dateFormat.parse(dateStr2);
			return td1.getTime()>td2.getTime();
		} catch (ParseException ex) {
			ex.printStackTrace();
			return true;
		}
		
	}
	
	/**
	 * 获取当前时间 yyyy-MM-dd
	 * @return
	 */
	public static String getDate(){
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.format(new Date());
	}
	
	/**
	 * 获取当前时间 DATE
	 * @return
	 * @throws ParseException
	 */
	public static Date getNowDate() throws ParseException{
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.parse(getDate());
	}
	
	/**
	 * 获取当前时间 yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static String getDateByFormat(String format){
		DateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(new Date());
	}

	public static Date parse(String source) throws ParseException {
		if(StringUtils.isNoneBlank(source)){
			if(source.matches("\\d{4}-\\d{2}-\\d{2}")){
				return new SimpleDateFormat("yyyy-MM-dd").parse(source);
			} else if(source.matches("\\d{4}/\\d{2}/\\d{2}")){
				return new SimpleDateFormat("yyyy/MM/dd").parse(source);
			}
		}
		return null;
	}
}
