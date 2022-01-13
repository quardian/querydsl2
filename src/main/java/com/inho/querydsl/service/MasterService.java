package com.inho.querydsl.service;

import com.inho.querydsl.entity.Member;
import com.inho.querydsl.repository.MemberDao;
import com.inho.querydsl.repository.MemberRepository;
import com.inho.querydsl.web.dto.MemberDto;
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

    private final MemberDao memberDao;

    public void insert()
    {
        System.out.println("========insert::start==========");
        Member member = new Member("leeinho", 48);
        memberRepository.save(member);
        System.out.println("member = " + member);
        System.out.println("========insert::end==========");
    }


    public void insertMybatis()
    {
        System.out.println("========Mybatis.insert::start==========");
        MemberDto member = new MemberDto("leeinho", 48);
        memberDao.insert(member);
        System.out.println("member = " + member);
        System.out.println("========Mybatis.insert::end==========");
    }
}
