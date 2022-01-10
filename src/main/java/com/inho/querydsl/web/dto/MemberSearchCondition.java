package com.inho.querydsl.web.dto;

import lombok.Data;

/**
 * 검색조건
 */
@Data
public class MemberSearchCondition
{
    // 회원명, 팀명, 나이(ageGoe, ageLoe)
    private String username;
    private String teamName;
    private Integer ageGoe;
    private Integer ageLoe;
}
