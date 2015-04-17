package org.huihoo.ofbiz.smart.base.utils.json;

import java.sql.Date;
import java.sql.Timestamp;

public class JsonConfigExtends {
	static net.sf.json.JsonConfig jsonConfig = new net.sf.json.JsonConfig();   //JsonConfig是net.sf.json.JsonConfig中的这个，为固定写法  
	static{
		jsonConfig.registerJsonValueProcessor(Timestamp.class , new JsonDateValueProcessor()); 
		jsonConfig.registerJsonValueProcessor(Date.class , new JsonDateValueProcessor()); 
	}
	
	public static net.sf.json.JsonConfig getJsonconfig(){
		
		return jsonConfig;
	}
}
