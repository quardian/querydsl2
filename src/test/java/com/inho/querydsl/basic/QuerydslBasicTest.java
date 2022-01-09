package com.inho.querydsl.basic;

import com.inho.querydsl.entity.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {
    @PersistenceContext
    private EntityManager em;

    @Test
    @DisplayName("Querydsl 설정 확인")
    void querydslSettings()
    {
        Hello hello = new Hello();
        em.persist(hello);

        JPAQueryFactory query = new JPAQueryFactory(em);
        QHello qhello = new QHello("h");

        Hello result = query.selectFrom(qhello)
                .fetchOne();

        Assertions.assertThat(result).isEqualTo(hello);
    }


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

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }

    @Test
    @DisplayName("JPA 시작")
    void jpqlTest()
    {
        String qlString = "select m from Member m where m.username = :username";
        Member findMember = em.createQuery(qlString, Member.class)
                .setParameter("username", "member1")
                .getSingleResult();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    @DisplayName("Querydsl 시작")
    void querydslTest()
    {
        // [01] JPAQueryFactory 생성
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        // [02]
        QMember m = new QMember("m");

        // [03] querydsl 작성 : PrepareStatement 로 변환함
        Member findMember = queryFactory
                            .select(m)
                            .from(m)
                            .where(m.username.eq("member1"))
                            .fetchOne();
        // [04] 검증
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

}
