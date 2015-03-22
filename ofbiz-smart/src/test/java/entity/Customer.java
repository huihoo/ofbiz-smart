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
