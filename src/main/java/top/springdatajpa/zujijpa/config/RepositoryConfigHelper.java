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

/**
 * Multiple data-source configuration helper class
 * @author azheng
 * @since 2019/10/22
 */
public class RepositoryConfigHelper {

    public static LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, String entityPackage) {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(dataSource);
        factory.setPackagesToScan(entityPackage);
        factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter(){{
            setShowSql(true);
            setGenerateDdl(true);
        }});
        factory.setJpaProperties(getProperties());
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
