# Zuji-JPA

> [Zuji-JPA](https://github.com/azhengZJ/zuji-jpa) 是一个不用写sql的 `Spring Data JPA` 增强库，在 Spring Data JPA 的基础上简化开发，目的是让开发者不再去书写冗长的SQL，支持 `入参定义式零逻辑` 和 `极简Java动态链式` 两种方式来代替SQL。

## 特性

- 无侵入性，只做增强不做改变；
- 超轻量，jar包只有58K，更好用的超轻量的spring-data-jpa增强库；
- 低功耗，全部基于静态工具类方法实现，程序启动无需加载任何Class；
- 提供了 `入参定义式零逻辑`（支持join） 和 `极简Java动态链式` 两种方式替代sql；
- 单层级的动态条件查询只需定义入参实体类，不用写具体java实现代码，也不用写sql，即可信手拈来；
- 多层级嵌套复杂的动态条件查询使用超简洁的动态链式编程即可轻松实现；
- 使用Zuji-JPA可以大大简化开发、提高效率，节省更多的时间让你专注于业务；

## 愿景

 [Spring Data JPA](https://spring.io/projects/spring-data-jpa) 越来越流行，极大的方便了对数据库的操作，但是spring data jpa在复杂查询方面，表现的很吃力，虽然提供了specification查询，但是使用起来不是很优雅。

鉴于此，为了减少开发人员书写查询逻辑代码和sql ，我基于`specification`功能开发出了为简化开发而生的Zuji-JPA框架，Zuji-JPA 中引入了 `入参定义式零逻辑` 和 `极简Java动态链式` 两种查询方式来替代动态SQL。基本解决开发过程中遇到的大部分场景，用更简单的方式去实现动态查询。

当然，开发中还是会遇到更复杂的sql场景，这里引入了 `Fenix` 开源库，可以比mybatis更方便的去书写动态SQL。

如果有任何问题或想要更多交流，请加QQ群 `758629787`。

## 开源许可证

本 `Zuji-JPA` 的 Spring Data JPA 扩展库遵守 [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0) 许可证。
