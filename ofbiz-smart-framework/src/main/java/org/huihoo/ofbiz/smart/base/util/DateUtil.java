package org.huihoo.ofbiz.smart.base.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	
	private static SimpleDateFormat ymdhms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static String format(Date date){
		
		return ymdhms.format(date);
	}
	
	public static String format(java.sql.Date date){
		
		return ymdhms.format(date);
	}
}
