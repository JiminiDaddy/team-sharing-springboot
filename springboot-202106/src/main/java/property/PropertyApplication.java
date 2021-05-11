package property;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by Choen-hee Park
 * User : chpark
 * Date : 2021/05/11
 * Time : 10:17 AM
 */

@SpringBootApplication
public class PropertyApplication implements ApplicationRunner {
    private final AuthorProperties authorProperties;

    public PropertyApplication(AuthorProperties authorProperties) {
       this.authorProperties = authorProperties;
    }

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(PropertyApplication.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        application.run(args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("AuthorProperties: " + authorProperties.toString());
    }
}
