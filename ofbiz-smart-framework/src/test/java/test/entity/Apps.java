package test.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.WhenCreated;
import com.avaje.ebean.annotation.WhenModified;

@Entity
@Table(name = "apps")
public class Apps extends Model {
  @Id
  Long id;

  String appKey;

  String appSecret;

  String appStatus;

  @WhenCreated
  Date createdAt;

  @WhenModified
  Date updatedAt;

  public Long getId() {
    return id;
  }

  public String getAppKey() {
    return appKey;
  }

  public String getAppSecret() {
    return appSecret;
  }

  public String getAppStatus() {
    return appStatus;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public Date getUpdatedAt() {
    return updatedAt;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setAppKey(String appKey) {
    this.appKey = appKey;
  }

  public void setAppSecret(String appSecret) {
    this.appSecret = appSecret;
  }

  public void setAppStatus(String appStatus) {
    this.appStatus = appStatus;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
  }
}
