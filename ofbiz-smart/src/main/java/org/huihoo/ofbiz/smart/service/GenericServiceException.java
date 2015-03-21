package org.huihoo.ofbiz.smart.service;

import org.huihoo.ofbiz.smart.base.GeneralException;





public class GenericServiceException extends GeneralException {
  private static final long serialVersionUID = 1L;

  public GenericServiceException() {
    super();
  }

  public GenericServiceException(String msg) {
    super(msg);
  }

  public GenericServiceException(Throwable t) {
    super(t);
  }

  public GenericServiceException(String msg, Throwable t) {
    super(msg, t);
  }
}
