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
