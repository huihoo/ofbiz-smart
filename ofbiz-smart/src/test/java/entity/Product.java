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
