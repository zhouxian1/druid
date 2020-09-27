package com.hjj.common.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RestUrlUtil {

	////调用orc接口
	public static String restUrl(String ocrurl) throws Exception{
		String url=PropertiesUtils.getProperty("resturl")+"?imgPath="+ocrurl+"";
		URL restURL=new URL(url);
		HttpURLConnection conn=(HttpURLConnection) restURL.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setAllowUserInteraction(false);
		PrintStream ps=new PrintStream(conn.getOutputStream());
		ps.close();
		BufferedReader bReader=new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line,resultStr="";
		while(null!=(line=bReader.readLine())){
			resultStr+=line;
		}
		bReader.close();
		return resultStr;
	}
}
