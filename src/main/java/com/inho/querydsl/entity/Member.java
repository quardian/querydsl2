package com.inho.querydsl.entity;

import lombok.*;

import javax.persistence.*;

@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"team"})
@Entity
public class Member {
    @Id @GeneratedValue
    @Column(name="member_id")
    private Long id;

    private String username;

    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="team_id")
    Team team;

    /**
     * 편의 메소드
     * @param team
     */
    public void changeTeam(Team team)
    {
        if ( this.getTeam() != null && this.getTeam().getMembers() != null ){
            this.getTeam().getMembers().remove(this);
        }
        this.team = team;
        if ( team != null && team.getMembers() != null ){
            team.getMembers().add(this);
        }
    }

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        this.changeTeam(team);
    }

    public Member(String username, int age) {
       this(username,age,null);
    }

    public Member(String username) {
        this(username,0,null);
    }
}
