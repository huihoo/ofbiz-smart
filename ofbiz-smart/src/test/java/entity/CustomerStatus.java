package entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.Model;


@Entity
@Table(name = "test_customer_status")
public class CustomerStatus extends Model {
  @Id
  String code;

  String title;

  public String getCode() {
    return code;
  }

  public String getTitle() {
    return title;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public void setTitle(String title) {
    this.title = title;
  }
}
