/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
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

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("OrderDetail [order=");
    builder.append(order);
    builder.append(", product=");
    builder.append(product);
    builder.append(", orderQty=");
    builder.append(orderQty);
    builder.append(", shipQty=");
    builder.append(shipQty);
    builder.append("]");
    return builder.toString();
  }


}
