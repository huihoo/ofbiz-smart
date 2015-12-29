package org.huihoo.samples.petclinic.model;

import javax.persistence.MappedSuperclass;

import org.huihoo.ofbiz.smart.base.validation.Required;

@MappedSuperclass
public class Person extends BaseModel {

  @Required
  protected String firstName;


  protected String lastName;

  public String getFirstName() {
    return this.firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return this.lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Person [firstName=");
    builder.append(firstName);
    builder.append(", lastName=");
    builder.append(lastName);
    builder.append("]");
    return builder.toString();
  }
}
