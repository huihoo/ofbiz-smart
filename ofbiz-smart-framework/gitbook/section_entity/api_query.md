查询API指的是查询实体类相关的API，在实际开发中，会经常用到。

### 根据主键查询实体

API:

```java
Object findById(Class<?> entityClazz, Object id);

Object findById(Class<?> entityClazz, Object id, boolean useCache);

Object findById(Class<?> entityClazz, Object id, boolean useCache, 
                                                 int liveTimeInSeconds);
                                                   
```

示例代码：

```java
//指定缓存多久，以秒为单位
int ts = 60; 

//查找ID等于1的Customer实体，并缓存60秒
Customer c = (Customer)delegator.findById(Customer.class, 1L,true,ts);

```

### 根据指定的条件映射**Map**查询实体

该类API以**byAnd**结尾，根据传入的属性/值映射**Map**查找相关实体。**Map**中的所有条件映射全部为与的关系。

API:

```java
List<Object> findIdsByAnd(Class<?> entityClazz, Map<String, Object> andMap);

List<?> findListByAnd(Class<?> entityClazz, Map<String, Object> andMap);

List<?> findListByAnd(Class<?> entityClazz, Map<String, Object> andMap, 
                                            List<String> orderBy);

List<?> findListByAnd(Class<?> entityClazz, Map<String, Object> andMap,
                                            Set<String> fieldsToSelect, 
                                            List<String> orderBy, 
                                            boolean useCache);
                                              

List<?> findListByAnd(Class<?> entityClazz, Map<String, Object> andMap,
                                            Set<String> fieldsToSelect, 
                                            List<String> orderBy, 
                                            boolean useCache, 
                                            int liveTimeInSeconds);
                                              
                                            

Object findUniqueByAnd(Class<?> entityClazz, Map<String, Object> andMap) ;

Object findUniqueByAnd(Class<?> entityClazz, Map<String, Object> andMap,
                                             Set<String> fieldsToSelect, 
                                             boolean useCache);

Object findUniqueByAnd(Class<?> entityClazz, Map<String, Object> andMap,
                                             Set<String> fieldsToSelect, 
                                             boolean useCache, 
                                             int liveTimeInSeconds);                                   
```

示例代码:

```java

//查找 状态为 'ACTIVE' 且 性别为 'MALE' 的所有客户ID
Map<String,Object> andMap = CommUtil.toMap("status","ACTIVE",
                                           "gender","MALE");
List<Long> customerIds = (List<Long>)
                         delegator.findIdsByAnd(Customer.class,andMap);

//性别为 'MALE' 且 状态为 'ACTIVE'
Map<String,Object> andMap = CommUtil.toMap("gender","MALE","status","ACTIVE");
//按客户的创建时间降序，年龄升序排序
List<String> orderBy = Arrays.asList(new String[]{"createdAt desc","age asc");
//仅查询客户的firstName,lastName,age三个属性
Set<String> fieldsToSelect = new LinkedHashSet<>();
fieldsToSelect.add("firstName");
fieldsToSelect.add("lastName");
fieldsToSelect.add("age");
//缓存300秒，即5分钟
int liveTimeInSeconds = 300;
List<Customer> customers = (List<Customer>)delegator.findListByAnd(Customer.class,andMap,
                                        fieldsToSelect,
                                        true,
                                        liveTimeInSeconds);
```

### 根据指定的条件表达式查询相关实体

该类API以**byCond**结尾，根据传入的条件表达式，该表达式为框架本身定义的[**查询条件字符串**](../section_entity/query_expr.html)，来查询相关实体。

API:

```java
List<Object> findIdsByCond(Class<?> entityClazz, String cond);

List<?> findListByCond(Class<?> entityClazz, String cond);

List<?> findListByCond(Class<?> entityClazz, String cond, 
                                             Set<String> fieldsToSelect,
                                             List<String> orderBy);

List<?> findListByCond(Class<?> entityClazz, String cond, 
                                             Set<String> fieldsToSelect,
                                             List<String> orderBy, 
                                             boolean useCache);

List<?> findListByCond(Class<?> entityClazz, String cond, 
                                             Set<String> fieldsToSelect,
                                             List<String> orderBy, 
                                             boolean useCache, 
                                             int liveTimeInSeconds);

```

示例代码:

```java
//查询姓名含有 'hbh' 且 性别 等于 'MALE'的所有客户ID"
String cond = "{firstName,like,hbh}{gender,eq,MALE}";
List<Long> cIds = (List<Long>) delegator.findIdsByCond(Customer.class,cond);

String cond = "{firstName,like,hbh}{gender,eq,MALE}";
//按客户的创建时间降序，年龄升序排序
List<String> orderBy = Arrays.asList(new String[]{"createdAt desc","age asc"});
//仅查询客户的firstName,lastName,age三个属性
Set<String> fieldsToSelect = new LinkedHashSet<>();
fieldsToSelect.add("firstName");
fieldsToSelect.add("lastName");
fieldsToSelect.add("age");
//缓存300秒，即5分钟
int liveTimeInSeconds = 300;
List<Customer> customers = (List<Customer>)delegator.findListByCond(Customer.class,
                                         cond,
										  fieldsToSelect,
									      true,
                                         liveTimeInSeconds);

```

### 分页查询

分页API以__findPage__开头，有上面提到的**byAnd**和**byCond**两种查询方式，显示指定第几页，每页显示的大小。

API:

```java
Map<String, Object> findPageByAnd(Class<?> entityClazz, 
                                  Map<String, Object> andMap, 
                                  int pageNo,
                                  int pageSize);

Map<String, Object> findPageByAnd(Class<?> entityClazz, 
                                  Map<String, Object> andMap, 
                                  int pageNo,
                                  int pageSize, 
                                  Set<String> fieldsToSelect, 
                                  List<String> orderBy);

Map<String, Object> findPageByAnd(Class<?> entityClazz, 
                                  Map<String, Object> andMap, 
                                  int pageNo,
                                  int pageSize, 
                                  Set<String> fieldsToSelect, 
                                  List<String> orderBy, 
                                  boolean useCache);

Map<String, Object> findPageByAnd(Class<?> entityClazz, 
                                  Map<String, Object> andMap, 
                                  int pageNo,
                                  int pageSize, 
                                  Set<String> fieldsToSelect, 
                                  List<String> orderBy, 
                                  boolean useCache,
                                  int liveTimeInSeconds);

Map<String, Object> findPageByCond(Class<?> entityClazz, 
                                   String cond, 
                                   int pageNo, 
                                   int pageSize);

Map<String, Object> findPageByCond(Class<?> entityClazz, 
								    String cond, 
                                   int pageNo, 
                                   int pageSize,
                                   Set<String> fieldsToSelect, 
                                   List<String> orderBy);

Map<String, Object> findPageByCond(Class<?> entityClazz, 
                                   String cond, 
                                   int pageNo, 
                                   int pageSize,
                                   Set<String> fieldsToSelect, 
                                   List<String> orderBy, 
                                   boolean useCache);

Map<String, Object> findPageByCond(Class<?> entityClazz, 
                                   String cond, 
                                   int pageNo, 
                                   int pageSize,
                                   Set<String> fieldsToSelect, 
                                   List<String> orderBy, 
                                   boolean useCache, 
                                   int liveTimeInSeconds) ;
```

如上面的API所示，和__byAnd__和__byCond__两种查询API相比，区别在于：

1. 需要指定 **pageNo**和**pageSize**两个参数。
2. 返回值类型为 **Map**。

返回的**Map**有如下元素组成：

>**totalPage**： 总页数

>**totalEntry**: 总记录

>**list**: 实体集合

>**pageNo**:第几页

>**pageSize**:每页显示的大小


### 查询总数

查询总数指的是各种**count** API。

API:

```java
int countByAnd(Class<?> entityClazz, Map<String, Object> andMap) ;

int countByCond(Class<?> entityClazz, String cond) ;


int countByRawQuery(String query, String countAlias) ;

int countByRawQuery(String query, String countAlias, List<?> params) ;
  
```

注意：

> API中的**countAlias**参数指的是给返回的总数字段起个别名。必须包含在**query**参数里。
> 如 
> ```
> int c = delegator.countByRawQuery("select count(1) as c from customers","c");
> ```


### 自定义SQL查询

如果以上查询API都不能满足业务需求，可以使用自定义SQL查询。

API:

```java
List<Map<String, Object>> findListByRawQuery(String query, 
                                             List<?> params) ;

List<Map<String, Object>> findListByRawQuery(String query, 
                                             List<?> params, 
                                             boolean useCache) ;
```

返回的一个**List**,集合里的元素是**Map**，**Map**里的键值映射，由自定义**query**参数决定。

如下所示：

```java
String rawSql = "select id,first_name,last_name from customers";
List<Map<String,Object>> customers = delegator
                                     .findListByRawQuery(rawSql,null);

//返回的集合里的**Map**元素应该是这种结构
// ('first_name','abc')('last_name','hbh')('id',20)

```

注意：

> 以上API中名为**fieldsToSelect**的参数，指定的是查询哪些实体属性，不管怎么指定，实体的**主键id**是一定会返回的。