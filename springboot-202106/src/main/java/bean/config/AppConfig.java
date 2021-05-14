package bean.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Choen-hee Park
 * User : chpark
 * Date : 2021/05/14
 * Time : 10:31 AM
 */

@Configuration
public class AppConfig {
    @Bean
    public String intro() {
        return "intro";
    }
}
