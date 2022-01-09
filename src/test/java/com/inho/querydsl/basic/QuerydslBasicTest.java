package com.inho.querydsl.basic;

import com.inho.querydsl.entity.*;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import static com.inho.querydsl.entity.QMember.member;
import static com.inho.querydsl.entity.QTeam.team;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {
    @PersistenceContext
    private EntityManager em;

    @Autowired
    JPAQueryFactory query;

    @Test
    @DisplayName("Querydsl 설정 확인")
    void querydslSettings()
    {
        Hello hello = new Hello();
        em.persist(hello);

        //JPAQueryFactory query = new JPAQueryFactory(em);
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
        //JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        // [02]
        QMember m = new QMember("m");

        // [03] querydsl 작성 : PrepareStatement 로 변환함
        Member findMember = query
                            .select(m)
                            .from(m)
                            .where(m.username.eq("member1"))
                            .fetchOne();
        // [04] 검증
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }


    @Test
    @DisplayName("Q-Type")
    void qTypeTest()
    {
        // [01] JPAQueryFactory 생성
        //JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        // [03] querydsl 작성 : PrepareStatement 로 변환함
        Member findMember = query
                .select(member)
                .from(member)
                .where(member.username.eq("member1"))
                .fetchOne();
        // [04] 검증
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    @DisplayName("검색조건")
    void querydslSearch()
    {
        Member findMember = query
                        .selectFrom(member)
                        .where(member.username.eq("member1")
                                .and(member.age.eq(10)))
                        .fetchOne();
        assertThat(findMember.getUsername()).isEqualTo("member1");

    }


    @Test
    @DisplayName("AndParam")
    void querydslAndParam()
    {
        Member findMember = query
                .selectFrom(member)
                .where(
                        member.username.eq("member1")
                        , (member.age.eq(10))
                )
                .fetchOne();
        assertThat(findMember.getUsername()).isEqualTo("member1");

    }



    @Test
    @DisplayName("결과조회")
    void resultViewquerydsl()
    {
        // List
        List<Member> fetch = query
                .selectFrom(member)
                .fetch();

        // 단 건
        Member fetchOne = query
                .selectFrom(QMember.member)
                .fetchOne();

        // 처음 한 건 조회
        Member fetchFirst = query
                .selectFrom(QMember.member)
                .fetchFirst();

        // 페이징 사용 ( deprecated )
        QueryResults<Member> fetchResults = query
                .selectFrom(member)
                .fetchResults();

        long total = fetchResults.getTotal();
        List<Member> results = fetchResults.getResults();
        long limit = fetchResults.getLimit();
        long offset = fetchResults.getOffset();

        // count ( deprecated )
        long fetchCount = query
                .selectFrom(member)
                .fetchCount();

    }


    @Test
    @DisplayName("정렬")
    void sortQuerydsl()
    {
        /**
         * 회원 정렬 순서
         * 1. 회원 나이 내림차순(desc)
         * 2. 회원 이름 올림차순(asc)
         * 단 2 에서 회원 이름이 엇으면 마지막에 출력 ( nulls last )
          */
        List<Member> fetch = query
                .selectFrom(member)
                .where(member.age.eq(100))
                .orderBy(member.age.desc(), member.username.asc().nullsLast())
                .fetch();
        for (Member member : fetch) {
            System.out.println("member = " + member);
        }
    }

    @Test
    @DisplayName("페이징1")
    void paging1Querydsl()
    {
        List<Member> fetch = query
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(0)
                .limit(2)
                .fetch();

    }

    @Test
    @DisplayName("페이징2")
    void paging2Querydsl()
    {
        QueryResults<Member> results = query
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(0)
                .limit(2)
                .fetchResults();
        long total = results.getTotal();
        long limit = results.getLimit();
        long offset = results.getOffset();
        List<Member> members = results.getResults();
    }



    @Test
    @DisplayName("집합")
    void aggregationQuerydsl()
    {
        /**
         * JPQL
         * select
         *      COUNT(m),
         *      SUM(m.age),
         *      AVG(m.age),
         *      MAX(m.age),
         *      MIN(m.age)
         * from Member m
         */
        List<Tuple> fetch = query
                .select(
                        member.count(),
                        member.age.sum(),
                        member.age.avg(),
                        member.age.max(),
                        member.age.min()
                )
                .from(member)
                .fetch();

        Tuple tuple = fetch.get(0);
        Long memberCount = tuple.get(member.count());
        Integer ageSum = tuple.get(member.age.sum());
        Double ageAvg = tuple.get(member.age.avg());
        Integer ageMax = tuple.get(member.age.max());
        Integer ageMin = tuple.get(member.age.min());



    }

    @Test
    @DisplayName("GroupBy")
    void groupByQuerydsl()
    {
        /**
         * 팀의 이름과 각 팀의 평균 연령을 구래하
         */
        List<Tuple> fetch = query
                .select(
                        team.name,
                        member.age.avg()
                ).from(member)
                .join(member.team, team)
                .groupBy(team.name)
                .fetch();

        for (Tuple tuple : fetch)
        {
            String teamName = tuple.get(team.name);
            Double ageAvg = tuple.get(member.age.avg());
            System.out.println( "teamName=" + teamName + ", ageAvg = " + ageAvg);
        }

    }
}
