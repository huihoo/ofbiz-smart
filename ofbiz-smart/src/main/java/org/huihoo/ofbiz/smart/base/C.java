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
package org.huihoo.ofbiz.smart.base;


/**
 * <p>
 * 常量类，它持有框架的一些不变值和标识Key
 * </p>
 * 
 * @author huangbohua
 * @since 1.0
 */
public class C {
  /**
   * 开发模式的标识
   */
  public static final String ENV_MODE_DEVELOP = "develop";
  /**
   * 生产模式的标识
   */
  public static final String ENV_MODE_PRODUCTION = "production";

  /**
   * 标识请求的 {@link com.dnx.webapp.control.ConfigXMLLoader.Event} 类型为 api 类型,表示普通的API请求
   */
  public static final String EVENT_API = "api";

  /**
   * 标识请求的 {@link com.dnx.webapp.control.ConfigXMLLoader.Event} 类型为 java 类型,表示普通的Java方法调用
   */
  public static final String EVENT_JAVA = "java";
  /**
   * 标识请求的 {@link com.dnx.webapp.control.ConfigXMLLoader.Event} 类型为 service(服务) 类型,表示配置的服务调用
   */
  public static final String EVENT_SERVICE = "service";
  /**
   * 标识请求的 {@link com.dnx.webapp.control.ConfigXMLLoader.Event} 类型为 none 类型,表示无任何处理
   */
  public static final String EVENT_NONE = "none";
  /**
   * 标识请求的 {@link com.dnx.webapp.control.ConfigXMLLoader.Event} 类型为 auto 类型,表示框架自动处理
   */
  public static final String EVENT_AUTO = "auto";
  /**
   * 标识请求的 {@link com.dnx.webapp.control.ConfigXMLLoader.Event} 类型为 simple 类型,表示服务类型为简单服务的调用
   */
  public static final String EVENT_SIMPLE = "simple";

  /**
   * 标识请求的 {@link com.dnx.webapp.control.ConfigXMLLoader.Response} 类型为 include-view 类型,表示响应返回为
   * 服务器内部包含的页面
   */
  public static final String RESP_INCLUDE_VIEW = "include-view";

  /**
   * 标识请求的 {@link com.dnx.webapp.control.ConfigXMLLoader.Response} 类型为 redirect 类型,表示响应为一个重定向
   */
  public static final String RESP_REDIRECT = "redirect";
  /**
   * 标识请求的 {@link com.dnx.webapp.control.ConfigXMLLoader.Response} 类型为 json 类型,表示响应为标准的JSON字符串
   */
  public static final String RESP_JSON = "json";
  /**
   * 标识请求的 {@link com.dnx.webapp.control.ConfigXMLLoader.Response} 类型为 html 类型,表示响应为标准的HTML文本内容
   */
  public static final String RESP_HTML = "html";
  /**
   * 标识请求的 {@link com.dnx.webapp.control.ConfigXMLLoader.Response} 类型为 none 类型,表示无任何响应动作
   */
  public static final String RESP_NONE = "none";

  /**
   * 标识请求 接受任何方式
   */
  public static final String ALLOW_ALL = "all";

  /**
   * 表示当前环境为Web环境的Key
   */
  public static final String IS_WEB_CONTEXT = "__is_web_context__";

  /**
   * 表示请求路径后缀的Key
   */
  public static final String ACTION_URI_SUFFIX = "uriSuffix";

  /**
   * 表示Web上下文 <code>HttpServletRequest</code> 对象的Key
   */
  public static final String CTX_REQUEST = "__request__";

  /**
   * 表示Web上下文 <code>HttpServletResponse</code> 对象的Key
   */
  public static final String CTX_RESPONSE = "__response__";

  /**
   * 表示持久化上下文 {@link com.dnx.entity.Delegator} 对象的Key
   */
  public static final String CTX_DELEGATOR = "__delegator__";

  /**
   * 表示服务上下文 {@link com.dnx.service.ServiceDispatcher} 对象的Key
   */
  public static final String CTX_SERVICE_DISPATCHER = "__service_dispatcher__";

  /**
   * 上下文持有应用配置对象Properties实例的key
   */
  public static final String CTX_APP_CONFIG_PROP = "__app_config_prop__";

  /**
   * 用于保存正确提示信息的 Key
   */
  public static final String TIP_FLASH_SUCCESS = "flashSuccess";

  /**
   * 用于保存错误提示信息的 Key
   */
  public static final String TIP_FLASH_ERROR = "flashError";


  /**
   * 标识是否是对外提供API服务的 key
   */
  public static final String IS_EXPORT_API = "__is_export_api__";

  /**
   * 标识API登录验证key
   */
  public static final String API_LOGIN_AUTH_KEY = "api_login_auth_key";


}
