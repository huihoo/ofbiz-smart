package org.huihoo.ofbiz.smart.base;

public class GeneralException extends Exception {
  private static final long serialVersionUID = 1L;


  public GeneralException() {
    super();
  }


  public GeneralException(String msg) {
    super(msg);
  }


  public GeneralException(String msg, Throwable nested) {
    super(msg, nested);
  }


  public GeneralException(Throwable nested) {
    super(nested);
  }

  @Override
  public String getMessage() {
    Throwable nested = getCause();
    if (nested != null) {
      if (super.getMessage() == null) {
        return nested.getMessage();
      } else {
        return super.getMessage() + " (" + nested.getMessage() + ")";
      }
    } else {
      return super.getMessage();
    }
  }
}
