package ru.trustsoft;

import com.sun.xml.internal.bind.v2.TODO;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    /*
    TODO 1) archives of bases
    TODO 2) send act at e-mail
    TODO 3) datapicker
    TODO 4) pagination
    TODO 5) buttons in every row of tables
    TODO 6) favicon
    TODO 7) formatting text and tables

    */

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {

/*
            System.out.println("Let's inspect the beans provided by Spring Boot:");

            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            for (String beanName : beanNames) {
                System.out.println(beanName);
            }
*/

        };
    }

}
