package com.inho.querydsl.service;

import com.inho.querydsl.entity.Member;
import com.inho.querydsl.repository.MemberDao;
import com.inho.querydsl.repository.MemberRepository;
import com.inho.querydsl.web.configuration.db.DataSourceType;
import com.inho.querydsl.web.configuration.db.DbContextHolder;
import com.inho.querydsl.web.dto.MemberDto;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Optional;

@Service
@Transactional(readOnly = false)
@RequiredArgsConstructor
public class SlaveService {

    private final MemberRepository memberRepository;

    //private final SqlSessionFactory sqlSessionFactory;
    //private final SqlSessionTemplate sqlSessionTemplate;

    private final MemberDao memberDao;

    @Transactional(readOnly = true)
    public void select()
    {
        DataSourceType dataSourceType = DbContextHolder.get();
        boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
        System.out.println("select dataSourceType = " + dataSourceType);
        System.out.println(" transaction.isReadOnly = " + isReadOnly);
        System.out.println("========findById::start==========");
        Optional<Member> findMember = memberRepository.findById(1L);
        System.out.println("findMember = " + findMember);
        System.out.println("========findById::end==========");
    }


    @Transactional(readOnly = true)
    public void selectMybatis()
    {
        System.out.println("========Mybatis.findById::start==========");
        //Configuration configuration = sqlSessionFactory.getConfiguration();
        //Connection connection = sqlSessionTemplate.getConnection();

        DataSourceType dataSourceType = DbContextHolder.get();
        boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
        System.out.println(" dataSourceType = " + dataSourceType);
        System.out.println(" transaction.isReadOnly = " + isReadOnly);
        MemberDto findMember = memberDao.findById(1L);
        System.out.println("findMember = " + findMember);
        System.out.println("========Mybatis.findById::end==========");
    }

}
