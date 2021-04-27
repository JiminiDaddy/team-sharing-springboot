package hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * Created by Choen-hee Park
 * User : chpark
 * Date : 2021/04/27
 * Time : 4:17 PM
 */

@SpringBootApplication
public class HelloApplication {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(HelloApplication.class, args);
    }
}
