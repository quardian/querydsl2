package com.inho.querydsl.repository;

import com.inho.querydsl.entity.Member;
import com.inho.querydsl.entity.Team;
import com.inho.querydsl.web.dto.MemberSearchCondition;
import com.inho.querydsl.web.dto.MemberTeamDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class MemberRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;


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
    void basic1Test()
    {
        Member member = new Member("member1", 10);
        memberRepository.save(member);

        Member findMember = memberRepository.findById(member.getId()).get();
        assertThat(findMember).isEqualTo(member);

        List<Member> members = memberRepository.findAll();
        assertThat(members).containsExactly(member);

        List<Member> member1 = memberRepository.findByUsername("member1");
        assertThat(member1).isEqualTo(member);
    }


    @Test
    @DisplayName("실무활용 - Spring DATA JPA와 Querydsl")
    void pureJapAndQuerydslTest()
    {
        MemberSearchCondition dto = new MemberSearchCondition();
        dto.setUsername("member1");
        dto.setAgeGoe(10);
        dto.setAgeLoe(30);

        List<MemberTeamDto> memberTeamDtos = memberRepository.searchByBuilder(dto);
        for (MemberTeamDto memberTeamDto : memberTeamDtos) {
            System.out.println("memberTeamDto = " + memberTeamDto);
        }
    }



    @Test
    @DisplayName("페이징 테스트")
    void pagingSimpleTest()
    {
        MemberSearchCondition dto = new MemberSearchCondition();

        PageRequest pagable = PageRequest.of(0, 3);

        //Page<MemberTeamDto> page = memberRepository.searchPageSimple(dto, pagable);
        Page<MemberTeamDto> page = memberRepository.searchPageComplex(dto, pagable);
        List<MemberTeamDto> content = page.getContent();
        System.out.println("totalElements = " + page.getTotalElements());
        System.out.println("totalPages = " + page.getTotalPages());
        System.out.println("number = " + page.getNumber());
        for (MemberTeamDto memberTeamDto : content) {
            System.out.println("memberTeamDto = " + memberTeamDto);
        }
    }
}