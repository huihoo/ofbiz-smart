# RedirectView

> 重定向界面，通过设置响应头的状态为303,设置响应头部参数Location来实现

> 当[view-type](action.md#response)指定为**redirect**启用
    >```xml
     <action uri="/home">
		<response view-type="redirect" view-name="/redirect_to?a=1&b=2"/>
	 </action>
    >```

> 重定向路径的指定逻辑(依次按如下的顺序)

  1. 如果传入渲染方法中的model参数含有 ** redirect:// ** 属性，直接重定向至该路径
  2. 如果view-name属性有值,重定向至该路径
  3. 如果以上都没有值，从**HttpServletRequest**中获取**redirectUrl**属性，如果有值，重定向至该路径
  4. 以上都没有值，重定向至应用根路径
