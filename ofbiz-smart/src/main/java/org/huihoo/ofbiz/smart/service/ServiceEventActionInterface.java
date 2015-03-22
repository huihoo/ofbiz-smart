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
package org.huihoo.ofbiz.smart.service;

import java.util.Map;

/**
 * <p>
 * 服务事件处理接口类
 * </p>
 * 
 * @author huangbohua
 *
 */
public interface ServiceEventActionInterface {
  /**
   * <p>
   * 在服务调用之前执行
   * </p>
   * 
   * @param context 要调用的服务上下文<code>Map</code>对象
   */
  public void before(Map<String, Object> context);

  /**
   * <p>
   * 在服务调用成功之后执行
   * </p>
   * 
   * @param context 要调用的服务上下文<code>Map</code>对象
   * @param result 服务调用完成后<code>Map</code>对象
   */
  public void success(Map<String, Object> context, Map<String, Object> result);
}
