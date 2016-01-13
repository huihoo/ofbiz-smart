# Action配置

> Action配置必须是一个以action-map开头命名的xml文件，只有这种类型的文件，才会被加载。

> 该xml文件的跟元素为 action-map

示例：

```java
<action-map xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:noNamespaceSchemaLocation="http://huihoo.com/xsd/action-map.xsd">
</action-map>
```

## 配置文件元素定义

### action元素

#### action属性

名称                                   | 描述                                    
:-----------:| :-----------
uri          | 请求uri路径    
method       | 请求的方法，仅支持get,post
require-auth | 是否需要认证，true是，false否，默认为false  
process-type | 请求的处理方式，[uriAuto(根据URI自动匹配)](req_default.md#uriauto),[entityAuto(根据实体名称自动匹配)](req_default.md#entityauto),[byConfig(根据配置空全匹配)](req_default.md#byconfig)
nav-tag      | 导航标签名，用于导航选中的情况。 

示例：

```
<action uri="/index" process-type="uriAuto"  nav-tag="home",method="get">
	<response layout="/layout/layout.jsp" />
</action>
```

#### action子元素

名称                                   | 描述                                    
:-----------:| :-----------
page-title | 页面的标题    
more-css  | 引用的其它样式,多个以逗号隔开
more-javascripts | 引用的其它js,多个以逗号隔开
[service-call](#service-call) | 服务调用，可以有多个
[response](#response)      | 响应元素

示例：

```
<action uri="/pet/create" process-type="entityAuto"  nav-tag="pet">
	<service-call service-name="entityAuto#findById" 
	              entity-name="org.huihoo.samples.petclinic.model.Owner"
	              result-name="owner" 
	              param-pairs="id,requestScope.ownerId"/>
	<service-call service-name="entityAuto#findListByAnd" 
	              entity-name="org.huihoo.samples.petclinic.model.PetType"
	              result-name="petTypes" />		              
	<response layout="/layout/layout.jsp"/>
</action>
```

##### service-call

> 定义了请求的服务调用

名称                                   | 描述                                    
:-----------:| :-----------
service-name | 服务的名称    
entity-name  | 实体的名称(如果有)
param-pairs | 参数对，以逗号隔开，必须是偶数个
result-name | 自定义服务调用的返回结果名称

##### response

> 定义了请求的输出响应

名称                                   | 描述                                    
:-----------:| :-----------
view-type | 界面类型  jsp,json,xml,html,redirect,pdf,excel,cvs等
view-name  | 界面的逻辑名称
layout | 界面应用的模板(如果有)

示例：

```
<action uri="/pet/update" process-type="entityAuto" method="post">
	<response view-type="redirect" view-name="/owner/view?id={model.owner.id}"/>
</action>
```
