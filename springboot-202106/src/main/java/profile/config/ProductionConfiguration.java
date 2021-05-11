package profile.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Created by Choen-hee Park
 * User : chpark
 * Date : 2021/05/11
 * Time : 11:03 AM
 */

@Profile("prod")
@Configuration
public class ProductionConfiguration {
    @Value("${author.name}")
    private String authorName;
    @Value("${author.age}")
    private Integer authorAge;

    @Bean
    public String intro() {
        StringBuilder sb = new StringBuilder();
        sb.append("This is production-configuration, name:").append(authorName).append(" age:").append(authorAge);
        return sb.toString();
    }

}
