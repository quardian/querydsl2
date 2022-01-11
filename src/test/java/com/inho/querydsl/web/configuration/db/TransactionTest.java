package com.inho.querydsl.web.configuration.db;

import com.inho.querydsl.entity.Member;
import com.inho.querydsl.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class TransactionTest {



    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MockMvc mockMvc;

    private static final String Test_User_Name = "leeinho";

    @Test
    @DisplayName("트랙샌션 테스트")
    void transactionTest() throws Exception {

        MvcResult mvcResult = mockMvc.perform(post("/jpa")
                        .param("username", Test_User_Name)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();



        List<Member> members = memberRepository.findByUsername(Test_User_Name);

        for (Member member : members) {
            System.out.println("member = " + member);
            assertThat(member.getUsername()).isEqualTo(Test_User_Name);
        }
    }


    @Test
    @DisplayName("트랙샌션 Rollback 테스트")
    void transactionRollbackTest() throws Exception {

        try{

            MvcResult mvcResult = mockMvc.perform(post("/jpa/rollback")
                            .param("username", Test_User_Name)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andReturn();
        }catch (Exception e){
            log.error("{}", e.toString());
        }

        List<Member> members = memberRepository.findByUsername(Test_User_Name);
        assertThat(members.size()).isEqualTo(0);
    }

}