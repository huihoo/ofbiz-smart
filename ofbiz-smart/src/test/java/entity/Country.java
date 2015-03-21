package entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.Model;


@Entity
@Table(name = "test_country")
public class Country extends Model {
  @Id
  String code;

  String name;

  public String getCode() {
    return code;
  }

  public String getName() {
    return name;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Country [code=");
    builder.append(code);
    builder.append(", name=");
    builder.append(name);
    builder.append("]");
    return builder.toString();
  }


}
