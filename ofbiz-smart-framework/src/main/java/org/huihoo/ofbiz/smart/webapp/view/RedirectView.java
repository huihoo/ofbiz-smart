 package org.huihoo.ofbiz.smart.webapp.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.huihoo.ofbiz.smart.base.C;
import org.huihoo.ofbiz.smart.base.util.CommUtil;
import org.huihoo.ofbiz.smart.base.util.Log;

public class RedirectView implements View {
  
  private final static String TAG = RedirectView.class.getName();

  @Override
  public String getContentType() {
    return C.DEFAULT_CONTENT_TYPE;
  }

  @Override
  public void render(Map<String, Object> model, HttpServletRequest request,
          HttpServletResponse response) {
    String redirectUrl = null;
    if (CommUtil.isNotEmpty(model)) {
      redirectUrl = (String) model.get(C.REDIRECT_FLAG_ATTRIBUTE);
      //TODO {val} replace
    }
    
    if (redirectUrl == null) {
      redirectUrl = (String) request.getAttribute(C.JSP_VIEW_NAME_ATTRIBUTE);
      
      if (CommUtil.isEmpty(redirectUrl) || redirectUrl.startsWith("/WEB-INF/")) {
        redirectUrl = (String) request.getAttribute("redirectUrl");
      }
      
      if (redirectUrl == null) {
        redirectUrl = request.getContextPath() + "/" ;
      }
      
    }
    
    String encodedRedirectURL = response.encodeRedirectURL(redirectUrl);
    Log.d("Redirect to [%s]", TAG,encodedRedirectURL);
    response.setStatus(303);//http://tools.ietf.org/html/rfc7231#section-6.4.4
    response.setHeader("Location", encodedRedirectURL);
  }
}
