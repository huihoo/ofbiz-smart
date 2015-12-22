package org.huihoo.ofbiz.smart.webapp.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RedirectView implements View {

  @Override
  public String getContentType() {
    return null;
  }

  @Override
  public void render(Map<String, ?> model, HttpServletRequest request,
          HttpServletResponse response) {

  }

}
