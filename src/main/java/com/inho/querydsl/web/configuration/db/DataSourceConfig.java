package com.inho.querydsl.web.configuration.db;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class DataSourceConfig {


    @Bean(name = "masterDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.master")
    public DataSource masterDataSource(){
        log.info("[REPLACTION] masterDataSource 생성");
        return DataSourceBuilder.create()
                .build();
    }


    @Bean(name = "slaveDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.slave")
    public DataSource slaveDataSource(){
        log.info("[REPLACTION] slaveDataSource 생성");
        return DataSourceBuilder.create()
                .build();
    }


    @Bean(name = "routingDataSource")
    public DataSource routingDataSource (@Qualifier("masterDataSource") DataSource masterDataSource,
                                         @Qualifier("slaveDataSource") DataSource slaveDataSource) {
        ReplicationRoutingDataSource routingDataSource = new ReplicationRoutingDataSource();

        Map<Object, Object> dataSourceMap = new HashMap<>();

        dataSourceMap.put(DataSourceType.Master, masterDataSource);
        dataSourceMap.put(DataSourceType.Slave, slaveDataSource);

        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.setDefaultTargetDataSource(masterDataSource);

        return routingDataSource;
    }

    /**
     * 스프링에서 Transaction 생성 시점에 DataSource Connection을 가져오게 되는데, 항상 @Primary가 붙은 Datasource를 반환하게 된다.
     * 따라서 Transaction 생성 시점이 아닌 실제로 쿼리가 발생하는 시점에 Datasource Connection을 가져오기 위해서 사용.
     * @param routingDataSource
     * @return
     */
    @Primary
    @Bean(name="dataSource")
    public DataSource dataSource(@Qualifier("routingDataSource") DataSource  routingDataSource)
    {
        //return new LazyReplicationConnectionDataSourceProxy(writeDataSource, readDataSource);
        LazyConnectionDataSourceProxy dataSourceProxy = new LazyConnectionDataSourceProxy(routingDataSource);
        log.info("[REPLACTION] dataSourceProxy : {} ", dataSourceProxy);
        return dataSourceProxy;
    }


    @Bean(name="transactionManager")
    public PlatformTransactionManager transactionManager(@Qualifier("routingDataSource") DataSource dataSource)
    {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("routingDataSource") DataSource dataSource,
                                               ApplicationContext applicationContext) throws Exception {

        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath:mapper/**/*.xml"));
        sqlSessionFactoryBean.setTypeAliasesPackage("com.inho.querydsl.web.dto");

        return sqlSessionFactoryBean.getObject();
    }

    @Bean(name = "sqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {

        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
