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
public class QuerydslBasicTest {
    @PersistenceContext
    private EntityManager em;

    @PersistenceUnit
    private EntityManagerFactory emf;


    @Autowired
    JPAQueryFactory queryFactory;

    @Test
    @DisplayName("Querydsl 설정 확인")
    void querydslSettings()
    {
        Hello hello = new Hello();
        em.persist(hello);

        //JPAQueryFactory query = new JPAQueryFactory(em);
        QHello qhello = new QHello("h");

        Hello result = queryFactory.selectFrom(qhello)
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
        Member findMember = queryFactory
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
        Member findMember = queryFactory
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
        Member findMember = queryFactory
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
        Member findMember = queryFactory
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
        List<Member> fetch = queryFactory
                .selectFrom(member)
                .fetch();

        // 단 건
        Member fetchOne = queryFactory
                .selectFrom(QMember.member)
                .fetchOne();

        // 처음 한 건 조회
        Member fetchFirst = queryFactory
                .selectFrom(QMember.member)
                .fetchFirst();

        // 페이징 사용 ( deprecated )
        QueryResults<Member> fetchResults = queryFactory
                .selectFrom(member)
                .fetchResults();

        long total = fetchResults.getTotal();
        List<Member> results = fetchResults.getResults();
        long limit = fetchResults.getLimit();
        long offset = fetchResults.getOffset();

        // count ( deprecated )
        long fetchCount = queryFactory
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
        List<Member> fetch = queryFactory
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
        List<Member> fetch = queryFactory
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
        QueryResults<Member> results = queryFactory
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
    @DisplayName("JOIN")
    void joinQuerydsl()
    {

        List<Member> members = queryFactory
                .select(member)
                .from(member)
                .leftJoin(member.team, team)
                .where(team.name.eq("teamA"))
                .fetch();
    }


    @Test
    @DisplayName("thetajoinQuerydsl")
    void thetajoinQuerydsl()
    {

        List<Member> members = queryFactory
                .select(member)
                .from(member, team)
                .where(team.name.eq("teamA"))
                .fetch();
    }




    @Test
    @DisplayName("조인 대상 필터링")
    void joinOnFiltering()
    {
        /** 회원과 팀을 조인하면서,
         * 팀 이름이 teamA인 팀만 조인, 회원은 모두 조인
         * JPQL :
         * select
         *  m,t
         * form         Member  m
         * left join    m.team   t on t.name = 'teamA'
         */
        List<Tuple> members = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(member.team, team).on(team.name.eq("teamA"))
                .fetch();

        for (Tuple tuple : members) {
            System.out.println("tuple = " + tuple);
        }

        List<Tuple> members1 = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(member.team, team)
                .where(team.name.eq("teamA"))
                .fetch();

        for (Tuple tuple : members1) {
            System.out.println("tuple = " + tuple);
        }
    }


    @Test
    @DisplayName("연관관계 없는 외부 조인")
    void externalJoin()
    {
        List<Tuple> fetch = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(team).on(member.id.eq(team.id))
                .fetch();
        for (Tuple tuple : fetch) {
            System.out.println("tuple = " + tuple);
        }
    }

    @Test
    @DisplayName("noFetchJoin")
    void noFetchJoin()
    {
        em.flush();
        em.clear();

        Member member1 = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        PersistenceUnitUtil persistenceUnitUtil = emf.getPersistenceUnitUtil();
        boolean loaded = persistenceUnitUtil.isLoaded(member1.getTeam());

        assertThat(loaded).as("페치 조인 미적용").isFalse();
    }

    @Test
    @DisplayName("FetchJoin")
    void fetchJoin()
    {
        em.flush();
        em.clear();

        Member member1 = queryFactory
                .selectFrom(member)
                .join(member.team, team).fetchJoin()
                .where(member.username.eq("member1"))
                .fetchOne();

        PersistenceUnitUtil persistenceUnitUtil = emf.getPersistenceUnitUtil();
        boolean loaded = persistenceUnitUtil.isLoaded(member1.getTeam());

        assertThat(loaded).as("페치 조인 적용").isTrue();
    }


    @Test
    @DisplayName("subQuery - 나이가 가장 많은 회원 조회 ")
    void subQuery()
    {
        /** 나이가 가장 많은 회원 조회 */
        QMember ms = new QMember("ms");

        List<Member> fetch = queryFactory
                .selectFrom(member)
                .where(member.age.eq(
                        JPAExpressions
                                .select(ms.age.max())
                                .from(ms)
                )).fetch();
    }

    @Test
    @DisplayName("subQuery - 나이가 평균 이상인 회원 조회")
    void subQuery1()
    {
        /** 나이가 평균 이상인 회원 조회 */
        QMember ms = new QMember("ms");

        List<Member> fetch = queryFactory
                .selectFrom(member)
                .where(member.age.goe(
                        JPAExpressions
                                .select(ms.age.avg())
                                .from(ms)
                )).fetch();
    }


    @Test
    @DisplayName("subQueryIn ")
    void subQueryIn()
    {
        QMember ms = new QMember("ms");

        List<Member> fetch = queryFactory
                .selectFrom(member)
                .where( member.id.in ( JPAExpressions
                                .select(ms.id)
                                .from(ms)
                                .where(ms.age.gt(10))
                )).fetch();
    }


    @Test
    @DisplayName("selectSubquery ")
    void selectSubquery()
    {
        QMember ms = new QMember("ms");

        List<Tuple> fetch = queryFactory
                .select(member.username,
                        JPAExpressions
                                .select(ms.age.avg())
                                .from(ms)
                )
                .from(member)
                .fetch();

        for (Tuple tuple : fetch) {
            System.out.println("tuple = " + tuple);
        }
    }

    @Test
    @DisplayName("조건식 ")
    void caseSimple()
    {
        System.out.println("===단순한 조건식 ==");
        List<String> caseResult1 = queryFactory
                .select(member.age
                        .when(10).then("10살")
                        .when(20).then("20살")
                        .otherwise("기타"))
                .from(member)
                .fetch();
        for (String s : caseResult1) {
            System.out.println("s = " + s);
        }

        System.out.println("===단순한 조건식 [JPQL]==");
        String qlString = "select " +
                " case m.age" +
                "   when 10 then '10살' " +
                "   when 20 then '20살' " +
                "   else '기타' " +
                " end " +
                "From Member m";
        TypedQuery<String> query = em.createQuery(qlString, String.class);
        List<String> resultList = query.getResultList();
        for (String s : resultList) {
            System.out.println("s = " + s);
        }


        System.out.println("===복잡한 조건식 ==");
        List<String> caseResult2 = queryFactory
                .select( new CaseBuilder()
                                .when( member.age.between( 0, 20) ).then("0~20살")
                                .when( member.age.between(21, 30) ).then("21~30살")
                                .otherwise("기타") )
                .from(member)
                .fetch();
        for (String s : caseResult2) {
            System.out.println("s = " + s);
        }

        System.out.println("===복잡한 조건식 [JPQL]==");
        qlString = "select " +
                " case " +
                "   when m.age between 0 and 20 then '0~20살' " +
                "   when m.age between 21 and 30 then '21~30살' " +
                "   else '기타' " +
                " end " +
                "From Member m";
        query = em.createQuery(qlString, String.class);
        resultList = query.getResultList();
        for (String s : resultList) {
            System.out.println("s = " + s);
        }
    }


    @Test
    @DisplayName("상수,문자 더하기 ")
    void constantTest()
    {
        Tuple result = queryFactory
                .select(member.username,
                        Expressions.constant("A")
                )
                .from(member)
                .limit(1)
                .fetchOne();
        System.out.println("result = " + result);

        String s = queryFactory
                .select(
                        member.username.concat("_")
                                .concat(member.age.stringValue())
                                .as("username_age")
                )
                .from(member)
                .where(member.username.eq("member1"))
                .fetchOne();
        System.out.println("s = " + s);
    }

}
