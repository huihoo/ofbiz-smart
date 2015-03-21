package entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name = "test_customer")
public class Customer extends BaseModel {
  @ManyToOne
  @JoinColumn(name="status_code")
  CustomerStatus statusCode;

  String name;

  @ManyToOne
  Address shippingAddress;

  @ManyToOne
  Address billingAddress;

  public CustomerStatus getStatusCode() {
    return statusCode;
  }

  public String getName() {
    return name;
  }

  public Address getShippingAddress() {
    return shippingAddress;
  }

  public Address getBillingAddress() {
    return billingAddress;
  }

  public void setStatusCode(CustomerStatus statusCode) {
    this.statusCode = statusCode;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setShippingAddress(Address shippingAddress) {
    this.shippingAddress = shippingAddress;
  }

  public void setBillingAddress(Address billingAddress) {
    this.billingAddress = billingAddress;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Customer [statusCode=");
    builder.append(statusCode);
    builder.append(", name=");
    builder.append(name);
    builder.append(", shippingAddress=");
    builder.append(shippingAddress);
    builder.append(", billingAddress=");
    builder.append(billingAddress);
    builder.append("]");
    return builder.toString();
  }


}
