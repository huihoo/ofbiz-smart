package org.huihoo.samples.petclinic.model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.huihoo.ofbiz.smart.base.validation.Required;

@Entity
@Table(name = "owners")
public class Owner extends Person {
  @Required
  private String address;

  private String city;

  private String telephone;
  
  @OneToMany
  @OrderBy("createdAt desc")
  private Set<Pet> pets;

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getTelephone() {
    return telephone;
  }

  public void setTelephone(String telephone) {
    this.telephone = telephone;
  }

  public Set<Pet> getPets() {
    return pets;
  }

  public void setPets(Set<Pet> pets) {
    this.pets = pets;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Owner [address=");
    builder.append(address);
    builder.append(", city=");
    builder.append(city);
    builder.append(", telephone=");
    builder.append(telephone);
    builder.append(", pets=");
    builder.append(pets);
    builder.append("]");
    return builder.toString();
  }
  
  
}
