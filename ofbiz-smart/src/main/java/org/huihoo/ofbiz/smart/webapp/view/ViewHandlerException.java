package org.huihoo.ofbiz.smart.webapp.view;

import org.huihoo.ofbiz.smart.base.GeneralException;



public class ViewHandlerException extends GeneralException {
  private static final long serialVersionUID = 1L;

  public ViewHandlerException() {
    super();
  }

  public ViewHandlerException(String msg) {
    super(msg);
  }

  public ViewHandlerException(Throwable t) {
    super(t);
  }

  public ViewHandlerException(String msg, Throwable t) {
    super(msg, t);
  }
}
