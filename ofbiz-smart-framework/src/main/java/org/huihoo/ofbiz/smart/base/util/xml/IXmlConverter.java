package org.huihoo.ofbiz.smart.base.util.xml;


public interface IXmlConverter{
	
	/**
	 * 对象转xml
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public String objectToXml(Object object)throws Exception;
	
}