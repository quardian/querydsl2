package com.inho.querydsl.web.controller;

import com.inho.querydsl.repository.MemberJpaRepository;
import com.inho.querydsl.repository.MemberRepository;
import com.inho.querydsl.web.dto.MemberSearchCondition;
import com.inho.querydsl.web.dto.MemberTeamDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberJpaRepository memberJpaRepository;
    private final MemberRepository memberRepository;

    @GetMapping("/v1/members")
    public List<MemberTeamDto> searchMemberV1(MemberSearchCondition condition,
                                              HttpServletRequest request)
    {
        log.info("requestURI={}, condition={}",request.getRequestURI(), condition);
        return memberJpaRepository.searchByBuilder(condition);
    }


    @GetMapping("/v2/members")
    public Page<MemberTeamDto> searchMemberV2(MemberSearchCondition condition,
                                              Pageable pageable,
                                              HttpServletRequest request)
    {
        log.info("requestURI={}, condition={}",request.getRequestURI(), condition);
        return memberRepository.searchPageSimple(condition, pageable);
    }

    @GetMapping("/v3/members")
    public Page<MemberTeamDto> searchMemberV3(MemberSearchCondition condition,
                                              Pageable pageable,
                                              HttpServletRequest request)
    {
        log.info("requestURI={}, condition={}",request.getRequestURI(), condition);
        return memberRepository.searchPageComplex(condition, pageable);
    }


}
