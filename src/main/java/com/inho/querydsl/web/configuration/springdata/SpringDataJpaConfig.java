package com.inho.querydsl.web.configuration.springdata;


import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;
import java.util.UUID;

@Configuration
@EnableJpaAuditing(modifyOnCreate = true)
// Springboot 에서는 아래 설정 안해도, Application 하위에서 알아서 찾아줌.
@EnableJpaRepositories(basePackages = {"com.inho.querydsl.web.repository"})
public class SpringDataJpaConfig {

    @PersistenceContext
    private EntityManager em;

    /**
     * Spring Data JPA
     * @return
     */
    @Bean
    public AuditorAware<String> auditorProvider()
    {
        // 실제는 : 세션정보에서 ID 를 갖어와서 반환
        return () -> Optional.of( UUID.randomUUID().toString() );
    }

    /**
     * JPAQueryFactory 빈 등록
     * @return
     */
    @Bean
    public JPAQueryFactory jpaQueryFactory(){
        return new JPAQueryFactory((this.em));
    }

}
