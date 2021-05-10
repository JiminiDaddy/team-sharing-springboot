package event.web;

import event.SendMessageEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Choen-hee Park
 * User : chpark
 * Date : 2021/05/10
 * Time : 4:14 PM
 */

@RestController
public class EventController {
    private final ApplicationEventPublisher publisher;

    public EventController(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @PostMapping("/event")
    public String event(@RequestBody SendMessageEvent event) {
        publisher.publishEvent(event);
        return event.getName();
    }
}
