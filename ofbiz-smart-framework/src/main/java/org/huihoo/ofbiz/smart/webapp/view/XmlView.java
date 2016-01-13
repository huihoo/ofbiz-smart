package org.huihoo.ofbiz.smart.webapp.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.huihoo.ofbiz.smart.base.C;
import org.huihoo.ofbiz.smart.base.util.xml.IXmlConverter;

public class XmlView implements View {

  @Override
  public String getContentType() {
    return C.XML_CONTENT_TYPE;
  }

  @Override
  public void render(Map<String, Object> model, HttpServletRequest request,
          HttpServletResponse response) throws ViewException{
	  try{
		  IXmlConverter converter = (IXmlConverter)request.getSession().getServletContext().getAttribute(C.CTX_SUPPORTED_XML_HANDLE_ATTRIBUTE);
		  response.setContentType(getContentType());
		  response.getWriter().write( converter.objectToXml(model));
		  response.getWriter().flush();
	  }catch(Exception e){
		  throw new ViewException(e);
	  }
  }

}
