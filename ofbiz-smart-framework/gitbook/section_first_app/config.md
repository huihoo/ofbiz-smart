# 应用配置

## web.xml

```
   <servlet>
		<servlet-name>SmartServlet</servlet-name>
		<servlet-class>org.huihoo.ofbiz.smart.webapp.DispatchServlet</servlet-class>
		<init-param>
			<param-name>jsp-view-base-path</param-name>
			<param-value>/WEB-INF/views</param-value>
		</init-param>
		<init-param>
			<param-name>uri-suffix</param-name>
			<param-value>.do</param-value>
		</init-param>
		<init-param>
			<param-name>http-api-uri-base</param-name>
			<param-value>/api</param-value>
		</init-param>
		<init-param>
			<param-name>rest-api-uri-base</param-name>
			<param-value>/rest</param-value>
		</init-param>
		<init-param>
			<param-name>api-doc-uri-base</param-name>
			<param-value>/doc</param-value>
		</init-param>
		<init-param>
			<param-name>use-smart-session</param-name>
			<param-value>true</param-value>
		</init-param>

		<load-on-startup>1</load-on-startup>
	</servlet>

	<servlet-mapping>
		<servlet-name>SmartServlet</servlet-name>
		<url-pattern>*.do</url-pattern>
	</servlet-mapping>
```

## application.properties

```
profile=develop
active.profiles=develop
```

### application-develop.properties

```
action.config.basepath=/action
entity.scanning.packages=org.huihoo.samples.petclinic.model.**
service.scanning.resource.names=org.huihoo.samples.petclinic.service
#service.scanning.transaction=insert*,update*,delete*,save*
service.slowtime.milliseconds=1000
seed.data.sql.file=seed_data.sql

//=========================================================
//                EbeanDelegator Config 
//=========================================================
# generate DDL files
ebean.ddl.generate=true
# run ddl drops and recreates tables
ebean.ddl.run=true
ebean.uuidStoreAsBinary=true
ebean.databaseSequenceBatchSize=1
ebean.debug.sql=true
ebean.debug.lazyload=false

datasource.default=h2
datasource.h2.provider=
datasource.h2.username=sa
datasource.h2.password=
datasource.h2.databaseUrl=jdbc:h2:mem:tests
datasource.h2.databaseDriver=org.h2.Driver

#datasource.mysql.username=root
#datasource.mysql.password=root
#datasource.mysql.databaseUrl=jdbc:mysql://localhost:3306/test
#datasource.mysql.databaseDriver=com.mysql.jdbc.Driver
```

## seed_data.sql(初始化数据)

```
INSERT INTO vets VALUES (1, 'James', 'Carter','2016-01-01 00:00:00','2016-01-01 00:00:00');
INSERT INTO vets VALUES (2, 'Helen', 'Leary','2016-01-01 00:00:00','2016-01-01 00:00:00');
INSERT INTO vets VALUES (3, 'Linda', 'Douglas','2016-01-01 00:00:00','2016-01-01 00:00:00');
INSERT INTO vets VALUES (4, 'Rafael', 'Ortega','2016-01-01 00:00:00','2016-01-01 00:00:00');
INSERT INTO vets VALUES (5, 'Henry', 'Stevens','2016-01-01 00:00:00','2016-01-01 00:00:00');
INSERT INTO vets VALUES (6, 'Sharon', 'Jenkins','2016-01-01 00:00:00','2016-01-01 00:00:00');

INSERT INTO specialties VALUES (1, 'radiology','2016-01-01 00:00:00','2016-01-01 00:00:00');
INSERT INTO specialties VALUES (2, 'surgery','2016-01-01 00:00:00','2016-01-01 00:00:00');
INSERT INTO specialties VALUES (3, 'dentistry','2016-01-01 00:00:00','2016-01-01 00:00:00');

INSERT INTO vet_specialties VALUES (2, 1);
INSERT INTO vet_specialties VALUES (3, 2);
INSERT INTO vet_specialties VALUES (3, 3);
INSERT INTO vet_specialties VALUES (4, 2);
INSERT INTO vet_specialties VALUES (5, 1);

INSERT INTO pet_types VALUES (1, '猫咪','2016-01-01 00:00:00','2016-01-01 00:00:00');
INSERT INTO pet_types VALUES (2, '狗狗','2016-01-01 00:00:00','2016-01-01 00:00:00');
INSERT INTO pet_types VALUES (3, '蜥蜴','2016-01-01 00:00:00','2016-01-01 00:00:00');
INSERT INTO pet_types VALUES (4, '蛇','2016-01-01 00:00:00','2016-01-01 00:00:00');
INSERT INTO pet_types VALUES (5, '鸟儿','2016-01-01 00:00:00','2016-01-01 00:00:00');
INSERT INTO pet_types VALUES (6, '仓鼠','2016-01-01 00:00:00','2016-01-01 00:00:00');

INSERT INTO owners VALUES (1, 'George', 'Franklin', '110 W. Liberty St.', 'Madison', '6085551023','2016-01-01 00:00:00','2016-01-01 00:00:00');
INSERT INTO owners VALUES (2, 'Betty', 'Davis', '638 Cardinal Ave.', 'Sun Prairie', '6085551749','2016-01-01 00:00:00','2016-01-01 00:00:00');
INSERT INTO owners VALUES (3, 'Eduardo', 'Rodriquez', '2693 Commerce St.', 'McFarland', '6085558763','2016-01-01 00:00:00','2016-01-01 00:00:00');
INSERT INTO owners VALUES (4, 'Harold', 'Davis', '563 Friendly St.', 'Windsor', '6085553198','2016-01-01 00:00:00','2016-01-01 00:00:00');
INSERT INTO owners VALUES (5, 'Peter', 'McTavish', '2387 S. Fair Way', 'Madison', '6085552765','2016-01-01 00:00:00','2016-01-01 00:00:00');
INSERT INTO owners VALUES (6, 'Jean', 'Coleman', '105 N. Lake St.', 'Monona', '6085552654','2016-01-01 00:00:00','2016-01-01 00:00:00');
INSERT INTO owners VALUES (7, 'Jeff', 'Black', '1450 Oak Blvd.', 'Monona', '6085555387','2016-01-01 00:00:00','2016-01-01 00:00:00');
INSERT INTO owners VALUES (8, 'Maria', 'Escobito', '345 Maple St.', 'Madison', '6085557683','2016-01-01 00:00:00','2016-01-01 00:00:00');
INSERT INTO owners VALUES (9, 'David', 'Schroeder', '2749 Blackhawk Trail', 'Madison', '6085559435','2016-01-01 00:00:00','2016-01-01 00:00:00');
INSERT INTO owners VALUES (10, 'Carlos', 'Estaban', '2335 Independence La.', 'Waunakee', '6085555487','2016-01-01 00:00:00','2016-01-01 00:00:00');

INSERT INTO pets VALUES (1, 'Leo', '2010-09-07', 1, 1,'2016-01-01 00:00:00','2016-01-01 00:00:00');
INSERT INTO pets VALUES (2, 'Basil', '2012-08-06', 6, 2,'2016-01-01 00:00:00','2016-01-01 00:00:00');
INSERT INTO pets VALUES (3, 'Rosy', '2011-04-17', 2, 3,'2016-01-01 00:00:00','2016-01-01 00:00:00');
INSERT INTO pets VALUES (4, 'Jewel', '2010-03-07', 2, 3,'2016-01-01 00:00:00','2016-01-01 00:00:00');
INSERT INTO pets VALUES (5, 'Iggy', '2010-11-30', 3, 4,'2016-01-01 00:00:00','2016-01-01 00:00:00');
INSERT INTO pets VALUES (6, 'George', '2010-01-20', 4, 5,'2016-01-01 00:00:00','2016-01-01 00:00:00');
INSERT INTO pets VALUES (7, 'Samantha', '2012-09-04', 1, 6,'2016-01-01 00:00:00','2016-01-01 00:00:00');
INSERT INTO pets VALUES (8, 'Max', '2012-09-04', 1, 6,'2016-01-01 00:00:00','2016-01-01 00:00:00');
INSERT INTO pets VALUES (9, 'Lucky', '2011-08-06', 5, 7,'2016-01-01 00:00:00','2016-01-01 00:00:00');
INSERT INTO pets VALUES (10, 'Mulligan', '2007-02-24', 2, 8,'2016-01-01 00:00:00','2016-01-01 00:00:00');
INSERT INTO pets VALUES (11, 'Freddy', '2010-03-09', 5, 9,'2016-01-01 00:00:00','2016-01-01 00:00:00');
INSERT INTO pets VALUES (12, 'Lucky', '2010-06-24', 2, 10,'2016-01-01 00:00:00','2016-01-01 00:00:00');
INSERT INTO pets VALUES (13, 'Sly', '2012-06-08', 1, 10,'2016-01-01 00:00:00','2016-01-01 00:00:00');

INSERT INTO visits VALUES (1, 7, '2013-01-01', 'rabies shot','2016-01-01 00:00:00','2016-01-01 00:00:00');
INSERT INTO visits VALUES (2, 8, '2013-01-02', 'rabies shot','2016-01-01 00:00:00','2016-01-01 00:00:00');
INSERT INTO visits VALUES (3, 8, '2013-01-03', 'neutered','2016-01-01 00:00:00','2016-01-01 00:00:00');
INSERT INTO visits VALUES (4, 7, '2013-01-04', 'spayed','2016-01-01 00:00:00','2016-01-01 00:00:00');

```