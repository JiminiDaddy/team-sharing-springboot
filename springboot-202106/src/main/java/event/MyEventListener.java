package event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Created by Choen-hee Park
 * User : chpark
 * Date : 2021/05/10
 * Time : 4:08 PM
 */

@Component
public class MyEventListener {
    @EventListener
    public void onEvent(SendMessageEvent event) {
        System.out.println("MyEventListener.onEvent, event-name: " + event.getName());
    }
}
