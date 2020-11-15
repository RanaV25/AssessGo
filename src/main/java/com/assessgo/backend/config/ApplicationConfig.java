package com.assessgo.backend.config;

import org.modelmapper.ModelMapper;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;

@Configuration
@ConfigurationProperties("spring.datasource")

public class ApplicationConfig {

    private String driverClassName;
    private String url;
    private String username;
    private String password;

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Profile("local-h2")
    @Bean
    public String localh2DatabaseConnection() {
        System.out.println("DB connection for LOCAL - H2");
        System.out.println(driverClassName);
        System.out.println(url);
        return "DB connection for LOCAL - H2";
    }

    @Profile("local")
    @Bean
    public String localDatabaseConnection() {
        System.out.println("DB connection for LOCAL - POSTGRESQL");
        System.out.println(driverClassName);
        System.out.println(url);
        return "DB connection for LOCAL - POSTGRESQL";
    }

    @Profile("staging")
    @Bean
    public String stagingDatabaseConnection() {
        System.out.println("DB connection for STAGING");
        System.out.println(driverClassName);
        System.out.println(url);
        return "DB connection for STAGING";
    }

    @Profile("production")
    @Bean
    public String productionDatabaseConnection() {
        System.out.println("DB connection for PRODUCTION");
        System.out.println(driverClassName);
        System.out.println(url);
        return "DB connection for PRODUCTION";
    }


    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    /**
     * The password encoder to use when encrypting passwords.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);

        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

}
