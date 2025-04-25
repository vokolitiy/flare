package eu.flare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@Configuration
@EntityScan(basePackages = {"eu.flare"})
@EnableJpaRepositories(basePackages = {"eu.flare"})
@ComponentScan(basePackages = {"eu.flare"})
public class Main {
    public static void main(String[] args) {
        try {
            SpringApplication.run(Main.class, args);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}