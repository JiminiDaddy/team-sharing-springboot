package event;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;

/**
 * Created by Choen-hee Park
 * User : chpark
 * Date : 2021/04/29
 * Time : 4:29 PM
 */

@SpringBootApplication
public class EventApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(EventApplication.class);
        app.addListeners(new MyAppStartedListener());
        app.run(args);
        printListeners(app);
    }

    static void printListeners(SpringApplication app) {
        for (ApplicationListener listener : app.getListeners()) {
            System.out.println(listener.getClass().getSimpleName());
        }
    }
}
