/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/
package org.huihoo.ofbiz.smart.base;

public class GeneralException extends Exception {
  private static final long serialVersionUID = 1L;


  public GeneralException() {
    super();
  }


  public GeneralException(String msg) {
    super(msg);
  }


  public GeneralException(String msg, Throwable nested) {
    super(msg, nested);
  }


  public GeneralException(Throwable nested) {
    super(nested);
  }

  @Override
  public String getMessage() {
    Throwable nested = getCause();
    if (nested != null) {
      if (super.getMessage() == null) {
        return nested.getMessage();
      } else {
        return super.getMessage() + " (" + nested.getMessage() + ")";
      }
    } else {
      return super.getMessage();
    }
  }
}
