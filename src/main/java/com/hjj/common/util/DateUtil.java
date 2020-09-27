package com.hjj.common.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DateUtil {
	
	public static Object dateToString(Date date,Object value){
		Object str=null;
		DateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		if(value.equals("SHORT")){
			format=DateFormat.getDateInstance(DateFormat.SHORT);
			str=format.format(date);
		}else if(value.equals("MEDIUM")){
			format=DateFormat.getDateInstance(DateFormat.MEDIUM);
			str=format.format(date);
		}else if(value.equals("FULL")){
			format=DateFormat.getDateInstance(DateFormat.FULL);
			str=format.format(date);
		}
		return str;
	}
}
