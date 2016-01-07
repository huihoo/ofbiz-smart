package org.huihoo.ofbiz.smart.base.util.xml.impl;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.huihoo.ofbiz.smart.base.util.DateUtil;
import org.huihoo.ofbiz.smart.base.util.xml.IXmlConverter;

public class SmartXmlConverter implements IXmlConverter {

	
	@Override
	public String objectToXml(Object object) throws Exception {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		stringBuilder.append("<smart");
		stringBuilder.append(" type='").append(getType(object)).append("'>");
		if(object instanceof List){
			stringBuilder.append(listToxml((List<?>)object));
		}else if(object instanceof Map){
			stringBuilder.append(maptToxml((Map<String,Object>)object));
		}else if (object instanceof Integer || object instanceof Long || object instanceof Byte || object instanceof Short
				|| object instanceof String || object instanceof BigDecimal || object instanceof Float
				|| object instanceof Double || object instanceof Date || object instanceof java.sql.Date) {
			if(object instanceof Date || object instanceof java.sql.Date){
				if(object instanceof Date ){
					stringBuilder.append("<value>").append(DateUtil.format((Date)object)).append("</value>");
				}else{
					stringBuilder.append("<value>").append(DateUtil.format((java.sql.Date)object)).append("</value>");
				}
			}else{
				stringBuilder.append("<value>").append(object).append("</value>");
			}
		}else{
			stringBuilder.append(beanToXml(object));
		}
		stringBuilder.append("</smart>");
		return stringBuilder.toString();
	}

	/**
	 * 类型处理
	 * @param ob
	 * @return
	 */
	private   String typeProcess(Object object){
		StringBuilder stringBuilder = new StringBuilder();
		if(object instanceof List){
			stringBuilder.append(listToxml((List<?>)object));
		}else if(object instanceof Map){
			stringBuilder.append(maptToxml((Map<String,Object>)object));
		}else if (object instanceof Integer || object instanceof Long || object instanceof Byte || object instanceof Short
				|| object instanceof String || object instanceof BigDecimal || object instanceof Float
				|| object instanceof Double || object instanceof Date || object instanceof java.sql.Date) {
			if(object instanceof Date || object instanceof java.sql.Date){
				if(object instanceof Date ){
					stringBuilder.append(DateUtil.format((Date)object));
				}else{
					stringBuilder.append(DateUtil.format((java.sql.Date)object));
				}
			}else{
				stringBuilder.append(object);
			}
		}else{
			stringBuilder.append(beanToXml(object));
		}
		return stringBuilder.toString();
	
	}
	
	/**
	 * 实体bean 转xml 规则
	 * @param bean
	 * @return
	 */
	private  String beanToXml(Object bean) {
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
						builder.append("<").append(name).append(" type='").append(getType(ob)).append("'").append(">");
						builder.append(typeProcess(ob));
						builder.append("</").append(name).append(">");
					}
				} catch (Exception e) {
				}
			}
		}
		builder.append(endXml(bean));
		return builder.toString();
	}
	
	/**
	 * 类型转换
	 * @param ob
	 * @return
	 */
	private  String getType(Object object){
		if(object instanceof List){
			return "list";
		}else if(object instanceof Map){
			return "map";
		}else if (object instanceof Integer ) {
			return "int";
		}else if(object instanceof Long ){
			return "long";
		}else if(object instanceof Byte ){
			return "byte";
		}else if(object instanceof Short){
			return "short";
		}else if(object instanceof String ){
			return "string";
		}else if(object instanceof BigDecimal){
			return "decimal";
		}else if(object instanceof Float){
			return "float";
		}else if(object instanceof Double){
			return "double";
		}else  if(object instanceof Date || object instanceof java.sql.Date){
			return "date";
		}else{
			return "object";
		}
	}
	
	
	/**
	 * map 转xml
	 * @param map
	 * @return
	 */
	private  String maptToxml(Map<String,Object> map){
		StringBuilder builder = new StringBuilder();
		Set<Entry<String, Object>> entries = map.entrySet();
		Iterator<Entry<String, Object>> iterator = entries.iterator();
		while(iterator.hasNext()){
			Entry<String, Object> entry=iterator.next();
			builder.append("<").append(entry.getKey()).append(" type='").append(getType(entry.getValue())).append("'").append(">");
			builder.append(typeProcess(entry.getValue()));
			builder.append("</").append(entry.getKey()).append(">");
		}
		return builder.toString();
	}
	
	
	
	/**
	 * list 转xml
	 * @param list
	 * @return
	 */
	private  String listToxml(List<?> list){
		StringBuilder builder = new StringBuilder();
		for(Object ob : list){
			builder.append("<value>").append(typeProcess(ob)).append("</value>");
		}
		return builder.toString();
	}
	

	
	private static String startXml(Object ob){
		if(ob instanceof List){
			return "<list>";
		}else if(ob instanceof Map){
			return "<map>";
		}else if (ob instanceof Integer || ob instanceof Long || ob instanceof Byte || ob instanceof Short
				|| ob instanceof String || ob instanceof BigDecimal || ob instanceof Float
				|| ob instanceof Double || ob instanceof Date || ob instanceof java.sql.Date) {
			return "<value>";
		}else{
			Package package1 = ob.getClass().getPackage();
			return "<"+ob.getClass().getSimpleName()+" package='"+package1.getName()+"'"+">";
		}
	}
	
	/**
	 * 结束标签
	 * @param ob
	 * @return
	 */
	private  String endXml(Object ob){
		if(ob instanceof List){
			return "</list>";
		}else if(ob instanceof Map){
			return "<／map>";
		}else if (ob instanceof Integer || ob instanceof Long || ob instanceof Byte || ob instanceof Short
				|| ob instanceof String || ob instanceof BigDecimal || ob instanceof Float || ob instanceof Double
				||  ob instanceof Date || ob instanceof java.sql.Date) {
			return "</value>";
		}else{
			return "</"+ob.getClass().getSimpleName()+">";
		}
	}
	
}
