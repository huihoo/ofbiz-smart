package org.huihoo.ofbiz.smart.base.util.xml.impl;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.huihoo.ofbiz.smart.base.util.xml.IXmlConverter;

public class SmartXmlConverter implements IXmlConverter {

	@Override
	public String ObjectToXml(Object object) throws Exception {

		return null;
	}

	private String listConverter(List<?> list, StringBuilder builder) {
		for (Object o : list) {

		}
		return null;
	}

	private static String beanToXml(Object bean) {
		StringBuilder builder = new StringBuilder();
		builder.append(startXml(bean));
		Method methods[] = bean.getClass().getMethods();
		for (Method method : methods) {
			String methodname = method.getName();
			if (methodname.startsWith("get") && !("getClass".equals(methodname))) {
				try {
					String name = methodname.substring(3, methodname.length());
					int size = name.length() ;
					if(size == 1){
						name = name.toLowerCase();
					}else if(size > 0){
						name = name.substring(0, 1).toLowerCase()+name.substring(1, name.length());
					}
					Object ob = method.invoke(bean, null);
					if (null != ob) {
						builder.append("<").append(name).append(">");
						builder.append(ob);
						builder.append("</").append(name).append(">");
					}
				} catch (Exception e) {
				}
			}
		}
		builder.append(endXml(bean));
		return builder.toString();
	}
	
	
	private static String maptToxml(Map<String,Object> map){
		StringBuilder builder = new StringBuilder();
		builder.append(startXml(map));
		Set<Entry<String, Object>> entries = map.entrySet();
		Iterator<Entry<String, Object>> iterator = entries.iterator();
		while(iterator.hasNext()){
			Entry<String, Object> entry=iterator.next();
			builder.append("<").append(entry.getKey()).append(">");
			builder.append(entry.getValue());
			builder.append("</").append(entry.getKey()).append(">");
		}
		
		builder.append(endXml(map));
		return null;
	}

	
	private static String startXml(Object ob){
		if(ob instanceof List){
			return "<list>";
		}else if(ob instanceof Map){
			return "<map>";
		}else if (ob instanceof Integer || ob instanceof Long || ob instanceof Byte || ob instanceof Short
				|| ob instanceof String || ob instanceof BigDecimal) {
			return "<value>";
		}else{
			Package package1 = ob.getClass().getPackage();
			return "<"+ob.getClass().getSimpleName()+" package='"+package1.getName()+"'"+">";
		}
	}
	
	private static String endXml(Object ob){
		if(ob instanceof List){
			return "</list>";
		}else if(ob instanceof Map){
			return "<／map>";
		}else if (ob instanceof Integer || ob instanceof Long || ob instanceof Byte || ob instanceof Short
				|| ob instanceof String || ob instanceof BigDecimal) {
			return "</value>";
		}else{
			return "</"+ob.getClass().getSimpleName()+">";
		}
	}
	
	
	private String typeConver(Object ob) {
		if (ob instanceof Integer || ob instanceof Long || ob instanceof Byte || ob instanceof Short
				|| ob instanceof String || ob instanceof BigDecimal) {

		}
		return null;
	}

	static class xxx {
		private String y;
		private String x;
		private String cdd;

		public String getY() {
			return y;
		}

		public void setY(String y) {
			this.y = y;
		}

		public String getX() {
			return x;
		}

		public void setX(String x) {
			this.x = x;
		}

		public String getCdd() {
			return cdd;
		}

		public void setCdd(String cdd) {
			this.cdd = cdd;
		}
		
	}

	public static void main(String[] args) {
		xxx s = new xxx();
		s.setX("123");
		s.setY("cc");
		s.setCdd("ddss");
		System.out.println(beanToXml(s));
	}

}
