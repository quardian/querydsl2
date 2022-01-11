package com.inho.querydsl.repository;

import com.inho.querydsl.entity.Member;
import com.inho.querydsl.entity.QMember;
import com.inho.querydsl.entity.QTeam;
import com.inho.querydsl.repository.support.Querydsl4RepositorySupport;
import com.inho.querydsl.web.dto.MemberSearchCondition;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.inho.querydsl.entity.QMember.member;
import static com.inho.querydsl.entity.QTeam.team;

@Repository
public class MemberTestRepository extends Querydsl4RepositorySupport
{
    public MemberTestRepository() {
        super(Member.class);
    }

    public List<Member> basicSelect(){
        return select(member)
                .from(member)
                .fetch();
    }


    public List<Member> basicSelectFrom(){
        return selectFrom(member)
                .fetch();
    }

    public Page<Member> searchPageByApplyPage(MemberSearchCondition condition, Pageable pageable)
    {
        BooleanBuilder whereBuilder = whereBuilder(condition);
        JPAQuery<Member> query = select(member)
                .from(member)
                .where(whereBuilder);
        List<Member> content = getQuerydsl().applyPagination(pageable, query).fetch();
        return PageableExecutionUtils.getPage(content,pageable,query::fetchCount);
    }

    public Page<Member> searchPaginating(MemberSearchCondition condition, Pageable pageable)
    {
        BooleanBuilder whereBuilder = whereBuilder(condition);

        return applyPagination(pageable, query -> query
                .select(member)
                .from(member)
                .join(member.team, team)
                .where(whereBuilder));
    }

    public Page<Member> searchPaginating2(MemberSearchCondition condition, Pageable pageable)
    {
        BooleanBuilder whereBuilder = whereBuilder(condition);

        return applyPagination(pageable,

                contentQuery -> contentQuery
                    .select(member)
                    .from(member)
                    .join(member.team, team)
                    .where(whereBuilder),

                countQuery -> countQuery
                        .select(member.count())
                        .from(member)
                        .join(member.team, team)
                        .where(whereBuilder)
                );
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

}
