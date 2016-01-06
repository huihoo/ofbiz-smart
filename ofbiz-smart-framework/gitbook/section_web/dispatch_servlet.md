# DispatchServlet

> DispatchServlet是一个标准的自启动HttpServlet。

> DispatchServlet是所有请求的入口，它负责请求的转发。

> DispatchServlet启动时，会自动加载 Action配置文件。

> DispatchServlet启动时，会加载内置的界面(View)和配置的界面(View)。

> DispatchServlet会记录所有的请求响应时间。

## Init

初始化参数

名称                                   | 描述                                    
:-----------:| :-----------
jsp-view-base-path | JSP界面的根目录，相对于/WEB-INF/目录   
uri-suffix        | 请求的后缀标识
http-api-uri-base | Http API请求处理的URI根路径
rest-api-uri-base | Rest API请求处理的URI根路径
api-doc-uri-base  | Api Doc请求处理的URI根路径 

### 配置示例：

```
<servlet>
	<servlet-name>SmartServlet</servlet-name>
	<servlet-class>org.huihoo.ofbiz.smart.webapp.DispatchServlet</servlet-class>
	<init-param>
		<param-name>jsp-view-base-path</param-name>
		<param-value>/WEB-INF/views</param-value>
	</init-param>
	<init-param>
		<param-name>uri-suffix</param-name>
		<param-value>.htm</param-value>
	</init-param>
	<init-param>
		<param-name>http-api-uri-base</param-name>
		<param-value>/api</param-value>
	</init-param>
	<init-param>
		<param-name>rest-api-uri-base</param-name>
		<param-value>/rest</param-value>
	</init-param>
	<init-param>
		<param-name>api-doc-uri-base</param-name>
		<param-value>/doc</param-value>
	</init-param>
	<load-on-startup>1</load-on-startup>
</servlet>

<servlet-mapping>
	<servlet-name>SmartServlet</servlet-name>
	<url-pattern>.htm</url-pattern>
</servlet-mapping>
```