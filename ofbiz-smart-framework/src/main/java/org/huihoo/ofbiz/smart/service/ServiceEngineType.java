package org.huihoo.ofbiz.smart.service;

public enum ServiceEngineType {

  ENTITY_AUTO("实体自动处理引擎", "entityAuto"), JAVA("普通的Java服务引擎", "java");

  private final String label;
  private final String value;

  private ServiceEngineType(String label, String value) {
    this.label = label;
    this.value = value;
  }


  public String value() {
    return this.value;
  }

  public String label() {
    return this.label;
  }
}
