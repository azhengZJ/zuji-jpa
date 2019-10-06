# 快速开始

对于很长的、复杂的动态或统计性的 SQL 采用注解 或者 Java 书写不仅冗长，且不易于调试和维护。因此，我更推荐你通过 `XML` 文件来书写 SQL，使得 SQL 和 Java 代码解耦，更易于维护和阅读。

下面我将以一个新的 `Spring Boot` 项目来演示 Fenix 的使用。

!> 本文档中的一些示例可以在 Fenix 示例项目源码 [fenix-example](https://github.com/blinkfox/fenix-example) 中查看，其他更多的使用示例可以在 Fenix 源码的[单元测试](https://github.com/blinkfox/fenix/tree/develop/src/test/java/com/blinkfox/fenix/repository)中找到。

## 项目和数据准备

!> **注**：下面“项目和数据准备”的内容，除了集成 Fenix 配置之外，基本上与 Fenix 无关，你大概体验和预览下内容就行。

### 创建项目

在 [start.spring.io](https://start.spring.io/) 中创建一个自己的 SpringBoot2.x 项目，目前最新稳定版本是 `2.1.7`。选出了一些组件来生成项目，我这里仅选了如下几个：

- `JPA`: 这是**必须**组件，就是用来试用 `Spring Data JPA` 的 Fenix 扩展的
- `Web`: Spring Boot Web 项目，用来测试打包后的 SQL 执行情况，**非必须**组件
- `Lombok`: 可以通过注解大量减少 Java 中重复代码的书写，**非必须**组件
- `HSQLDB`: 内存数据库，用来做测试，**非必须**组件

生成之后直接导入 IDE 开发工具，然后根据前面的 Fenix [Spring Boot 项目集成](quick-install?id=spring-boot-integrations) 的文档集成 Fenix 库到项目中即可，这里不再赘述。你也可以 [点击这里下载](https://github.com/blinkfox/fenix-example) 本示例项目的源码查看。

