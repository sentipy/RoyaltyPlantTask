package com.sentilabs.royaltyplanttask.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * Created by sentipy on 04/07/15.
 */

@Configuration
//@EnableJpaRepositories(basePackages = "com.sentilabs.royaltyplanttask.repository")
//@EntityScan("com.sentilabs.royaltyplanttask.entity")
@EnableTransactionManagement
@PropertySource("classpath:appConfig.properties")
public class DatasourceConfigurer {

    @Value("${dataSource.driverClassName}")
    private String driver;
    @Value("${dataSource.url}")
    private String url;
    @Value("${dataSource.username}")
    private String username;
    @Value("${dataSource.password}")
    private String password;
    @Value("${hibernate.dialect}")
    private String dialect;
    @Value("${hibernate.hbm2ddl.auto}")
    private String hbm2ddlAuto;

    @Autowired
    private Environment environment;


    /*@Bean
    @Profile("!inmemory")
    public DataSource configurePostgreSQLDataSource() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(driver);
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);

        return new HikariDataSource(config);
    }

    @Bean
    @Profile("inmemory")
    public DataSource configureH2DataSource() {
        HikariConfig config = new HikariConfig();
        config.setDataSourceClassName("org.h2.jdbcx.JdbcDataSource");
        config.setConnectionTestQuery("VALUES 1");
        config.addDataSourceProperty("URL", "jdbc:h2:mem:bank;DB_CLOSE_ON_EXIT=FALSE;INIT=CREATE SCHEMA IF NOT EXISTS bank\\;SET SCHEMA bank");
        config.addDataSourceProperty("user", "sa");
        //config.addDataSourceProperty("password", "sa");

        return new HikariDataSource(config);
    }*/

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        if (environment.acceptsProfiles("inmemory")) {
            config.setDataSourceClassName("org.h2.jdbcx.JdbcDataSource");
            config.setConnectionTestQuery("VALUES 1");
            config.addDataSourceProperty("URL", "jdbc:h2:mem:bank;DB_CLOSE_ON_EXIT=FALSE;INIT=CREATE SCHEMA IF NOT EXISTS bank\\;SET SCHEMA bank");
            config.addDataSourceProperty("user", "sa");
        }
        else {
            config.setDriverClassName(driver);
            config.setJdbcUrl(url);
            config.setUsername(username);
            config.setPassword(password);
        }
        return new HikariDataSource(config);
    }

    /*@Bean
    public HibernateTransactionManager transactionManager() {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory().getObject());
        return transactionManager;
    }*/

    public JpaTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setDataSource(dataSource());
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return transactionManager;
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean localSessionFactoryBean = new LocalSessionFactoryBean();
        localSessionFactoryBean.setDataSource(dataSource());
        localSessionFactoryBean.setPackagesToScan("com.sentilabs.royaltyplanttask.entity");
        return localSessionFactoryBean;
        /*if (entityManagerFactory.unwrap(SessionFactory.class) == null) {
            throw new NullPointerException("factory is not a hibernate factory");
        }
        return entityManagerFactory.unwrap(SessionFactory.class);*/
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setPackagesToScan("com.sentilabs.royaltyplanttask.entity");
        localContainerEntityManagerFactoryBean.setDataSource(dataSource());
        HibernatePersistenceProvider hibernatePersistenceProvider = new HibernatePersistenceProvider();
        localContainerEntityManagerFactoryBean.setPersistenceProvider(hibernatePersistenceProvider);

        return localContainerEntityManagerFactoryBean;
    }

}
