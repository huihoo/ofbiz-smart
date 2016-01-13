package org.huihoo.ofbiz.smart.webapp.view;

import java.io.IOException;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.huihoo.ofbiz.smart.base.C;
import org.huihoo.ofbiz.smart.base.util.Log;
import org.huihoo.ofbiz.smart.webapp.WebAppManager;

public class JspView implements View {
  private final static String TAG = JspView.class.getName();

  @Override
  public String getContentType() {
    return C.DEFAULT_CONTENT_TYPE;
  }

  @Override
  public void render(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response)
      throws ViewException {
    WebAppManager.setModelAsRequestAttributies(model, request);
    String viewName = (String) request.getAttribute(C.JSP_VIEW_NAME_ATTRIBUTE);
    RequestDispatcher rd = request.getRequestDispatcher(viewName); 
    if (rd == null) {
      throw new ViewException("Could not get RequestDispatcher for [" + viewName + "]");
    }
    
    boolean isIncludeRequest = request.getAttribute(C.INCLUDE_REQUEST_URI_ATTRIBUTE) != null || response.isCommitted();
    try {
      response.setContentType(getContentType());
      if (isIncludeRequest) {
        Log.d("Including resource [" + viewName + "] in JspView.", TAG);
        rd.include(request, response);
      } else {
        Log.d("Forwarding to resource [" + viewName + "] in JspView.", TAG);
        rd.forward(request, response);
      }
    } catch (IOException e) {
      throw new ViewException(e);
    } catch (ServletException e) {
      throw new ViewException(e);
    }
  }


  

}
