package com.inho.querydsl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TestRunner implements ApplicationRunner {

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Connection connection = dataSource.getConnection();
        log.info("dataSource:{}", dataSource);
        log.info("url : {}" , connection.getMetaData().getURL());
        log.info("username : {} ",  connection.getMetaData().getUserName());

        log.info("jdbcTemplate:{}", jdbcTemplate);
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("select * from member");
        log.info("rows.size={}", rows.size());
        for (Map<String, Object> row : rows) {
            System.out.println("row = " + row);
        }

    }
}
