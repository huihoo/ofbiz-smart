package org.huihoo.samples.petclinic.model;

import javax.persistence.MappedSuperclass;

import org.huihoo.ofbiz.smart.base.validation.Required;

@MappedSuperclass
public class NamedModel extends BaseModel {
  @Required
  private String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
