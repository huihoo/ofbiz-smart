# 种子数据

> 种子数据指的是支撑应用运行的基础初始化数据，比如实初始的用户角色数据等。

> 种子数据的SQL文件必须位于classpath中，在application.properties中进行配置。

> 种子数据的加载仅在非生产环境模式下执行，即profile不等于production，便于开发调试。

> 种子数据仅支持INSERT语句。

> 种子数据成功执行后，会在种子数据文件所在目录，生成一个以种子数据文件名加_resloved结尾的文件，表示已经处理过，
下次运行不处理。如果需要再次处理，先删除以_resloved结尾的文件。

现有名为seed_data.sql的种子数据文件，内容如下:

```sql
INSERT INTO vets VALUES (1, 'James', 'Carter','2016-01-01 00:00:00','2016-01-01 00:00:00');
INSERT INTO vets VALUES (2, 'Helen', 'Leary','2016-01-01 00:00:00','2016-01-01 00:00:00');
INSERT INTO vets VALUES (3, 'Linda', 'Douglas','2016-01-01 00:00:00','2016-01-01 00:00:00');
INSERT INTO vets VALUES (4, 'Rafael', 'Ortega','2016-01-01 00:00:00','2016-01-01 00:00:00');
INSERT INTO vets VALUES (5, 'Henry', 'Stevens','2016-01-01 00:00:00','2016-01-01 00:00:00');
INSERT INTO vets VALUES (6, 'Sharon', 'Jenkins','2016-01-01 00:00:00','2016-01-01 00:00:00');
```

配置
  
```java
#多个文件以逗号隔开
seed.data.sql.file=seed_data.sql
```


  
  
  