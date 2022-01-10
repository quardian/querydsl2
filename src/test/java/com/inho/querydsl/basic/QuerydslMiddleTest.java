package com.inho.querydsl.basic;

import com.inho.querydsl.entity.*;
import com.inho.querydsl.web.dto.MemberDto;
import com.inho.querydsl.web.dto.QMemberDto;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.List;

import static com.inho.querydsl.entity.QMember.member;
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

        for (String username : fetch) {
            System.out.println("username = " + username);
        }
    }

    @Test
    @DisplayName("프로젝션 대상 2개")
    void projectionTwoGTTest()
    {
        List<Tuple> fetch = queryFactory
                .select(
                        member.username,
                        member.age
                )
                .from(member)
                .fetch();

        for (Tuple tuple : fetch) {
            String username = tuple.get(member.username);
            Integer age = tuple.get(member.age);
            System.out.println("username = " + username + ", age = " + age);
        }
    }

    @Test
    @DisplayName("순수 JPA에서 DTO 조회")
    void projectionJpaDtoTest()
    {
        String qlString = "select " +
                " new com.inho.querydsl.web.dto.MemberDto( " +
                "   m.username, " +
                "   m.age " +
                ") from Member m";

        List<MemberDto> resultList = em.createQuery(qlString, MemberDto.class)
                .getResultList();

        for (MemberDto memberDto : resultList) {
            System.out.println("memberDto = " + memberDto);
        }
    }


    @Test
    @DisplayName("Querydsl에서 DTO 조회")
    void projectQuerydslDtoTest()
    {
        // 프로퍼티 접근 - Setter
        List<MemberDto> fetch = queryFactory
                .select(
                        Projections.bean(MemberDto.class,
                                member.username,
                                member.age)
                )
                .from(member)
                .fetch();

        for (MemberDto member : fetch) {
            System.out.println("username = " + member.getUsername() + ", age = " + member.getUsername());
        }


        // 필드 직접 접근
        fetch = queryFactory
                .select(
                        Projections.fields(MemberDto.class,
                                member.username,
                                member.age)
                )
                .from(member)
                .fetch();

        for (MemberDto member : fetch) {
            System.out.println("username = " + member.getUsername() + ", age = " + member.getUsername());
        }


        // 생성자 접근
        fetch = queryFactory
                .select(
                        Projections.constructor(MemberDto.class,
                                member.username,
                                member.age)
                )
                .from(member)
                .fetch();

        for (MemberDto member : fetch) {
            System.out.println("username = " + member.getUsername() + ", age = " + member.getUsername());
        }

        QMember memberSub = new QMember("memberSub");
        List<MemberDto> fetch1 = queryFactory
                .select(
                        Projections.fields(MemberDto.class,
                                member.username.as("username"),

                                ExpressionUtils.as( JPAExpressions
                                                .select(memberSub.age.max())
                                                .from(memberSub)
                                                .from(member)
                                        , "age")
                        )
                )
                .from(member)
                .fetch();
        for (MemberDto memberDto : fetch1) {
            System.out.println("memberDto = " + memberDto);
        }


    }

    @Test
    @DisplayName("@QueryProjection")
    void queryProjectionTest()
    {
        List<MemberDto> fetch = queryFactory
                .select(new QMemberDto(
                        member.username,
                        member.age
                ))
                .from(member)
                .fetch();

        for (MemberDto memberDto : fetch) {
            System.out.println("memberDto = " + memberDto);
        }
    }



}
