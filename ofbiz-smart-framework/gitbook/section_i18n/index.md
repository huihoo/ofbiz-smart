# 国际化

> 支持原有的基于properties资源文件的国际化

> 支持基于xml资源文件的国际化

> xml资源文件的命名规则必须以i18n_开头；文件必须放在classpath目录下，比如(src/main/resources)目录

xml文件格式如下:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<resource xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <property key="btnOk">
        <value xml:lang="en_US">OK</value>
        <value xml:lang="zh_CN">确定</value>
    </property>
    <property key="btnCancle">
        <value xml:lang="en_US">Cancle</value>
        <value xml:lang="zh_CN">取消</value>
    </property>
</resource>
```

如上所示，相比传统的国际化资源文件编写，基于XML的方式不需要对中文进行转码，而且更加直观。

> key 定义了属性的名称

> xml:lang 定义了语言代码和地区代码

# I18NUtil

> 该类负责国际化资源文件的加载，根据指定**Locale**地区读取对应的语言

示例：

有名为i18n_ui.xml的国际化资源文件，内容为:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<resource xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <property key="btnOk">
        <value xml:lang="en_US">OK</value>
        <value xml:lang="zh_CN">确定</value>
    </property>
    <property key="btnCancle">
        <value xml:lang="en_US">Cancle</value>
        <value xml:lang="zh_CN">取消</value>
    </property>
</resource>
```

使用：

```java
 //Normal
 Locale locale = new Locale("zh", "CN");
 ResourceBundle resourceBundle = I18NUtil.getResourceBundle("i18n_ui", locale);
 String btnCancle = resourceBundle.getString("btnCancle");
 String btnOk = resourceBundle.getString("btnOk");
 
 //HttpServletRequest
 Locale locale = request.getLocale();
 ResourceBundle resourceBundle = I18NUtil.getResourceBundle("i18n_ui", locale);
 String btnCancle = resourceBundle.getString("btnCancle");
 String btnOk = resourceBundle.getString("btnOk");
```

{% em color="#ff0000" %}
注意：语言代码和地区代码必须和xml资源文件里一一对应
{% endem %}
