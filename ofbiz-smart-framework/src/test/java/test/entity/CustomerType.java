package test.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.WhenCreated;
import com.avaje.ebean.annotation.WhenModified;

@Entity
@Table(name = "customer_type")
public class CustomerType extends Model {
  @Id
  Long id;

  String name;

  @WhenCreated
  Date createdAt;

  @WhenModified
  Date updatedAt;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public Date getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
  }
}
