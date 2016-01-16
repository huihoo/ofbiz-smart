package org.huihoo.ofbiz.smart.base.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

  // SimpleDateFormat是非线程安全的，所以通过ThreadLocal在当前执行线程中拷贝一个它的实例
  private static final ThreadLocal<SimpleDateFormat> formatter = new ThreadLocal<SimpleDateFormat>() {
            @Override
            protected SimpleDateFormat initialValue() {
              return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            }
          };



  public static String format(Date date) {

    return formatter.get().format(date);
  }

  public static String format(java.sql.Date date) {

    return formatter.get().format(date);
  }
}
