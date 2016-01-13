操作类API指的是保存，更新，删除实体，及对应的原生SQL。

API: 

```java
  void save(Collection<?> entities);

  void save(Object entity);

  void update(Collection<?> entities);

  void update(Object entity);

  void remove(Collection<?> entities);

  void remove(Object entity);

  void removeById(Class<?> entityClazz, Object id);
  
  int executeByRawSql(String sql);

  int executeByRawSql(String sql, List<?> params);

```

示例代码片段:

```java
Customer customer = new Customer();
customer.setFirstname("hbh");
customer.setLastname("peter");
delegator.save(customer);

customer.setAge(30);
delegator.update(customer);

String rawSql = "update customer set age = ? where id = ?";
List<Object> params = Arrays.asList(new Object[]{30,customer.getId()});
int updatedCount = delegator.executeByRawSql(rawSql,params);

delegator.removeById(Customer.class,customer.getId());

```

