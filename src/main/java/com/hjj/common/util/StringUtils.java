package com.hjj.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @ProjectNmae：util.
 * @ClassName：StringUtils
 * @Description： (String工具类)
 * @author：chl
 * @crateTime：2013-5-16 上午9:52:29
 * @editor：
 * @editTime：
 * @editDescription：
 * @version 1.0.0
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

    /**
     * 
     * @stringByRegularValidation(根据正则表达式判断字符串是否满足要求).
     * @author： Administrator
     * @createTime：2013-5-31 上午9:44:10
     * @param regularStr
     *            正则表达式
     * @param str
     *            需要验证的字符串
     * @return 满足要求返回true，否则 false
     * @return boolean
     */
    public static boolean stringByRegularValidation(String regularStr, String str) {
        Pattern pattern = Pattern.compile(regularStr);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 
     * @filterSqlString(处理SQL注入的sql语句).
     * @author： chl
     * @createTime：2013-5-15 下午4:32:59
     * @param sqlStr
     *            注入的sql语句
     * @return String
     */
    public static String escapeSql(String sqlStr) {
        if (sqlStr == null || "".equals(sqlStr)) {
            return null;
        }
        sqlStr = org.apache.commons.lang3.StringUtils.replace(sqlStr, " ", "");
        sqlStr = org.apache.commons.lang3.StringUtils.replace(sqlStr, "?<", "");
        sqlStr = org.apache.commons.lang3.StringUtils.replace(sqlStr, ">?", "");
        sqlStr = org.apache.commons.lang3.StringUtils.replace(sqlStr, "?", "");
        sqlStr = org.apache.commons.lang3.StringUtils.replace(sqlStr, "/", "");
        sqlStr = org.apache.commons.lang3.StringUtils.replace(sqlStr, "&", "");
        sqlStr = org.apache.commons.lang3.StringUtils.replace(sqlStr, "]", "");
        sqlStr = org.apache.commons.lang3.StringUtils.replace(sqlStr, "[", "");
        sqlStr = org.apache.commons.lang3.StringUtils.replace(sqlStr, "||", "");
        sqlStr = org.apache.commons.lang3.StringUtils.replace(sqlStr, ";", "");
        sqlStr = org.apache.commons.lang3.StringUtils.replace(sqlStr, "_", "");
        sqlStr = org.apache.commons.lang3.StringUtils.replace(sqlStr, "--", "");
        sqlStr = org.apache.commons.lang3.StringUtils.replace(sqlStr, "=<", "");
        sqlStr = org.apache.commons.lang3.StringUtils.replace(sqlStr, "=", "");
        sqlStr = org.apache.commons.lang3.StringUtils.replace(sqlStr, ">=", "");
        sqlStr = org.apache.commons.lang3.StringUtils.replace(sqlStr, ",", "");
        sqlStr = org.apache.commons.lang3.StringUtils.replace(sqlStr, "%", "");
        sqlStr = org.apache.commons.lang3.StringUtils.replace(sqlStr, ">", "");
        sqlStr = org.apache.commons.lang3.StringUtils.replace(sqlStr, "<", "");
        sqlStr = org.apache.commons.lang3.StringUtils.replace(sqlStr, "@", "");
        sqlStr = org.apache.commons.lang3.StringUtils.replace(sqlStr, "#", "");
        sqlStr = org.apache.commons.lang3.StringUtils.replace(sqlStr, "$", "");
        sqlStr = org.apache.commons.lang3.StringUtils.replace(sqlStr, "|", "");
        sqlStr = org.apache.commons.lang3.StringUtils.replace(sqlStr, "\"", "");
        sqlStr = org.apache.commons.lang3.StringUtils.replace(sqlStr, "!", "");
        sqlStr = org.apache.commons.lang3.StringUtils.replace(sqlStr, "~", "");
        sqlStr = org.apache.commons.lang3.StringUtils.replace(sqlStr, "^", "");
        sqlStr = org.apache.commons.lang3.StringUtils.replace(sqlStr, "*", "");
        sqlStr = org.apache.commons.lang3.StringUtils.replace(sqlStr, "//", "");
        sqlStr = org.apache.commons.lang3.StringUtils.replace(sqlStr, "\\", "");
        sqlStr = org.apache.commons.lang3.StringUtils.replace(sqlStr, "1=1", "");
        sqlStr = org.apache.commons.lang3.StringUtils.replace(sqlStr, "1", "");
        sqlStr = org.apache.commons.lang3.StringUtils.replace(sqlStr, "and", "");
        sqlStr = org.apache.commons.lang3.StringUtils.replace(sqlStr, "exec", "");
        sqlStr = org.apache.commons.lang3.StringUtils.replace(sqlStr, "insert", "");
        sqlStr = org.apache.commons.lang3.StringUtils.replace(sqlStr, "select", "");
        sqlStr = org.apache.commons.lang3.StringUtils.replace(sqlStr, "delete", "");
        sqlStr = org.apache.commons.lang3.StringUtils.replace(sqlStr, "update", "");
        sqlStr = org.apache.commons.lang3.StringUtils.replace(sqlStr, "like", "");
        sqlStr = org.apache.commons.lang3.StringUtils.replace(sqlStr, "drop", "");
        sqlStr = org.apache.commons.lang3.StringUtils.replace(sqlStr, "or", "");
        sqlStr = org.apache.commons.lang3.StringUtils.replace(sqlStr, "where", "");
        sqlStr = org.apache.commons.lang3.StringUtils.replace(sqlStr, "from", "");
        return sqlStr;
    }

    
    
    
    /**
	 * 处理列名
	 * 把空格下划线'_'去掉,同时把下划线后的首字母大写
	 */
	public static String processColnames(String name){
		if(StringUtils.isBlank(name)){
			return name;
		}
		
		//转小写
		name = name.toLowerCase();
		
		char[] ch = name.toCharArray();

		char c ='a';

		if(ch.length>3){

			for(int j=0;j <ch.length; j++){

				c = ch[j];

				if(c == '_'){

					if (ch[j+1]>= 'a' && ch[j+1] <= 'z') {

						ch[j+1]=(char) (ch[j+1]-32);

					}

				}

			}

		}

		String str = new String(ch);
		str = str.replaceAll("_", "");
		
		return str;
	}
    
    
    public static void main(String[] args) {
		String test = "QB_R_TJWJQK_BDQK";
		System.out.println(StringUtils.processColnames(test));
	}

}
