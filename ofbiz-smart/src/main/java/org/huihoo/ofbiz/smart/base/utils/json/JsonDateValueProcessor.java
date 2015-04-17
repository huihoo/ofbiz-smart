package org.huihoo.ofbiz.smart.base.utils.json;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

public class JsonDateValueProcessor implements JsonValueProcessor {
		
	private String format = "yyyy-MM-dd HH:mm:ss";

		public Object processArrayValue(Object value, JsonConfig config) {
			return process(value);
		}

		public Object processObjectValue(String key, Object value, JsonConfig config) {
			return process(value);
		}

		private Object process(Object value) {

			if (value instanceof Date) {
				SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.UK);
				return sdf.format(value);
			}else if(value instanceof Timestamp){
				SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.UK);
				return sdf.format(value);
			}
			return value == null ? "" : value.toString();
		}
}
