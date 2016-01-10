# 日志

OFBiz Smart的日志处理基于[slf4j](http://www.slf4j.org/)和[logback](http://logback.qos.ch/)。

# Log

日志处理工具类

```java
 //log trace
 void t(Throwable t, String msg, String tag, Object... params)
 void t(Throwable t, String msg, String tag) 
 void t(String msg, String tag) 
 void t(String msg, String tag, Object... params) 
 //log debug 
 void d(Throwable t, String msg, String tag, Object... params) 
 void d(Throwable t, String msg, String tag) 
 void d(String msg, String tag) 
 void d(String msg, String tag, Object... params) 
 //log info
 void i(Throwable t, String msg, String tag, Object... params) 
 void i(Throwable t, String msg, String tag) 
 void i(String msg, String tag) 
 void i(String msg, String tag, Object... params) 
 //log warn
 void w(Throwable t, String msg, String tag, Object... params) 
 void w(Throwable t, String msg, String tag) 
 void w(String msg, String tag) 
 void w(String msg, String tag, Object... params) 
 //log error
 void e(Throwable t, String msg, String tag, Object... params) 
 void e(Throwable t, String msg, String tag) 
 void e(String msg, String tag) 
 void e(String msg, String tag, Object... param) 
  
```

示例:

```
 private final static String TAG = OrderService.class.getName();
 Log.d("debug message",TAG);
 Log.d("debug message val1[%s] val2[%s]",TAG,"hbh","abc");
 
 Log.e(exception,"error message",TAG);
 Log.e(exception,"error message val1[%s] val2[%s]",TAG,"hbh","abc");
```
