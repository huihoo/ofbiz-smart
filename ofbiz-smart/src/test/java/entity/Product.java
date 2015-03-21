package entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "test_product")
public class Product extends BaseModel {
  String sku;
  String name;

  public String getSku() {
    return sku;
  }

  public String getName() {
    return name;
  }

  public void setSku(String sku) {
    this.sku = sku;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Product [sku=");
    builder.append(sku);
    builder.append(", name=");
    builder.append(name);
    builder.append("]");
    return builder.toString();
  }
}
