package com.inho.querydsl.basic;

import com.inho.querydsl.entity.*;
import com.inho.querydsl.web.dto.MemberDto;
import com.inho.querydsl.web.dto.QMemberDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.Arrays;
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


    @Test
    @DisplayName("동적쿼리-BooleanBuilder 사용")
    void dynamicBooleanBuilderTest()
    {
        // 동적쿼리 파라미터(가정)
        String usernameParam = "member1";
        Integer ageParam = 10;

        List<Member> result = searchMember1(usernameParam, ageParam);
        Assertions.assertThat(result.size()).isEqualTo(1);

    }

    private List<Member> searchMember1(String usernameCond, Integer ageCond)
    {
        BooleanBuilder builder = new BooleanBuilder(member.username.eq(usernameCond));
        if ( ageCond != null ){
            builder.and( member.age.eq(ageCond) );
        }
        List<Member> result = queryFactory
                .select(member)
                .from(member)
                .where(builder)
                .fetch();

        return result;
    }


    @Test
    @DisplayName("동적쿼리-BooleanBuilder2 사용")
    void dynamicBooleanBuilder2Test()
    {
        // 동적쿼리 파라미터(가정)
        String usernameParam = null;
        Integer ageParam = null;

        List<Member> result = searchMember3(usernameParam, ageParam);
        Assertions.assertThat(result.size()).isEqualTo(1);

    }

    private List<Member> searchMember3(String usernameCond, Integer ageCond)
    {
        List<Member> result = queryFactory
                .select(member)
                .from(member)
                .where( usernameEqB(usernameCond)
                        .and(ageEqB(ageCond)) )
                .fetch();

        return result;
    }

    private BooleanBuilder usernameEqB(String usernameCond) {
        BooleanBuilder builder = new BooleanBuilder();
        if ( usernameCond != null ) builder.and(member.username.eq(usernameCond));
        return builder;
    }

    private BooleanBuilder ageEqB(Integer ageCond) {
        BooleanBuilder builder = new BooleanBuilder();
        if ( ageCond != null ) builder.and(member.age.eq(ageCond));
        return builder;
    }

    private BooleanBuilder allEqB(String usernameCond, Integer ageCond)
    {
        return  usernameEqB(usernameCond)
                .and(ageEqB(ageCond));
    }




    @Test
    @DisplayName("동적쿼리-Where 다중 파라미터 사용")
    void dynamicWhereParamTest()
    {
        // 동적쿼리 파라미터(가정)
        String usernameParam = "member1";
        Integer ageParam = 10;

        List<Member> result = searchMember2(usernameParam, ageParam);
        Assertions.assertThat(result.size()).isEqualTo(1);

    }

    private List<Member> searchMember2(String usernameCond, Integer ageCond)
    {
        List<Member> result = queryFactory
                .select(member)
                .from(member)
                .where(
                        usernameEq(usernameCond),
                        ageEq(ageCond)
                )
                .fetch();

        return result;
    }

    private BooleanExpression usernameEq(String usernameCond) {
        return usernameCond != null ? member.username.eq(usernameCond) : null;
    }

    private BooleanExpression ageEq(Integer ageCond) {
        return ageCond != null ? member.age.eq(ageCond) : null;
    }

    private BooleanExpression allEq(String usernameCond, Integer ageCond){
        return usernameEq(usernameCond).and(ageEq(ageCond));
    }


    @Test
    @DisplayName("쿼리 한번으로 대량 데이터 수정")
    void updateBulkTest()
    {
        long affected = queryFactory
                .update(member)
                    .set(member.username, "비회원")
                    .set(member.age, member.age.add(1))
                .where(member.age.gt(10))
                .execute();

        // 벌크 연산 후, 영속성 컨텍스트 초기화 필요
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("쿼리 한번으로 대량 데이터 수정2")
    void updateBulk2Test()
    {
        long affected = queryFactory
                .update(member)
                .set(
                        Arrays.asList(
                                member.username,
                                member.age
                        ),
                        Arrays.asList(
                                "비회원",
                                50
                        )
                )
                .where(member.age.gt(10))
                .execute();

    }

    @Test
    @DisplayName("쿼리 한번으로 대량 데이터 삭제")
    void deleteBulkTest()
    {
        long affected = queryFactory
                .delete(member)
                .where(member.age.gt(10))
                .execute();

        System.out.println("affected = " + affected);
        // 벌크 연산 후, 영속성 컨텍스트 초기화 필요
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("SQL function 호출하기")
    void sqlFunctionTest()
    {
        List<String> fetch = queryFactory
                .select(
                        Expressions.stringTemplate("function('replace',{0}, {1}, {2})"
                                , member.username
                                , "member"
                                , "M")
                )
                .from(member)
                .fetch();

        for (String s : fetch) {
            System.out.println("s = " + s);
        }
    }


    @Test
    @DisplayName("SQL function 호출하기")
    void sqlFunction2Test()
    {
        List<String> fetch = queryFactory
                .select(
                        member.username
                )
                .from(member)
                .where(member.username.eq(member.username.lower()))
                .fetch();

        for (String s : fetch) {
            System.out.println("s = " + s);
        }
    }
}
