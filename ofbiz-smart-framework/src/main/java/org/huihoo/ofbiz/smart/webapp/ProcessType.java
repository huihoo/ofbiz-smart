package org.huihoo.ofbiz.smart.webapp;

public enum ProcessType {

  URI_AUTO("URI路径自动匹配", "uriAuto"), ENTITY_AUTO("根据实体自动匹配", "entityAuto"), BY_CONFIG("完全根据配置匹配", "byConfig");

  private final String label;
  private final String value;

  private ProcessType(String label, String value) {
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
