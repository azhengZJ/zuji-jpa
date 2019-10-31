# 更多功能

## 多数据源配置

Zuji-Jpa提供了多数据源配置的辅助类，大大简化了多数据源的配置。

第一个多数据源的配置

```java
@Configuration
@EnableJpaRepositories(entityManagerFactoryRef = FirstRepositoryConfig.ENTITY_MANAGER_REF,
					   transactionManagerRef = FirstRepositoryConfig.TRANSACTION_REF,
					   basePackages = {FirstRepositoryConfig.REPOSITORY_PACKAGE })
@EnableTransactionManagement
public class FirstRepositoryConfig {

	public static final  String TRANSACTION_REF = "fiTransactionManager";
	static final  String DATASOURCE_NAME = "fiDataSource";
	static final  String ENTITY_MANAGER_REF = "fiEntityManagerFactory";
	static final  String REPOSITORY_PACKAGE = "com.zuji.demo.repository.fi";
	static final  String ENTITY_PACKAGE = "com.zuji.demo.model.fi";

	@Autowired
	@Qualifier(DATASOURCE_NAME)
    private DataSource dataSource;

	@Bean(name = ENTITY_MANAGER_REF)
	@Primary
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		return RepositoryConfigHelper.entityManagerFactory(dataSource,ENTITY_PACKAGE);
	}

	@Bean(name = TRANSACTION_REF)
	public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
		return RepositoryConfigHelper.transactionManager(entityManagerFactory);
	}

}
```
第二个多数据源的配置
```java
@Configuration
@EnableJpaRepositories(entityManagerFactoryRef = SecondRepositoryConfig.ENTITY_MANAGER_REF,
					   transactionManagerRef = SecondRepositoryConfig.TRANSACTION_REF,
					   basePackages = {SecondRepositoryConfig.REPOSITORY_PACKAGE })
@EnableTransactionManagement
public class SecondRepositoryConfig {

	public static final  String TRANSACTION_REF = "seTransactionManager";
	static final  String DATASOURCE_NAME = "seDataSource";
	static final  String ENTITY_MANAGER_REF = "seEntityManagerFactory";
	static final  String REPOSITORY_PACKAGE = "com.zuji.demo.repository.se";
	static final  String ENTITY_PACKAGE = "com.zuji.demo.model.se";

	@Autowired
	@Qualifier(DATASOURCE_NAME)
	private DataSource dataSource;

	@Bean(name = ENTITY_MANAGER_REF)
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		return RepositoryConfigHelper.entityManagerFactory(dataSource,ENTITY_PACKAGE);
	}

	@Bean(name = TRANSACTION_REF)
	public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
		return RepositoryConfigHelper.transactionManager(entityManagerFactory);
	}

}
```

也可以自定义属性.

```java
@Bean(name = ENTITY_MANAGER_REF)
public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
    return RepositoryConfigHelper.entityManagerFactory(dataSource, ENTITY_PACKAGE,(adapter, properties)->{
        //默认为true
        adapter.setShowSql(false);
        //默认为false
        adapter.setGenerateDdl(true);
        //默认为mysql
        properties.put(AvailableSettings.DIALECT, MysqlConfig.class.getName());
        //默认支持驼峰下划线转换
        properties.put(AvailableSettings.PHYSICAL_NAMING_STRATEGY, SpringPhysicalNamingStrategy.class.getName());
    });
}
```

还可以自定义更多属性
```java
@Bean(name = ENTITY_MANAGER_REF)
public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
    return RepositoryConfigHelper.entityManagerFactory(dataSource, ENTITY_PACKAGE, factory->{
        factory.setPersistenceUnitName("name");
        factory.setPackagesToScan("com.zuji.demo.model");
    });
}
```


## 自定义查询返回

### 1、Dynamic Projections（动态投影）

数据库查询大部分时候都要 返回自定义字段（ VO/DTO ），Spring Data Jpa 默认支持 泛型 动态投影（Projections） ，投影的类型可以是接口也可以是类。

Repository查询接口定义
```java
interface PersonRepository extends Repository<Person, UUID> {
  <T> Collection<T> findByUsername(String username, Class<T> type);
}
```
或者
```java
interface PersonRepository extends Repository<Person, UUID> {
  NamesQueryDTO findByUsername(String username, Class<T> type);
}
```

**基于接口的投影**，须提供字段对应的get方法接口。

```java
interface NamesQueryDTO {

  String getFirstname();
  String getLastname();
  
}
```
**基于类的投影**，定义好所有查询字段，并且提供全参构造器。强烈推荐 使用Lombok的@Value注解 简化代码。

```java
@Value
class NamesOnlyDTO {
	String firstname, lastname;
}
```

### 2、提供工具类转换

动态投影有时候会不生效，这时候就需要自己进行类型转换，zuji-jpa提供了相关工具类。

没有分页的列表
```java
List<Blog> list = repository.findAll(spec);
return EntityUtils.cast(list, RespBlogVO.class);
```

有分页的列表
```java
Specification<Blog> spec = Specifications.conditionOf(vo, e->{
    e.eq(Blog.Fields.status, 1);
});
PageRequest page = vo.pageRequest(Sort.by(Blog.Fields.id).descending());
Page<Blog> list = repository.findAll(spec, page);
return JpaHelper.castPage(list, page, RespBlogVO.class);
```
或者

```java
Specification<Blog> spec = Specifications.conditionOf(vo, e->{
    e.eq(Blog.Fields.status, 1);
});
Page<Blog> list = repository.findAll(spec, vo.pageRequest(Sort.by(Blog.Fields.id).descending()));
Page<RespBlogVO> result = JpaHelper.castPage(list, RespBlogVO.class);
```

单个对象
```java
Blog blog = repository.findOne(id);
RespBlogVO result =  EntityUtils.cast(Blog, RespBlogVO.class);
```



