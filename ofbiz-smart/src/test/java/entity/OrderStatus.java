package entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.Model;


@Entity
@Table(name = "test_order_status")
public class OrderStatus extends Model {
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

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("OrderStatus [code=");
    builder.append(code);
    builder.append(", title=");
    builder.append(title);
    builder.append("]");
    return builder.toString();
  }
}
