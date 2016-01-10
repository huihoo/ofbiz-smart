# CaptchaView

> 简单的图文验证码界面

> 当[view-type](action.md#response)指定为**captcha**启用
    >```xml
     <action uri="/register_captcha">
		<response view-type="captcha"/>
	 </action>
    >```
    
支持图文验证码的一些参数设置，参数直接放到请求uri中。如下所示：

```html
  <img src="/register_captcha?w=150&h=50&cKey=register_code&cType=letter&cLen=6"/>
```

1. 参数w和h分别指定图文验证码的宽高，不指定，宽为135,高为45
2. 参数cKey指定了验证码存在session中的属性名称，不指定，默认为captchaCode
3. 参数cType指定了验证码生成规则，支持三种alphanum(字母和数字组合),number(数字),letter(字母)，不指定，为alphanum
4. 参数cLen指定了验证码的长度，范围1-10之间，不指定，长度为4



    
    
    
