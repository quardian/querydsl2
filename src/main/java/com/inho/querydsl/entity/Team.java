package com.inho.querydsl.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"members"})
@Entity
public class Team {
    @Id @GeneratedValue
    @Column(name="team_id")
    private Long id;

    private String name;

    @OneToMany(mappedBy = "team")
    private List<Member> members = new ArrayList<>();

    public Team(String name) {
        this.name = name;
    }
}
