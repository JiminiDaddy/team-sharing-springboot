package event;

import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;

/**
 * Created by Choen-hee Park
 * User : chpark
 * Date : 2021/04/29
 * Time : 4:32 PM
 */

public class MyAppStartedListener implements ApplicationListener<ApplicationStartedEvent> {
    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
        System.out.println("MyAppStartedListener.onApplicationEvent");
    }
}
