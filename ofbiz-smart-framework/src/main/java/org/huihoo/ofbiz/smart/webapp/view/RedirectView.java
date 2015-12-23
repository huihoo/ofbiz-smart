package org.huihoo.ofbiz.smart.webapp.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.huihoo.ofbiz.smart.base.C;

public class RedirectView implements View {

  @Override
  public String getContentType() {
    return C.DEFAULT_CONTENT_TYPE;
  }

  @Override
  public void render(Map<String, Object> model, HttpServletRequest request,
          HttpServletResponse response) {
    String targetUrl = "";
    String encodedRedirectURL = response.encodeRedirectURL(targetUrl);
    response.setStatus(303);//http://tools.ietf.org/html/rfc7231#section-6.4.4
    response.setHeader("Location", encodedRedirectURL);
  }
}
