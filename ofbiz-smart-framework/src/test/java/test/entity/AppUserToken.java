package test.entity;



import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.WhenCreated;
import com.avaje.ebean.annotation.WhenModified;

@Entity
@Table(name = "app_user_access_tokens")
public class AppUserToken extends Model {
  @Id
  Long id;

  int expiredIn;

  String accessToken;

  String userId;

  String deviceId;

  @WhenCreated
  Date createdAt;

  @WhenModified
  Date updatedAt;

  public Long getId() {
    return id;
  }

  public int getExpiredIn() {
    return expiredIn;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public String getUserId() {
    return userId;
  }

  public String getDeviceId() {
    return deviceId;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setExpiredIn(int expiredIn) {
    this.expiredIn = expiredIn;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public Date getUpdatedAt() {
    return updatedAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
  }
}
