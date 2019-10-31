## 支持场景

适用于 Java `Spring Data JPA` 项目，`JDK 1.8` 及以上，Spring Data JPA 版本无具体要求。

使用之前需要对`Spring Data JPA`框架有一定的了解和使用，如果还未使用过，请先写个简单的DEMO熟悉一下，
具体可参考[Spring Data JPA](https://spring.io/projects/spring-data-jpa)官方文档。

## Spring 项目集成

如果你是 Spring 项目，请直接集成Zuji-JPA依赖，spring boot和spring mvc项目均支持。

gradle
```groovy
implementation 'top.spring-data-jpa:zuji-jpa:1.0.1'
```
maven
```xml
<dependency>
  <groupId>top.spring-data-jpa</groupId>
  <artifactId>zuji-jpa</artifactId>
  <version>1.0.1</version>
</dependency>
```