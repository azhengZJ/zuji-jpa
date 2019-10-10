# Zuji-JPA

> [Zuji-JPA](https://github.com/azhengZJ/zuji-jpa) 是一个不用写sql的 `Spring Data JPA` 扩展库，目的是让开发者不再去书写冗长的SQL，用极简的代码解决一切问题，支持 定义式零逻辑 和 简单式Java 链式 API 两种方式来书写动态 SQL。

## 特性

- 比spring-data-jpa更好用的超轻量的spring-data-jpa扩展库；
- 全部基于静态工具类方法实现，程序启动无需加载任何Class；
- 提供了 入参定义式零逻辑 和 简单式Java 链式 API 两种方式替代sql
- 单层级的动态条件查询只需定义入参实体类，不用写具体java实现代码，也不用写sql即可信手拈来；
- 多层级嵌套复杂的动态条件查询使用超简洁的链式编程、语义化编程即可轻松实现；
- 使用zuji-jpa可以大大简化开发，节省更多的时间让你专注于业务；

## 初衷

随着 [Spring Data JPA](https://spring.io/projects/spring-data-jpa) 越来越流行，极大的方便了数据的“增删改”和简单查询的场景，但是在复杂、动态查询方面就显得有些“糟糕”了，相比 `MyBatis` 的 `XML` 动态 SQL 而言，缺少了一定优雅和可维护性。

所有，为了减少开发人员书写sql和查询逻辑代码 ，Zuji-JPA 中引入了 入参定义式查询 和 简单式Java 链式 API 来替代动态 SQL。基本解决开发过程中遇到的大部分场景，用更简单的方式去实现动态查询。

当然，开发中还是会遇到更复杂的sql场景，这里引入了 [Fenix](https://github.com/blinkfox/fenix) 开源库，可以比mybatis更方便的去书写动态SQL.


## 开源许可证

本 `Zuji-JPA` 的 Spring Data JPA 扩展库遵守 [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0) 许可证。
