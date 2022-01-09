package com.inho.querydsl.basic;

import com.inho.querydsl.entity.*;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.List;

import static com.inho.querydsl.entity.QMember.member;
import static com.inho.querydsl.entity.QTeam.team;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class QuerydslMiddleTest {
    @PersistenceContext
    private EntityManager em;

    @PersistenceUnit
    private EntityManagerFactory emf;


    @Autowired
    JPAQueryFactory queryFactory;

    @BeforeEach
    void beforeEach()
    {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        Member member5 = new Member("null", 100);
        Member member6 = new Member("member5", 100);
        Member member7 = new Member("member6", 100);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
        em.persist(member5);
        em.persist(member6);
        em.persist(member7);
    }

    @Test
    @DisplayName("프로젝션 대상 하나")
    void projectionOneTest()
    {
        List<String> fetch = queryFactory
                .select(member.username)
                .from(member)
                .fetch();
    }

    @Test
    @DisplayName("프로젝션 대상 하나")
    void projectionTwoGTTest()
    {
        List<String> fetch = queryFactory
                .select(member.username)
                .from(member)
                .fetch();
    }


}
