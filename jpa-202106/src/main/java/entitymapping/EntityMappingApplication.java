package entitymapping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Arrays;

@EnableJpaAuditing
@SpringBootApplication
public class EntityMappingApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(EntityMappingApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        ApplicationContext context = springApplication.run(args);

        String[] beans = context.getBeanDefinitionNames();
        Arrays.sort(beans);
        Arrays.stream(beans).forEach(System.out::println);

        EntityMappingApplication application = context.getBean(EntityMappingApplication.class);
    }
}
