package ru.pchelicam;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.sql.SQLException;

//@SpringBootTest
public class SpringBootConnectionTest {

    @Autowired
    private DataSource dataSource;

//    @Test
//    public void givenTomcatConnectionPoolInstance_whenCheckedPoolClassName_thenCorrect() throws SQLException {
//        Assertions.assertThat(dataSource.getConnection().getClientInfo())
//                .isEqualTo("org.apache.tomcat.jdbc.pool.DataSource");
//    }
}