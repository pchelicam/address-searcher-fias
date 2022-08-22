package ru.pchelicam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.pchelicam.services.XmlParserManager;

import java.time.Duration;
import java.time.Instant;

@SpringBootApplication
//@EnableTransactionManagement
public class Application implements CommandLineRunner {

    @Autowired
    private XmlParserManager xmlParserManager;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Instant start = Instant.now();
        xmlParserManager.manageDataInsert();

        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);

        System.out.println("Execution time: " + String.format("%d:%02d:%02d", duration.getSeconds() / 3600, (duration.getSeconds() % 3600) / 60, (duration.getSeconds() % 60)));
    }

//    @Bean
//    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory){
//        JpaTransactionManager transactionManager = new JpaTransactionManager();
//        transactionManager.setEntityManagerFactory(entityManagerFactory);
//        return transactionManager;
//    }

}
