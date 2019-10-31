package top.springdatajpa.zujijpa.config;

import org.hibernate.cfg.AvailableSettings;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Multiple data-source configuration helper class
 * @author azheng
 * @since 2019/10/22
 */
public class RepositoryConfigHelper {

    public static LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, String entityPackage) {
        return entityManagerFactory(dataSource, entityPackage, (a,p) -> {});
    }

    /**
     *
     * By default, SQL log printing, automatic table creation,
     *   underline and hump automatic conversion, mysql dialect are enabled.
     * @param dataSource dataSource
     * @param entityPackage entityPackage
     * @param action action
     * @return LocalContainerEntityManagerFactoryBean
     */
    public static LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, String entityPackage,
                                                                              BiConsumer<HibernateJpaVendorAdapter,Properties> action) {
        return entityManagerFactory(dataSource, entityPackage, factory -> {
            HibernateJpaVendorAdapter jpaAdapter = new HibernateJpaVendorAdapter() {{
                setShowSql(true);
                setGenerateDdl(false);
            }};
            Properties properties = getProperties();
            action.accept(jpaAdapter,properties);
            factory.setJpaVendorAdapter(jpaAdapter);
            factory.setJpaProperties(properties);
        });
    }

    public static LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, String entityPackage,
                                                                              Consumer<LocalContainerEntityManagerFactoryBean> action) {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(dataSource);
        factory.setPackagesToScan(entityPackage);
        action.accept(factory);
        return factory;
    }

    public static PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory);
        return txManager;
    }

    private static Properties getProperties(){
        Properties properties = new Properties();
        properties.put(AvailableSettings.PHYSICAL_NAMING_STRATEGY, SpringPhysicalNamingStrategy.class.getName());
        properties.put(AvailableSettings.DIALECT, MysqlConfig.class.getName());
        return properties;
    }

}
