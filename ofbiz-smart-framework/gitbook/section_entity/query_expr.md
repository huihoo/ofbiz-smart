条件表达式适用于以**byCond**结尾的API，格式为 **{fieldName,expr,value}{n+1}**。一个条件表达式符号'**{**'开头，以符号'**}**'结尾。表达式里的字符串以逗号'**,**'隔开，第一个字符串为指定的属性名称，第二个字符串为表达式，第三个为具体的值。各**表达式**之间为 **与** 的关系。

### 支持的表达式：

表达式        | 描述           | 示例
:-----------:| :-----------: | :-----------:
eq           | 等于指定的值    | {firstName,eq,hbh} firstName等于'hbh'
neq          | 不等于值定的值   | {firstName,neq,hbh} firstName不等于'hbh'
ge           | 大于等于指定的值    | {age,ge,25} age大于等于25
gt           | 大于指定的值   | {age,gt,25} age大于25
le           | 小于等于指定的值    | {age,le,30} age小于等于30
lt           | 小于指定的值   | {age,lt,30} age小于30
between      | 介于指定的值之间    | {age,between,20#30} age介于20至30之间
in           | 在指定的值集合之内   | {id,in,1#2#3} id在指定的1,2,3里
notIn        | 不在指定的值集合之内    | {id,notIn,1#2#3} id不在指定的1,2,3里
like         | 全通配符匹配指定的值   | {lastName,like,hbh} lastName 含有'hbh'，等价于 %hbh%
llike        | 左通配符匹配指定的值    | {lastName,llike,hbh} lastName 以'hbh'结尾，等价于%hbh
rlike        | 右通配符匹配指定的值   | {lastName,rlike,hbh} lastName 以'hbh'开头，等价于hbh%
isNull       | 字段为空    | {birthday,isNull,placeholder(anyValue)} birthday为空
isNotNull    | 字段不为空   | {status,isNotNull,placeholder(anyValue)} birthday不为空
or           | 指定两个条件表达式为或的关系    | {age,eq,30,or,salary,ge,5000} age等于 30 或 salary 大于等于 5000

**注意:**

> placeholder(anyValue)意为占位符，可以是任意值。仅用来保证条件表达式格式的一致性。

### 纯字符串拼接条件表达式

示例：


```
//gender等于'Male' level介于1至5之间 salary介于5000至8000之间
//id在1,2,3里
//lastName不为空 firstName为空
//level大于等于1 level大于0 level小于等于10 level小于10
//id不在8,9,10里
//id大于等于1 或 id小于等于100
String cond = "{gender,eq,Male}{level,between,1#5}{salary,between,5000#8000}"
           += "{id,in,1#2#3}"
           += "{lastName,isNotNull,any}{firstName,isNull,any}"
           += "{level,ge,1}{level,gt,0}{level,le,10}{level,lt,10}"
           += "{id,notIn,8#9#10}{id,ge,1,or,id,le,100}";

```

### **Expr**类构建条件表达式

**Expr**提供了一种更加友好的方式来构建条件表达式，各方法名与支持的表达式同名。

如下所示：


```
String exprCond = Expr.create().eq("gender", "Male")
                               .between("level", 1,5)
                               .between("salary", 5000, 8000)
                               .in("id", Arrays.asList(new Object[]{1,2,3}))
                               .notIn("id", Arrays.asList(new Object[]{8,9,10}))
                               .isNotNull("lastName")
                               .isNull("firstName")
                               .ge("level", 1)
                               .gt("level", 0)
                               .le("level", 10)
                               .lt("level", 10)
                               .or("id,ge,1", "id,le,100")
                               .build();

```

**注意**

>**between**表达式要指定值的起止范围，两个值以'#'号分隔，如{age,between,10#20}

>**in**,**notIn**表达式可以有多个值，多个值以'#‘号分隔，如{id,in,1#2#3}