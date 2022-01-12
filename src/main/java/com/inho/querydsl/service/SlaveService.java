package com.inho.querydsl.service;

import com.inho.querydsl.entity.Member;
import com.inho.querydsl.repository.MemberRepository;
import com.inho.querydsl.web.dto.MemberDto;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.util.Optional;

@Service

public class SlaveService {

    private final MemberRepository memberRepository;
    private final DataSource dataSource;

    public SlaveService(MemberRepository memberRepository, DataSource dataSource) {
        this.memberRepository = memberRepository;
        this.dataSource = dataSource;
    }

    @Transactional(readOnly = true)
    public void select()
    {
        //printConnectionStatus();

        boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
        System.out.println("isReadOnly = " + isReadOnly);
        System.out.println("========findById::start==========");
        Optional<Member> findMember = memberRepository.findById(1L);
        System.out.println("findMember = " + findMember);

        System.out.println("dataSource.getClass() = " + dataSource.getClass());

        System.out.println("========findById::end==========");
    }

    private void printConnectionStatus() {
        final HikariPoolMXBean hikariPoolMXBean = ((HikariDataSource) dataSource).getHikariPoolMXBean();
        System.out.println("################################");
        System.out.println("현재 active인 connection의 수 : " + hikariPoolMXBean.getActiveConnections());
        System.out.println("현재 idle인 connection의 수 : " + hikariPoolMXBean.getIdleConnections());
        System.out.println("################################");
    }
}
