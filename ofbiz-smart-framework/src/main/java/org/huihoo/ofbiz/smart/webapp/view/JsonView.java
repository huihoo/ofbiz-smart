package org.huihoo.ofbiz.smart.webapp.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.huihoo.ofbiz.smart.base.C;
import org.json.JSONObject;

public class JsonView implements View {

  @Override
  public String getContentType() {
    return C.JSON_CONTENT_TYPE;
  }

  @Override
  public void render(Map<String, Object> model, HttpServletRequest request,
          HttpServletResponse response) throws ViewException{
	  //TODO
	  JSONObject jsonObject = new JSONObject(model);
	  response.setContentType(getContentType());
	  try{
		  response.getWriter().write(jsonObject.toString());
		  response.getWriter().flush();
	  }catch(Exception e){
		   throw new ViewException(e);
	  }
  }
  
}
