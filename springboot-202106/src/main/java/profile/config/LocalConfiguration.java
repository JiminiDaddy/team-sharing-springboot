package profile.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Created by Choen-hee Park
 * User : chpark
 * Date : 2021/05/11
 * Time : 11:04 AM
 */

@Profile("local")
@Configuration
public class LocalConfiguration {
    @Bean
    public String intro() {
        return "This is local-configuration";
    }
}
