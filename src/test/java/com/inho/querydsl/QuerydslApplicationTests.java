package com.inho.querydsl;

import com.inho.querydsl.entity.Hello;
import com.inho.querydsl.entity.QHello;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@SpringBootTest
class QuerydslApplicationTests {

    @PersistenceContext
    private EntityManager em;

    @Test
    void contextLoads() {

    }

    @Test
    @Transactional
    @Rollback(value = false)
    void querydslSettings()
    {
        Hello hello = new Hello();
        em.persist(hello);

        JPAQueryFactory query = new JPAQueryFactory(em);
        QHello qhello = new QHello("h");

        Hello result = query.selectFrom(qhello)
                .fetchOne();

        Assertions.assertThat(result).isEqualTo(hello);
    }
}
