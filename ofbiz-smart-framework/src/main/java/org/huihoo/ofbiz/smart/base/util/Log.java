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



public class Log {

  public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

  public static final int TRACE = 0;
  public static final int DEBUG = 1;
  public static final int INFO = 2;
  public static final int WARING = 3;
  public static final int ERROR = 4;


  public static Logger getLogger(String tag) {
    return LoggerFactory.getLogger(tag);
  }

  public static void log(int level, String tag, String msg, Throwable t, Object... params) {
    Logger logger = getLogger(tag);

    if (msg != null && params.length > 0) {
      StringBuilder sb = new StringBuilder();
      Formatter formatter = new Formatter(sb);
      formatter.format(msg, params);
      msg = sb.toString();
      formatter.close();
    }
    switch (level) {
      case Log.TRACE:
        if (!logger.isTraceEnabled()) return;
        if (t == null)
          logger.trace(msg);
        else
          logger.trace(msg, t);
        break;
      case Log.INFO:
        if (t == null)
          logger.info(msg);
        else
          logger.info(msg, t);
        break;
      case Log.WARING:
        if (t == null)
          logger.warn(msg);
        else
          logger.warn(msg, t);
        break;
      case Log.ERROR:
        if (t == null)
          logger.error(msg);
        else
          logger.error(msg, t);
        break;
      case Log.DEBUG:
        if (!logger.isDebugEnabled()) return;
        if (t == null)
          logger.debug(msg);
        else
          logger.debug(msg, t);
        break;
    }


  }


  public static void t(Throwable t, String msg, String tag, Object... params) {
    log(Log.TRACE, tag, msg, t, params);
  }

  public static void t(Throwable t, String msg, String tag) {
    log(Log.TRACE, tag, msg, t, EMPTY_OBJECT_ARRAY);
  }

  public static void t(String msg, String tag) {
    log(Log.TRACE, tag, msg, null, EMPTY_OBJECT_ARRAY);
  }

  public static void t(String msg, String tag, Object... params) {
    log(Log.TRACE, tag, msg, null, params);
  }

  public static void d(Throwable t, String msg, String tag, Object... params) {
    log(Log.DEBUG, tag, msg, t, params);
  }

  public static void d(Throwable t, String msg, String tag) {
    log(Log.DEBUG, tag, msg, t, EMPTY_OBJECT_ARRAY);
  }

  public static void d(String msg, String tag) {
    log(Log.DEBUG, tag, msg, null, EMPTY_OBJECT_ARRAY);
  }

  public static void d(String msg, String tag, Object... params) {
    log(Log.DEBUG, tag, msg, null, params);
  }

  public static void i(Throwable t, String msg, String tag, Object... params) {
    log(Log.INFO, tag, msg, t, params);
  }

  public static void i(Throwable t, String msg, String tag) {
    log(Log.INFO, tag, msg, t, EMPTY_OBJECT_ARRAY);
  }

  public static void i(String msg, String tag) {
    log(Log.INFO, tag, msg, null, EMPTY_OBJECT_ARRAY);
  }

  public static void i(String msg, String tag, Object... params) {
    log(Log.INFO, tag, msg, null, params);
  }

  public static void w(Throwable t, String msg, String tag, Object... params) {
    log(Log.WARING, tag, msg, t, params);
  }



  public static void w(Throwable t, String msg, String tag) {
    log(Log.WARING, tag, msg, t, EMPTY_OBJECT_ARRAY);
  }


  public static void w(String msg, String tag) {
    log(Log.WARING, tag, msg, null, EMPTY_OBJECT_ARRAY);
  }


  public static void w(String msg, String tag, Object... params) {
    log(Log.WARING, tag, msg, null, params);
  }



  public static void e(Throwable t, String msg, String tag, Object... params) {
    log(Log.ERROR, tag, msg, t, params);
  }

  public static void e(Throwable t, String msg, String tag) {
    log(Log.ERROR, tag, msg, t, EMPTY_OBJECT_ARRAY);
  }

  public static void e(String msg, String tag) {
    log(Log.ERROR, tag, msg, null, EMPTY_OBJECT_ARRAY);
  }

  public static void e(String msg, String tag, Object... param) {
    log(Log.ERROR, tag, msg, null, param);
  }

}
