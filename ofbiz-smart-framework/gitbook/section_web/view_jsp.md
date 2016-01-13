# JspView

> 基于JSP2.0，框架默认的动态界面实现

> 当[view-type](action.md#response)指定为**jsp**启用
    >```xml
     <action uri="/home">
		<response view-type="jsp" view-name="/index.jsp"/>
	 </action>
    >```

> 具体的渲染jsp页面路径由 **HttpServletRequest**请求对象中的 **viewName** 属性指定

> 实际的jsp页面转发方式为 include 或 forward

> 服务调用的返回**Map**，可以在jsp页面中直接读取； 请求的参数，也可以在jsp页面中直接读取
   >```java
     //服务调用返回结果Map的结构为：{name=hbh,age=12}
     //在jsp页面可以直接读取：${name}${age}
     //请求参数：a=1&b=2&c=3
     //在jsp页面可以直接读取：${a}${b}${c}
   >```
