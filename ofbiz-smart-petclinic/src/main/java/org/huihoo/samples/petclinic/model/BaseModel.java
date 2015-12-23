package org.huihoo.samples.petclinic.model;

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.avaje.ebean.annotation.WhenCreated;
import com.avaje.ebean.annotation.WhenModified;

@MappedSuperclass
public class BaseModel {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Integer id;

  @WhenCreated
  protected Date createdAt;

  @WhenModified
  protected Date updatedAt;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public boolean isNew() {
    return (this.id == null);
  }
}
