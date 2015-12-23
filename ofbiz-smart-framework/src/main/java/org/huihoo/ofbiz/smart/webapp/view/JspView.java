package org.huihoo.ofbiz.smart.webapp.view;

import java.io.IOException;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.huihoo.ofbiz.smart.base.C;
import org.huihoo.ofbiz.smart.base.util.Log;

public class JspView implements View {
  private final static String TAG = JspView.class.getName();

  @Override
  public String getContentType() {
    return C.DEFAULT_CONTENT_TYPE;
  }

  @Override
  public void render(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response)
      throws ViewException {
    
    setModelAsRequestAttributies(model,request);
    
    String uri = request.getRequestURI();
    uri = "/WEB-INF/views/owners/findOwners.jsp";
    RequestDispatcher rd = request.getRequestDispatcher(uri);
    if (rd == null) {
      throw new ViewException("Could not get RequestDispatcher for [" + uri + "]");
    }
    request.setAttribute("name", "黄柏华");
    boolean isIncludeRequest = request.getAttribute(C.INCLUDE_REQUEST_URI_ATTRIBUTE) != null || response.isCommitted();
    try {
      if (isIncludeRequest) {
        Log.d("Including resource [" + uri + "] in JspView.", TAG);
        response.setContentType(getContentType());
        rd.include(request, response);
      } else {
        Log.d("Forwarding to resource [" + uri + "] in JspView.", TAG);
        rd.forward(request, response);
      }
    } catch (IOException e) {
      throw new ViewException(e);
    } catch (ServletException e) {
      throw new ViewException(e);
    }
  }


  protected void setModelAsRequestAttributies(Map<String, Object> model, HttpServletRequest request) {
    for (Map.Entry<String, Object> entry : model.entrySet()) {
      String modelName = entry.getKey();
      Object modelValue = entry.getValue();
      if (modelValue != null) {
        request.setAttribute(modelName, modelValue);
      } else {
        request.removeAttribute(modelName);
      }
    }
  }

}
