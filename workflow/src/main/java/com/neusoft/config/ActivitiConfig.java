package com.neusoft.config;

import org.activiti.spring.SpringProcessEngineConfiguration;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ActivitiConfig {

    @Autowired
    public PlatformTransactionManager transactionManager;

    @Autowired
    public DataSource dataSource;

    @Bean
    public SpringProcessEngineConfiguration getProcessEngineConfiguration() {
        SpringProcessEngineConfiguration config = new SpringProcessEngineConfiguration();
        config.setDataSource(dataSource);
        config.setTransactionManager(transactionManager);
        config.setDatabaseType("mysql");// 必须小写。。。，不配置的话，activiti会根据DataSource去判断的
        config.setDatabaseSchemaUpdate("true");

        //流程图字体
        config.setActivityFontName("宋体");
        config.setAnnotationFontName("宋体");
        config.setLabelFontName("宋体");
        return config;
    }
}