package org.huihoo.ofbiz.smart.webapp.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface RequestHandler {
  void handleRequest(HttpServletRequest req, HttpServletResponse resp);
}