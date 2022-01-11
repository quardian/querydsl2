package com.inho.querydsl.repository;

import com.inho.querydsl.web.dto.MemberDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MemberDao {

    MemberDto findById(Long id);

    List<MemberDto> findAll();

    int insert(MemberDto member);
}
