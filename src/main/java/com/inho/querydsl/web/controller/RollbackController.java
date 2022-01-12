package com.inho.querydsl.web.controller;

import com.inho.querydsl.entity.Member;
import com.inho.querydsl.repository.MemberRepository;
import com.inho.querydsl.service.MasterService;
import com.inho.querydsl.service.SlaveService;
import com.sun.istack.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RollbackController {
    private final MemberRepository memberRepository;
    private final MasterService masterService;
    private final SlaveService slaveService;

    @PostMapping("/jpa/rollback")
    public ResponseEntity<Member> checkJpaTransactionRollback(@RequestParam @NotNull String username)
    {
        log.info("REQUEST_URL = /jpa/rollback 호출");

        Member member = new Member(username, 10);
        memberRepository.save(member);

        throw new RuntimeException("Rollback");
    }



    @PostMapping("/jpa")
    public ResponseEntity<Member> checkJpaTransaction(@RequestParam @NotNull String username)
    {
        log.info("REQUEST_URL = /jpa 호출");

        Member member = new Member(username, 10);
        memberRepository.save(member);
        ResponseEntity<Member> body = ResponseEntity.ok().body(member);
        return body;
    }



    @GetMapping("/master")
    public void master()
    {
        log.info("REQUEST_URL = /master 호출");
        masterService.insert();
    }

    @GetMapping("/slave")
    public void slave()
    {
        log.info("REQUEST_URL = /slave 호출");
        slaveService.select();
    }
}
