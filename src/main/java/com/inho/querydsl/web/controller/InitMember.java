package com.inho.querydsl.web.controller;

import com.inho.querydsl.entity.Member;
import com.inho.querydsl.entity.Team;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Profile("local")
@Component
@RequiredArgsConstructor
public class InitMember {

    private final InitMemberService initMemberService;

    @PostConstruct
    public void init()
    {
        /** PostContruct 와 Transactional은 분리를 해줘야 해서 따러 정의해야함. */
        initMemberService.init();
    }

    @Component
    static class InitMemberService {
        @PersistenceContext
        EntityManager em;

        @Transactional
        public void init(){
            Team teamA = new Team("teamA");
            Team teamB = new Team("teamB");
            em.persist(teamA);
            em.persist(teamB);

            for(int i=0; i<100; i++){
                Team team = (i % 2) == 0 ? teamA : teamB;
                em.persist( new Member("member"+i, i+1, team) ) ;
            }

        }
    }
}
