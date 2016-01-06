# EntityAutoEngine

> 实体自动处理服务引擎，根据指定的实体名称及要进行的操作来执行实体的CRUD服务。

> 服务引擎的名称固定为 **entityAuto**。

> 指定的实体名称，为实体类的全路径名称，如**test.engity.Customer**。

### 支持的操作

名称                                   | 描述                                    
:-----------:| :-----------
create       | 实体的创建    
update       | 实体的更新
remove       | 实体的删除 
findById     | 根据主键查找实体  
findUniqueByAnd|根据指定的条件查找唯一的实体  
findListByAnd | 根据指定的条件查找实体集合
findListByCond| 根据指定的条件查找实体集合
findPageByAnd | 根据指定的条件分页查找实体
findPageByCondition|根据指定的条件分页查找实体

```java

//服务的定义（仅需指定实体的名称，引擎指定为entityAuto，服务名固定为 entityAuto#create)
//create对应上面表格中的create
ServiceModel sm = new ServiceModel();
sm.engineName = "entityAuto";
sm.entityName = Customer.class.getName();
sm.name = "entityAuto#create";
//注册服务
serviceDispatcher.registerService(sm);
//执行服务，该服务的执行由 **EntityAutoEngine** 负责
Map<String,Object> resultMap = serviceDispatcher.runSync(sm.name, ctx);
    
```
