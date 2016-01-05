 package org.huihoo.ofbiz.smart.webapp.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.huihoo.ofbiz.smart.base.C;
import org.huihoo.ofbiz.smart.base.util.CommUtil;

public class RedirectView implements View {

  @Override
  public String getContentType() {
    return C.DEFAULT_CONTENT_TYPE;
  }

  @Override
  public void render(Map<String, Object> model, HttpServletRequest request,
          HttpServletResponse response) {
    String targetUri = (String) request.getAttribute("targetUri");
    if (CommUtil.isEmpty(targetUri)) {
      targetUri = request.getContextPath() + "/";
    }
    String encodedRedirectURL = response.encodeRedirectURL(targetUri);
    response.setStatus(303);//http://tools.ietf.org/html/rfc7231#section-6.4.4
    response.setHeader("Location", encodedRedirectURL);
  }
}
