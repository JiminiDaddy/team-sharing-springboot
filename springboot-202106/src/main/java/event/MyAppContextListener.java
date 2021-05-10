package event;

import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Created by Choen-hee Park
 * User : chpark
 * Date : 2021/04/29
 * Time : 4:36 PM
 */

@Component
public class MyAppContextListener {
    @EventListener
    public void onApplicationEvent(ApplicationContextEvent applicationContextEvent) {
        System.out.println("MyAppContextListener.onApplicationEvent");
    }
}
