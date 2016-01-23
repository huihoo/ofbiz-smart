package org.huihoo.ofbiz.smart.webapp.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.huihoo.ofbiz.smart.base.C;

public class ThymeleafView implements View {

  @Override
  public String getContentType() {
    return C.DEFAULT_CONTENT_TYPE;
  }

  @Override
  public void render(Map<String, Object> model, HttpServletRequest request,
          HttpServletResponse response) throws ViewException {
    //TODO
  }

}
