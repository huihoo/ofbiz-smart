package org.huihoo.samples.petclinic.model;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

@Entity
@Table(name = "pets")
public class Pet extends NamedModel {

  private Date birthdayDate;

  @ManyToOne
  private PetType type;

  @ManyToOne
  private Owner owner;


  @OneToMany(cascade = CascadeType.ALL, mappedBy = "pet", fetch = FetchType.EAGER)
  @OrderBy("createdAt desc")
  private Set<Visit> visits;


  public Date getBirthdayDate() {
    return birthdayDate;
  }


  public void setBirthdayDate(Date birthdayDate) {
    this.birthdayDate = birthdayDate;
  }


  public PetType getType() {
    return type;
  }


  public void setType(PetType type) {
    this.type = type;
  }


  public Owner getOwner() {
    return owner;
  }


  public void setOwner(Owner owner) {
    this.owner = owner;
  }


  public Set<Visit> getVisits() {
    return visits;
  }


  public void setVisits(Set<Visit> visits) {
    this.visits = visits;
  }


}
