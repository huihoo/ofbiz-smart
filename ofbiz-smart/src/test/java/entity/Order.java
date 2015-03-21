package entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name = "test_order")
public class Order extends BaseModel {
  @ManyToOne
  @JoinColumn(name="status_code")
  OrderStatus statusCode;

  @ManyToOne
  Customer customer;

  Date orderDate;

  Date shipDate;


  public OrderStatus getStatusCode() {
    return statusCode;
  }

  public Customer getCustomer() {
    return customer;
  }

  public Date getOrderDate() {
    return orderDate;
  }

  public Date getShipDate() {
    return shipDate;
  }

  public void setStatusCode(OrderStatus statusCode) {
    this.statusCode = statusCode;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
  }

  public void setOrderDate(Date orderDate) {
    this.orderDate = orderDate;
  }

  public void setShipDate(Date shipDate) {
    this.shipDate = shipDate;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Order [statusCode=");
    builder.append(statusCode);
    builder.append(", customer=");
    builder.append(customer);
    builder.append(", orderDate=");
    builder.append(orderDate);
    builder.append(", shipDate=");
    builder.append(shipDate);
    builder.append("]");
    return builder.toString();
  }



}
