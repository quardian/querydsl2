package com.inho.querydsl.repository;

import com.inho.querydsl.entity.Member;
import com.inho.querydsl.web.dto.MemberSearchCondition;
import com.inho.querydsl.web.dto.MemberTeamDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepositoryCustom {
    Optional<Member> findById_Querydsl(Long id);
    List<Member> findAll_Querydsl();
    List<Member> findByUsername_Querydsl(String username);
    List<MemberTeamDto> searchByBuilder(MemberSearchCondition dto);
    List<MemberTeamDto> searchByWhereParam(MemberSearchCondition dto);

    Page<MemberTeamDto> searchPageSimple(MemberSearchCondition dto, Pageable pageable);
    Page<MemberTeamDto> searchPageComplex(MemberSearchCondition dto, Pageable pageable);
}
