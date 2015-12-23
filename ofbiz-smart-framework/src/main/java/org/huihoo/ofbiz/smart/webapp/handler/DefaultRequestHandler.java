package org.huihoo.ofbiz.smart.webapp.handler;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.huihoo.ofbiz.smart.base.util.CommUtil;
import org.huihoo.ofbiz.smart.webapp.view.JspView;
import org.huihoo.ofbiz.smart.webapp.view.ViewException;

public class DefaultRequestHandler implements RequestHandler {
  @Override
  public void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    JspView jspView = new JspView();
    try {
      jspView.render(CommUtil.toMap("",""), req, resp);
    } catch (ViewException e) {
      e.printStackTrace();
    }
  }
}
