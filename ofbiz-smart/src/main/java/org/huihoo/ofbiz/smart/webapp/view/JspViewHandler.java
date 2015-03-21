package org.huihoo.ofbiz.smart.webapp.view;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.huihoo.ofbiz.smart.base.C;

public class JspViewHandler implements ViewHandler {

  @Override
  public void render(String page, String layout, HttpServletRequest req, HttpServletResponse resp)
          throws ViewHandlerException {
    String jspViewBasePath = (String) req.getAttribute("jspViewBasePath");
    if(jspViewBasePath == null)
      jspViewBasePath = "/WEB-INF/jsp";
    RequestDispatcher dispatcher = null;
    if (C.RESP_NONE.equals(layout)) {
      dispatcher = req.getRequestDispatcher(jspViewBasePath+page);
    }else{
      dispatcher = req.getRequestDispatcher(jspViewBasePath+(layout == null ? "/layout.jsp" : layout));
      req.setAttribute("pageContent", jspViewBasePath+page);
    }

    try {
      dispatcher.forward(req, resp);
    } catch (ServletException e) {
      throw new ViewHandlerException(e);
    } catch (IOException e) {
      throw new ViewHandlerException(e);
    }

  }

}
