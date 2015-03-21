package entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;



@Entity
@Table(name = "test_order_detail")
public class OrderDetail extends BaseModel {
  @ManyToOne
  Order order;

  @ManyToOne
  Product product;

  BigDecimal orderQty;

  BigDecimal shipQty;

  public Order getOrder() {
    return order;
  }

  public Product getProduct() {
    return product;
  }

  public BigDecimal getOrderQty() {
    return orderQty;
  }

  public BigDecimal getShipQty() {
    return shipQty;
  }

  public void setOrder(Order order) {
    this.order = order;
  }

  public void setProduct(Product product) {
    this.product = product;
  }

  public void setOrderQty(BigDecimal orderQty) {
    this.orderQty = orderQty;
  }

  public void setShipQty(BigDecimal shipQty) {
    this.shipQty = shipQty;
  }


}
