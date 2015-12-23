package org.huihoo.samples.petclinic.model;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class NamedModel extends BaseModel {
  private String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
