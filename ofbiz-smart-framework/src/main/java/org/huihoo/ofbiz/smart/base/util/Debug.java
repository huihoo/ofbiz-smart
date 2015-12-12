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
package org.huihoo.ofbiz.smart.base.util;

import java.util.Formatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class Debug {

  public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

  public static final int TRACE = 0;
  public static final int DEBUG = 1;
  public static final int INFO = 2;
  public static final int WARING = 3;
  public static final int ERROR = 4;


  public static Logger getLogger(String module) {
    return LoggerFactory.getLogger(module);
  }

  public static void log(int level, String module, String msg, Throwable t, Object... params) {
    Logger logger = getLogger(module);

    if (msg != null && params.length > 0) {
      StringBuilder sb = new StringBuilder();
      Formatter formatter = new Formatter(sb);
      formatter.format(msg, params);
      msg = sb.toString();
      formatter.close();
    }
    switch (level) {
      case Debug.TRACE:
        if (!logger.isTraceEnabled()) return;
        if (t == null)
          logger.trace(msg);
        else
          logger.trace(msg, t);
        break;
      case Debug.INFO:
        if (t == null)
          logger.info(msg);
        else
          logger.info(msg, t);
        break;
      case Debug.WARING:
        if (t == null)
          logger.warn(msg);
        else
          logger.warn(msg, t);
        break;
      case Debug.ERROR:
        if (t == null)
          logger.error(msg);
        else
          logger.error(msg, t);
        break;
      case Debug.DEBUG:
        if (!logger.isDebugEnabled()) return;
        if (t == null)
          logger.debug(msg);
        else
          logger.debug(msg, t);
        break;
    }


  }


  public static void logTrace(Throwable t, String msg, String module, Object... params) {
    log(Debug.TRACE, module, msg, t, params);
  }

  public static void logTrace(Throwable t, String msg, String module) {
    log(Debug.TRACE, module, msg, t, EMPTY_OBJECT_ARRAY);
  }

  public static void logTrace(String msg, String module) {
    log(Debug.TRACE, module, msg, null, EMPTY_OBJECT_ARRAY);
  }

  public static void logTrace(String msg, String module, Object... params) {
    log(Debug.TRACE, module, msg, null, params);
  }

  public static void logDebug(Throwable t, String msg, String module, Object... params) {
    log(Debug.DEBUG, module, msg, t, params);
  }

  public static void logDebug(Throwable t, String msg, String module) {
    log(Debug.DEBUG, module, msg, t, EMPTY_OBJECT_ARRAY);
  }

  public static void logDebug(String msg, String module) {
    log(Debug.DEBUG, module, msg, null, EMPTY_OBJECT_ARRAY);
  }

  public static void logDebug(String msg, String module, Object... params) {
    log(Debug.DEBUG, module, msg, null, params);
  }

  public static void logInfo(Throwable t, String msg, String module, Object... params) {
    log(Debug.INFO, module, msg, t, params);
  }

  public static void logInfo(Throwable t, String msg, String module) {
    log(Debug.INFO, module, msg, t, EMPTY_OBJECT_ARRAY);
  }

  public static void logInfo(String msg, String module) {
    log(Debug.INFO, module, msg, null, EMPTY_OBJECT_ARRAY);
  }

  public static void logInfo(String msg, String module, Object... params) {
    log(Debug.INFO, module, msg, null, params);
  }

  public static void logWaring(Throwable t, String msg, String module, Object... params) {
    log(Debug.WARING, module, msg, t, params);
  }



  public static void logWarn(Throwable t, String msg, String module) {
    log(Debug.WARING, module, msg, t, EMPTY_OBJECT_ARRAY);
  }


  public static void logWarn(String msg, String module) {
    log(Debug.WARING, module, msg, null, EMPTY_OBJECT_ARRAY);
  }


  public static void logWarn(String msg, String module, Object... params) {
    log(Debug.WARING, module, msg, null, params);
  }



  public static void logError(Throwable t, String msg, String module, Object... params) {
    log(Debug.ERROR, module, msg, t, params);
  }

  public static void logError(Throwable t, String msg, String module) {
    log(Debug.ERROR, module, msg, t, EMPTY_OBJECT_ARRAY);
  }

  public static void logError(String msg, String module) {
    log(Debug.ERROR, module, msg, null, EMPTY_OBJECT_ARRAY);
  }

  public static void logError(String msg, String module, Object... param) {
    log(Debug.ERROR, module, msg, null, param);
  }

}
