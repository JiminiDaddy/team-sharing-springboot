package profile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import profile.config.ServerInfo;

/**
 * Created by Choen-hee Park
 * User : chpark
 * Date : 2021/05/11
 * Time : 11:16 AM
 */

@SpringBootApplication
public class ProfileApplication implements ApplicationRunner {
    private final ServerInfo serverInfo;

    private final String intro;

    @Autowired
    public ProfileApplication(ServerInfo serverInfo, String intro) {
        this.serverInfo = serverInfo;
        this.intro = intro;
    }

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(ProfileApplication.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        application.run();
    }

    @Override
    public void run(ApplicationArguments args) {
        System.out.println(intro);
        System.out.println(serverInfo.toString());
    }
}
