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
        //默认为true
        adapter.setGenerateDdl(false);
        properties.put(AvailableSettings.DIALECT, MysqlConfig.class.getName());
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



