package com.inho.querydsl.repository;

import com.inho.querydsl.web.dto.MemberDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemberDaoTest {

    @Autowired MemberDao memberDao;
    @Autowired DaoTestImpl daoTest;

    @BeforeEach
    @Transactional(readOnly = false)
    void beforeEach() throws SQLException {
        boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
        System.out.println("isReadOnly = " + isReadOnly);

        System.out.println("========insert::start==========");
        MemberDto member = new MemberDto("leeinho", 48);
        memberDao.insert(member);
        System.out.println("member = " + member);

        System.out.println("========insert::end==========");
    }

    @Test
    void memberDaoTest()
    {
        System.out.println("========start::findById==========");
        MemberDto findMember = memberDao.findById(1L);
        System.out.println("findMember = " + findMember);
        System.out.println("========end::findById==========");
    }

    @Test
    void memberDaoSelectTest() throws SQLException {
        daoTest.select();
    }

    @Component
    static class DaoTestImpl
    {
        @Autowired MemberDao memberDao;

        @Transactional(readOnly = true)
        public void select()
        {
            System.out.println("========start::findById==========");
            MemberDto findMember = memberDao.findById(1L);
            System.out.println("findMember = " + findMember);
            System.out.println("========end::findById==========");
        }
    }
}