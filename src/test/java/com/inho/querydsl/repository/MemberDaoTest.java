package com.inho.querydsl.repository;

import com.inho.querydsl.service.MasterService;
import com.inho.querydsl.service.SlaveService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

@SpringBootTest

@Rollback(value = false)
public class MemberDaoTest {
    @Autowired
    private  MasterService masterService;

    @Autowired
    private  SlaveService slaveService;

    @BeforeEach
    @Transactional
    void beforeEach() throws SQLException {
        //masterService.insert();
        masterService.insertMybatis();
    }

    @Test
    @Transactional(readOnly = true)
    void memberDaoSelectTest() throws SQLException {
        slaveService.select();
        slaveService.selectMybatis();
    }

}