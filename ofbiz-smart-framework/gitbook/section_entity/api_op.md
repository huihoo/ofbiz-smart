操作类API指的是保存，更新，删除实体，及对应的原生SQL。

具体的API如下所示: 

```
  void save(Collection<?> entities) throws GenericEntityException;

  void save(Object entity) throws GenericEntityException;

  void update(Collection<?> entities) throws GenericEntityException;

  void update(Object entity) throws GenericEntityException;

  void remove(Collection<?> entities) throws GenericEntityException;

  void remove(Object entity) throws GenericEntityException;

  void removeById(Class<?> entityClazz, Object id) throws GenericEntityException;
  
  int executeByRawSql(String sql) throws GenericEntityException;

  int executeByRawSql(String sql, List<?> params) throws GenericEntityException;

```

示例代码片段:

```
Customer customer = new Customer();
customer.setFirstname("hbh");
customer.setLastname("peter");
delegator.save(customer);

customer.setAge(30);
delegator.update(customer);

int updatedCount = delegator.executeByRawSql("update customer set age = ? where id = ?",
Arrays.asList(new Object[]{30,customer.getId()}));

delegator.removeById(Customer.class,customer.getId());

```

