package com.inho.querydsl.repository;

import antlr.collections.impl.BitSet;
import com.inho.querydsl.entity.Member;
import com.inho.querydsl.entity.QMember;
import com.inho.querydsl.entity.QTeam;
import com.inho.querydsl.web.dto.MemberSearchCondition;
import com.inho.querydsl.web.dto.MemberTeamDto;
import com.inho.querydsl.web.dto.QMemberTeamDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

import static com.inho.querydsl.entity.QMember.*;
import static com.inho.querydsl.entity.QTeam.*;

@Repository
@RequiredArgsConstructor
public class MemberJpaRepository
{
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public void save(Member member){
        em.persist(member);
    }

    public Optional<Member> findById(Long id){
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    public Optional<Member> findById_Querydsl(Long id){
        Member member = queryFactory
                .selectFrom(QMember.member)
                .where(QMember.member.id.eq(id))
                .fetchOne();
        return Optional.ofNullable(member);
    }


    public List<Member> findAll()
    {
        String qlString = "select m from Member m";
        return em.createQuery(qlString, Member.class)
                .getResultList();
    }

    public List<Member> findAll_Querydsl()
    {
        return queryFactory
                .selectFrom(member)
                .fetch();
    }

    public List<Member> findByUsername(String username)
    {
        String qlString = "select m from Member m where m.username = :username";
        return em.createQuery(qlString, Member.class)
                .setParameter("username", username)
                .getResultList();
    }

    public List<Member> findByUsername_Querydsl(String username)
    {
        return queryFactory
                .selectFrom(member)
                .where(member.username.eq(username))
                .fetch();
    }

    public List<MemberTeamDto> searchByBuilder(MemberSearchCondition dto)
    {
        BooleanBuilder whereBuilder = whereBuilder(dto);

        List<MemberTeamDto> members = queryFactory
                .select(
                        new QMemberTeamDto(
                                member.id.as("memberId"),
                                member.username,
                                member.age,
                                team.id.as("teamId"),
                                team.name.as("teamName")
                        )
                )
                .from(member)
                .join(member.team, team)
                .where(whereBuilder)
                .fetch();

        List<MemberTeamDto> members1 = queryFactory
                .select(
                        Projections.constructor( MemberTeamDto.class,
                                member.id.as("memberId"),
                                member.username,
                                member.age,
                                team.id.as("teamId"),
                                team.name.as("teamName")
                        )
                )
                .from(member)
                .join(member.team, team)
                .where( whereBuilder )
                .fetch();

        return members;
    }

    public List<MemberTeamDto> searchByWhereParam(MemberSearchCondition dto)
    {
        List<MemberTeamDto> members = queryFactory
                .select(
                        new QMemberTeamDto(
                                member.id.as("memberId"),
                                member.username,
                                member.age,
                                team.id.as("teamId"),
                                team.name.as("teamName")
                        )
                )
                .from(member)
                .leftJoin(member.team, team)
                .where()
                .fetch();

        List<MemberTeamDto> members1 = queryFactory
                .select(
                        Projections.constructor( MemberTeamDto.class,
                                member.id.as("memberId"),
                                member.username,
                                member.age,
                                team.id.as("teamId"),
                                team.name.as("teamName")
                        )
                )
                .from(member)
                .leftJoin(member.team, team)
                .where(
                        usernameEqB( dto.getUsername() ),
                        teamNameEqB( dto.getTeamName() ),
                        ageGoeB( dto.getAgeGoe() ),
                        ageLoeB( dto.getAgeLoe() )
                )
                .fetch();

        return members;
    }


    private BooleanBuilder whereBuilder(MemberSearchCondition dto)
    {
        return usernameEq(dto.getUsername())
                .and( teamNameEq(dto.getTeamName()) )
                .and( ageGoe(dto.getAgeGoe()) )
                .and( ageLoe(dto.getAgeLoe() ) );
    }

    private BooleanBuilder usernameEq(String username) {
        BooleanBuilder builder = new BooleanBuilder();
        return ( StringUtils.hasText(username) ) ? builder.and(member.username.eq(username)) : builder;
    }

    private BooleanBuilder teamNameEq(String teamName) {
        BooleanBuilder builder = new BooleanBuilder();
        return ( StringUtils.hasText(teamName) ) ? builder.and(team.name.eq(teamName)) : builder;
    }

    private BooleanBuilder ageGoe(Integer ageGoe) {
        BooleanBuilder builder = new BooleanBuilder();
        return (ageGoe != null) ? builder.and(member.age.goe(ageGoe)) : builder;
    }

    private BooleanBuilder ageLoe(Integer ageLoe) {
        BooleanBuilder builder = new BooleanBuilder();
        return (ageLoe != null) ? builder.and(member.age.loe(ageLoe)) : builder;
    }


    private BooleanExpression usernameEqB(String username){
        return (StringUtils.hasText(username)) ? member.username.eq(username) : null;
    }

    private BooleanExpression teamNameEqB(String teamName){
        return (StringUtils.hasText(teamName)) ? team.name.eq(teamName) : null;
    }

    private BooleanExpression ageGoeB(Integer ageGoe){
        return (ageGoe != null) ? member.age.goe(ageGoe) : null;
    }

    private BooleanExpression ageLoeB(Integer ageLoe){
        return (ageLoe != null) ? member.age.loe(
                ageLoe) : null;
    }

}
