package org.huihoo.ofbiz.smart.entity;

import org.huihoo.ofbiz.smart.base.GeneralException;





public class GenericEntityException extends GeneralException {
  private static final long serialVersionUID = 1L;

  public GenericEntityException() {
    super();
  }

  public GenericEntityException(Throwable nested) {
    super(nested);
  }

  public GenericEntityException(String str) {
    super(str);
  }

  public GenericEntityException(String str, Throwable nested) {
    super(str, nested);
  }
}
