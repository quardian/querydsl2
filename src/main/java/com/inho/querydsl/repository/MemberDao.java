package com.inho.querydsl.repository;

import com.inho.querydsl.web.dto.MemberDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Mapper
public interface MemberDao {

    @Transactional(readOnly = true)
    MemberDto findById(Long id);

    List<MemberDto> findAll();

    int insert(MemberDto member);
}
