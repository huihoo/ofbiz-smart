首选必须在JSP页面头部中引入标签库

```java
 <%@ taglib prefix="hh" uri="http://huihoo.com/tags" %>
 
```

# 分页

分页标签生成如下结构的HTML代码

```html
 < 1 2 3 ... 4 5 >
 < 1 2 3 4 5 ...10 11... 90 91 >  
 
 //HTML代码为
 <ul class="">
  <li><a href="#"><i class=""></i></a></li>
  <li><a href="#">1</a></li>
  <li><a href="#">2</a></li>
  <li class=''><a href="#">3</a></li>
  <li><a href="#">...</a></li>
  <li><a href="#">4</a></li>
  <li><a href="#">5</a></li>
  <li><a href="#"><i class=""></i></a></li>
 </ul>
```

使用

```
<hh:pagination totalEntry="${totalEntry }" pageLink="" totalPage="${totalPage }"/>
```

属性

|名称|描述
|:------|:------|
|totalEntry|总记录数
|totalPage|总页数
|pageLink|分页链接,为空为当前请求链接
|pageFlag|分页参数名称，默认pageNo
|displayNum|总共显示多少个分页，默认为10
|endDisplayNum|分页中间分隔符后的显示页数，默认为2
|pageContainerClass|分页容器ul的class，默认为 pagination
|pageSelectedClass|分页选中的class，默认为 pagination
|pageContainerClass|分页容器ul的class，默认为 active
|disabledClass|分页不可点击的class，默认为 disabled
|prevIconClass|上一页图标的class，默认为 icon-chevron-left
|nextIconClass|下一页图标的class，默认为 icon-chevron-right
|prevString|上一页字符串，默认为 <
|nextString|下一页字符串，默认为 >


# SelectOptionTag

用于生成**Select**的option部分

使用

```
<hh:options className="org.huihoo.samples.petclinic.model.PetType" 
		    				currentId="${model.type.id }"
		    	            liveTimeInSeconds="300"/>
```

属性

|名称|描述
|:------|:------|
|className|实体的className
|labelName|显示的属性名称，默认为name
|valueName|值的名称，默认为id
|currentValue|当前的值,用于判断选中状态
|condition|查询条件(如果有)
|liveTimeInSeconds|缓存多少秒


# 国际化

TODO

# 服务调用

TODO

TODO


