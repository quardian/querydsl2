package com.inho.querydsl.service;

import com.inho.querydsl.entity.Member;
import com.inho.querydsl.repository.MemberRepository;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.util.Optional;

@Service
@Transactional(readOnly = false)
@RequiredArgsConstructor
public class MasterService {
    private final MemberRepository memberRepository;
    private final DataSource dataSource;

    public void insert()
    {
        //printConnectionStatus();
        System.out.println("========insert::start==========");
        Member member = new Member("leeinho", 48);
        memberRepository.save(member);
        System.out.println("member = " + member);
        System.out.println("dataSource.getClass() = " + dataSource.getClass());
        System.out.println("========insert::end==========");
    }

    private void printConnectionStatus() {
        final HikariPoolMXBean hikariPoolMXBean = ((HikariDataSource) dataSource).getHikariPoolMXBean();
        System.out.println("################################");
        System.out.println("현재 active인 connection의 수 : " + hikariPoolMXBean.getActiveConnections());
        System.out.println("현재 idle인 connection의 수 : " + hikariPoolMXBean.getIdleConnections());
        System.out.println("################################");
    }
}
