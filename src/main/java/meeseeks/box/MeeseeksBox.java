package meeseeks.box;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */

@SpringBootApplication
@EnableAutoConfiguration
public class MeeseeksBox {

    public static void main(String[] args) {
        SpringApplication.run(MeeseeksBox.class, args);
    }
}
