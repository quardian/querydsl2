package com.inho.querydsl.web.configuration.db;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
@EnableTransactionManagement
//@EnableJpaRepositories(basePackages = "com.inho.querydsl.repository")
public class DataSourceConfig {
    public static final String MASTER_DATASOURCE = "masterDataSource";
    public static final String SLAVE_DATASOURCE = "slaveDataSource";
    public static final String ROUTING_DATASOURCE = "routingDataSource";
    public static final String DATASOURCE = "dataSource";

    @Bean(name = MASTER_DATASOURCE)
    @ConfigurationProperties(prefix = "spring.datasource.master")
    public DataSource   masterDataSource(){
        log.info("[REPLACTION] masterDataSource ??????");
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .build();
    }


    @Bean(name = SLAVE_DATASOURCE)
    @ConfigurationProperties(prefix = "spring.datasource.slave")
    public DataSource   slaveDataSource(){
        log.info("[REPLACTION] slaveDataSource ??????");
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .build();
    }


    @Bean(name = ROUTING_DATASOURCE)
    @DependsOn({MASTER_DATASOURCE, SLAVE_DATASOURCE})
    public DataSource routingDataSource (@Qualifier(MASTER_DATASOURCE) DataSource masterDataSource,
                                         @Qualifier(SLAVE_DATASOURCE) DataSource slaveDataSource) {

        RoutingDataSource routingDataSource = new RoutingDataSource();

        Map<Object, Object> dataSourceMap = Map.of(
                DataSourceType.Master, masterDataSource,
                DataSourceType.Slave, slaveDataSource
        );

        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.setDefaultTargetDataSource(masterDataSource);

        return routingDataSource;
    }


    /**
     * ??????????????? Transaction ?????? ????????? DataSource Connection??? ???????????? ?????????, ?????? @Primary??? ?????? Datasource??? ???????????? ??????.
     * ????????? Transaction ?????? ????????? ?????? ????????? ????????? ???????????? ????????? Datasource Connection??? ???????????? ????????? ??????.
     * @return
     */
    @Bean(name=DATASOURCE)
    @Primary
    public DataSource dataSource(@Qualifier(ROUTING_DATASOURCE) DataSource routingDataSource)
    {
        log.info("LazyConnectionDataSourceProxy ??????");
        return new LazyConnectionDataSourceProxy(routingDataSource);
    }

    @Bean
    public PlatformTransactionManager transactionManager(
            @Qualifier(value =DATASOURCE) DataSource lazyRoutingDataSource) {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        //JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setDataSource(lazyRoutingDataSource);
        return transactionManager;
    }

    @Primary
    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier(ROUTING_DATASOURCE) DataSource lazyConnectionDataSource,
                                               ApplicationContext applicationContext) throws Exception {

        log.info("###sqlSessionFactory ??????");
        log.info("sqlSessionFactory.datasourc={}", lazyConnectionDataSource);

        Resource[] resources = new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml");
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(lazyConnectionDataSource);
        sqlSessionFactoryBean.setMapperLocations(resources);
        sqlSessionFactoryBean.setTypeAliasesPackage("com.inho.querydsl.web.dto");

        return sqlSessionFactoryBean.getObject();
    }

    @Primary
    @Bean(name = "sqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        log.info("###sqlSessionTemplate ??????");
        return new SqlSessionTemplate(sqlSessionFactory);
    }


}
