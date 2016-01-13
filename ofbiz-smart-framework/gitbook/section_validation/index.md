# 验证框架

> 一个简单的，基于注解的，能满足一般的实体验证的验证框架

## 验证注解

名称          | 描述                 | 示例
:----------- | :-----------        | :-----------
Required     | 检查属性是否必须有值    | ```@Required(message="your msg")```
Alphanum     | 检查要验证的字符串是否是字母和数字的组合    | ```@Alphanum(message="your msg")```
Digits       | 检查要验证的字符串是否是有效的数字    | ```@Digits(message="your msg")```
Email        | 检查要验证的字符串是否是有效的邮箱    | ```@Email(message="your msg")```
Url          | 检查要验证的字符串是否是一个有效的Url地址    | ```@Url(message="your msg")```
Pattern      | 检查要验证的字符串是否匹配指定的正则表达式    | ```@Pattern(value="\\d+",message="your msg")```
Range        | 检查要验证的<b>整数</b>是否在指定最小值和最大值之间，即：大于或等于 <b>指定的最小值</b> 并且 是否小于或等于 <b>指定的最大值</b>    | ```@Range(min=1,max=5,message="your msg")```
Max          | 检查要验证的<b>整数</b>是否小于或等于指定的最大值    | ```@Max(value=50,message="your msg")```
Min          | 检查要验证的<b>整数</b>是否大于或等于指定的最小值    | ```@Min(value=50,message="your msg")```
MaxLength    | 检查要验证的字符串长度是否小于或等于指定的最大值    | ```@MaxLength(value=50,message="your msg")```
MinLength    | 检查要验证的字符串长度是否大于或等于指定的最小值    | ```@MinLength(value=50,message="your msg")```
DecimalMax   | 检查要验证的数字字符串是否小于或等于指定的最大值    | ```@DecimalMax(value=50.00,message="your msg")```
DecimalMin   | 检查要验证的数字字符串是否大于或等于指定的最小值    | ```@DecimalMin(value=50.00,message="your msg")```
DecimalRange | 检查要验证的数字字符串是否在指定的最小值和最大值之间 | ```@DecimalRange(min=50.00,max=100.00,message="your msg")```
NotNull      | 检查要验证的对象不能为空   | ```@NotNull(message="your msg")```
Null         | 检查要验证的对象应该为空   | ```@Null(message="your msg")```

## 验证方法

> 直接调用**Validator**的**validate**方法进行验证

如下所示：

```java
//实体定义
public class Customer {
    @Required
    @MinLength(6)
    @MaxLength(32)
    String username;
    
    @Min(18)
    @Max(60)
    int age;

    //setter and getter
    ...
}
  
//验证
Customer customer = new Customer();
Map<String,List<ConstraintViolation>> violationMap = Validator.validate(customer);

```
如上所示，调用验证方法后，返回的**Map**结构如下：

```java
{
 username=[
  ConstraintViolation{
   fieldName='username', 
   filedMessage='This value is required.', 
   filedOriginalValue=null
  }
 ], 
 age=[
  ConstraintViolation{
   fieldName='age’, 
   filedMessage='This value should be greater than or equal to %s.',            
   filedOriginalValue=0
  }
 ]
}
```

**key**为字段名，**value**为**ConstraintViolation**集合，即一个属性有可能有多个验证不通过的结果

## ConstraintViolation

> 违反验证注解定义的约束的提示类，有fieldName,filedMessage,filedOriginalValue三个属性组成

> fieldName要验证的属性名称，这个名称会做为 [国际化](../section_i18n/index.md) 资源文件里的 属性名称； filedMessage提示信息； filedOriginalValue为属性原来的值


