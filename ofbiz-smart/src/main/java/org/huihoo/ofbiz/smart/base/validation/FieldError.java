package org.huihoo.ofbiz.smart.base.validation;

public class FieldError {
  private String name;
  private String message;


  public FieldError(String name, String message) {
    this.name = name;
    this.message = message;
  }


  public String getName() {
    return name;
  }


  @Override
  public String toString() {
    return "FieldError [name=" + name + ", message=" + message + "]";
  }


  public void setName(String name) {
    this.name = name;
  }


  public String getMessage() {
    return message;
  }


  public void setMessage(String message) {
    this.message = message;
  }

}
