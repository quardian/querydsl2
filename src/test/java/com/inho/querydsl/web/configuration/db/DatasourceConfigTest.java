package com.inho.querydsl.web.configuration.db;

import com.inho.querydsl.entity.Member;
import com.inho.querydsl.repository.MemberRepository;
import org.apache.ibatis.session.SqlSessionFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;

@SpringBootTest
class DatasourceConfigTest {

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    private MemberRepository memberRepository;

    private static final String Test_Method_Name = "determineCurrentLookupKey";

    @Test
    @DisplayName("쓰기 전용 트랙샌션 테스트")
    @Transactional(readOnly = false)
    @Rollback(false)
    void writeOnlyTransactionTest() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        RoutingDataSource replicationRoutingDataSource = new RoutingDataSource();

        Method determinCurrentLookupKey = RoutingDataSource.class.getDeclaredMethod(Test_Method_Name);
        determinCurrentLookupKey.setAccessible(true);

        DataSourceType dataSourceType = (DataSourceType) determinCurrentLookupKey.invoke(replicationRoutingDataSource);

        Member member = new Member("이인호", 100);
        memberRepository.saveAndFlush(member);

        Assertions.assertThat(dataSourceType).isEqualTo(DataSourceType.Master);
    }

    @Test
    @DisplayName("읽기 전용 트랙샌션 테스트")
    @Transactional(readOnly = true)
    @Rollback(false)
    void readOnlyTransactionTest() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        RoutingDataSource replicationRoutingDataSource = new RoutingDataSource();

        Method determinCurrentLookupKey = RoutingDataSource.class.getDeclaredMethod(Test_Method_Name);
        determinCurrentLookupKey.setAccessible(true);

        DataSourceType dataSourceType = (DataSourceType) determinCurrentLookupKey.invoke(replicationRoutingDataSource);

        Member member = new Member("이인호", 100);
        memberRepository.saveAndFlush(member);

        Assertions.assertThat(dataSourceType).isEqualTo(DataSourceType.Slave);
    }


    @Test
    @DisplayName("Mybatis 연결 테스트")
    void connection_test()
    {
        try{
            System.out.println("연결 시도======================================");
            Connection conn = sqlSessionFactory.openSession().getConnection();
            System.out.println("연결 성공======================================");
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }


}