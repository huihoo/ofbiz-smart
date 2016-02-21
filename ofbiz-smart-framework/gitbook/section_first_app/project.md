# 项目结构

```

ofbiz-smart-example-petclinic
 |-src
   |-main
     |-java
	   |-org
	     |-huihoo
		   |-samples
		     |-petclinic
			   |-model
			     |-BaseModel.java
			     |-NamedModel.java
				 |-Owner.java
				 |-Person.java
				 |-Pet.java
				 |-PetType.java
				 |-Specialty.java
				 |-Vet.java
				 |-Visit.java
			   |-service
   |-resources
     |-action
	   |-action-map-pet.xml
	 |-application.properties(全局应用配置)
	 |-logback.xml
	 |-seed_data.xml(初始种子数据)
   |-webapp
     |-css
	   |-(省略)
	 |-font
	   |-(省略)
	 |-images
	   |-(省略)
	 |-js
	   |-(省略)
	 |-WEB-INF
	   |-views
	     |-layout
		   |-layout.jsp
		 |-owner
		   |-(省略)
		 |-pet
		   |-(省略)
		 |-vet
		   |-(省略)
		 |-visit
		   |-(省略)
		 |-index.jsp
	   |-web.xml
	 |-index.jsp
 |-pom.xml
 
```

## pom.xml

```
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<groupId>org.huihoo</groupId>
	<artifactId>ofbiz-smart-example-petclinic</artifactId>
	<version>1.0.0</version>
	<packaging>war</packaging>

	<properties>
		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
		<java.version>1.7</java.version>
		<smart.version>1.0.0</smart.version>
		<h2.version>1.4.185</h2.version>
		<junit.version>4.11</junit.version>
		<mockito.version>1.10.19</mockito.version>
		<sonar.language>java</sonar.language>
	</properties>

	<dependencies>
		<dependency>
			<groupId>org.huihoo</groupId>
			<artifactId>ofbiz-smart-framework</artifactId>
			<version>${smart.version}</version>
		</dependency>

		<dependency>
			<groupId>com.h2database</groupId>
			<artifactId>h2</artifactId>
			<version>${h2.version}</version>
		</dependency>
		
		<!-- test -->
		<dependency>
			<groupId>junit</groupId>
			<artifactId>junit</artifactId>
			<version>${junit.version}</version>
		</dependency>
		
		<dependency>
			<groupId>org.mockito</groupId>
			<artifactId>mockito-all</artifactId>
			<version>${mockito.version}</version>
			<scope>test</scope>
		</dependency>
		
	</dependencies>

	<build>
		<finalName>ofbiz-smart-example-petclinic</finalName>
		<plugins>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-project-info-reports-plugin</artifactId>
				<version>2.7</version>
				<configuration>
					<dependencyLocationsEnabled>false</dependencyLocationsEnabled>
				</configuration>
			</plugin>

			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-compiler-plugin</artifactId>
				<version>3.1</version>
				<configuration>
					<encoding>UTF-8</encoding>
					<source>${java.version}</source>
					<target>${java.version}</target>
				</configuration>
			</plugin>

			<plugin>
				<groupId>org.codehaus.mojo</groupId>
				<artifactId>sonar-maven-plugin</artifactId>
				<version>2.3</version>
			</plugin>

		</plugins>
	</build>


	<repositories>
		<repository>
			<id>smart-oss</id>
			<name>smart-oss</name>
			<url>https://oss.sonatype.org/content/groups/public/</url>
			<snapshots>
				<enabled>true</enabled>
				<updatePolicy>interval:5</updatePolicy>
			</snapshots>
		</repository>
	</repositories>
</project>
```