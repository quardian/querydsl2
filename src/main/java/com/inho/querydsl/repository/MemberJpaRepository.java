package com.inho.querydsl.repository;

import com.inho.querydsl.entity.Member;
import com.inho.querydsl.entity.QMember;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

import static com.inho.querydsl.entity.QMember.*;

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
}
