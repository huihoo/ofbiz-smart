package entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name = "test_order")
public class Order extends BaseModel {
  @ManyToOne
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


}
