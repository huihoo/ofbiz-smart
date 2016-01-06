# DefaultRequestHandler

> DefaultRequestHandler处理非 api,doc,restful形式的请求。

> DefaultRequestHandler根据action的配置进行实际的处理和界面渲染。

## uriAuto

它主要适用于需要渲染界面类型为**jsp**的请求。

当action的process-type为 **URI路径自动匹配**类型时，在界面根目录下必须存在一个和路径的层级和名称(除去后缀)一一对应的**jsp**页面。

假如界面的根目录为**/WEB-INF/views** 以如下配置为例:

```
// 必须要有一个 /WEB-INF/views/index.jsp的界面，并应用布局
<action uri="/index" process-type="uriAuto">
	<response layout="/layout/layout.jsp"/>
</action>

// 必须要有一个 /WEB-INF/views/product/detail.jsp的界面，并应用布局
<action uri="/product/detail" process-type="uriAuto">
	<response layout="/layout/layout.jsp"/>
</action>

// 必须要有一个 /WEB-INF/views/product/list.jsp的界面，不应用布局
<action uri="/product/list" process-type="uriAuto">
	<response layout="none"/>
</action>	

```

## entityAuto

当action的process-type为 **根据实体自动匹配**类型时，会自动根据请求URI路径字符串中是否含有对应实体的名称来进行各种操作和界面渲染

请求URI规则：

>  必须以 /entityName/verb 形式的字符串结尾

>  entityName为实体的名称，命名规则为骆驼式命名法

>  verb为操作的名称 目前支持  list,index,home,create,add,save,new,update,modify,edit,view,detail

实体名称的截取规则: 以/号分隔，倒数第二个为期待的实体名称，得到该值后，尝试加载对应的实体类(应用配置entity.scanning.packages属性 + 实体名称),如果能加载
则进行下面的操作和界面渲染,否则抛出异常。

路径和操作对应关系：

>  路径以  /list,/index,/home 结尾,进行实体查询操作和列表界面的渲染

>  路径以 /create,/add 结尾,不进行任何操作，直接渲染新增界面的渲染

>  路径以 /save,/new结尾,进行实体的保存操作，成功后跳转至实体详细信息界面,失败则返回到新增界面

>  路径以 /update,/modify结尾,进行实体的更新操作，成功后跳转至实体的详细信息界面，失败则返回到新界面

>  路径以 /edit 结尾,进行根据实体ID进行的查找操作，直接渲染编辑界面

>  路径以/view,/detail结尾,进行根据实体ID进行的查找操作，直接渲染实体的详细信息界面


请求和界面的对应关系:

>  路径以  /list,/index,/home 结尾 ，对应list.jsp

>  路径以 /create,/add 结尾，对应 form.jsp

>  路径以/view,/detail结尾，对应 view.jsp

>  路径以/edit 结尾，对应 form.jsp

示例：

```
//重写 create(如果有必要)，必须放在 /pet/** 配置之前。
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

//注意通配符号是两个 **
<action uri="/pet/**" process-type="entityAuto"  nav-tag="pet">
	<response layout="/layout/layout.jsp"/>
</action>

//必须要有的jsp界面 /WEB-INF/views/pet/list.jsp,/WEB-INF/views/pet/form.jsp,/WEB-INF/views/pet/view.jsp

```


## byConfig

当action的process-type为**完全根据配置匹配 **类型时，会严格按照配置来进行操作，如果没有完全匹配，则应用 [uriAuto](#uriAuto)，如果uriAuto还是没有满足，返回404.

示例：

```

<action uri="/pet/create" process-type="byConfig"  nav-tag="pet">
	<response layout="/layout/layout.jsp" view-name="/pet/create.jsp" view-type="jsp"/>
</action>

<action uri="/pet/ajaxJson" process-type="byConfig"  nav-tag="pet">
	<response view-type="json"/>
</action>

<action uri="/pet/ajaxXml" process-type="byConfig"  nav-tag="pet">
	<response view-type="xml"/>
</action>
```