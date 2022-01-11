package com.inho.querydsl.web.configuration.db;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DatasourceConfigTest {
    private static final String Test_Method_Name = "determineCurrentLookupKey";

    @Test
    @DisplayName("쓰기 전용 트랙샌션 테스트")
    @Transactional(readOnly = false)
    void writeOnlyTransactionTest() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        ReplicationRoutingDataSource replicationRoutingDataSource = new ReplicationRoutingDataSource();

        Method determinCurrentLookupKey = ReplicationRoutingDataSource.class.getDeclaredMethod(Test_Method_Name);
        determinCurrentLookupKey.setAccessible(true);

        DataSourceType dataSourceType = (DataSourceType) determinCurrentLookupKey.invoke(replicationRoutingDataSource);

        Assertions.assertThat(dataSourceType).isEqualTo(DataSourceType.Master);
    }

    @Test
    @DisplayName("읽기 전용 트랙샌션 테스트")
    @Transactional(readOnly = true)
    void readOnlyTransactionTest() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        ReplicationRoutingDataSource replicationRoutingDataSource = new ReplicationRoutingDataSource();

        Method determinCurrentLookupKey = ReplicationRoutingDataSource.class.getDeclaredMethod(Test_Method_Name);
        determinCurrentLookupKey.setAccessible(true);

        DataSourceType dataSourceType = (DataSourceType) determinCurrentLookupKey.invoke(replicationRoutingDataSource);

        Assertions.assertThat(dataSourceType).isEqualTo(DataSourceType.Slave);
    }



}