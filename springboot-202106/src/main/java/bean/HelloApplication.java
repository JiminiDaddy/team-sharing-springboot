package bean;

import bean.config.AppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.Arrays;

/**
 * Created by Choen-hee Park
 * User : chpark
 * Date : 2021/04/27
 * Time : 4:17 PM
 */

@SpringBootApplication
public class HelloApplication {
    public static void main(String[] args) {
        // 웹 서버를 띄우고 싶다면 아래와 같이 기본값으로 실행
        //ApplicationContext context = SpringApplication.run(HelloApplication.class, args);

        // 웹 서버는 띄우지 않겠다면 아래와 같이 옵션을 설정하여 실행
        SpringApplication application = new SpringApplication(HelloApplication.class, AppConfig.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        ApplicationContext context = application.run(args);
        printRegisteredBeans(context);
    }

    private static void printRegisteredBeans(ApplicationContext context) {
        String[] beanNames = context.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            System.out.println("name: " + beanName);
        }
    }
}
