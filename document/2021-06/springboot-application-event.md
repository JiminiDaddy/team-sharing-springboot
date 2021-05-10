## Application Event  


### Spring Application Event  
Spring이 기본적으로 제공해주는 이벤트들이 있는데 이벤트 리스너를 사용하여 이 이벤트들을 받아볼 수 있습니다.  
ApplicationContext의 getListeners() 메서드를 통해 기본적으로 등록된 이벤트 리스너들을 확인할 수 있습니다.  
또한 해당 이벤트들을 처리하기위해 이벤트 리스너를 개발자가 직접 구현할 수 있으며, ApplicationContext에 추가해주면 호출됩니다.  
이벤트 리스너를 추가하는 방법은 2가지 방법이 있습니다.  
ApplicationListener 인터페이스를 구현하는 방법과 @EventListener 애너테이션을 사용하는 방법이 있습니다.  
Spring 4.2 미만에서는 주로 인터페이스 구현을 사용했으나 이후에는 애너테이션을 많이 선호한다고 합니다.  



#### 인터페이스 구현을 통한 이벤트 리스너 구현  
ApplicationListener 인터페이스의 onApplicationEvent 메서드를 구현합니다.  
```java
public class MyAppStartedListener implements ApplicationListener<ApplicationStartedEvent> {
    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
        System.out.println("MyAppStartedListener.onApplicationEvent");
    }
}
```  


#### 애너테이션을 통한 이벤트 리스터 구현  
Spring Bean으로 등록하기위해 @Component 애너테이션을 추가합니다.  
이벤트로 사용될 메서드에 @EventListener 애너테이션을 추가하여 이벤트 리스너임을 알려줍니다.  
```java
@Component
public class MyAppContextListener {
    @EventListener
    public void onApplicationEvent(ApplicationContextEvent applicationContextEvent) {
        System.out.println("MyAppContextListener.onApplicationEvent");
    }
}
```



### Custom Application Event
Spring에서 제공해주는 이벤트 외에 개발자가 필요에 따라 이벤트를 생성할 수도 있습니다.  
예를들어 사용자의 요청을 처리한 뒤, 처리결과를 메일이나 메시지를 보내야 할 경우 이벤트를 통해 비동기로 처리하는게 가능합니다.  
동기로 처리할경우 N개의 기능을 각각 트랜잭션으로 분리되지 않으면 부분실패가 발생하면 전체가 롤백되는 현상이 발생하는 문제가 있습니다.
이벤트로 처리할경우 또 하나의 장점은 객체간의 결합도를 낮출 수 있습니다.  
만약 작업을 처리하는 Service 객체가 작업완료 후 경우에 따라 Mail, SMS, Push를 보내야 한다면 Service 객체는 Mail, SMS, Push 객체에 대해 모두 의존하게 됩니다.  
하지만 이벤트로 처리함으로써 Service 객체는 이벤트에만 의존하게되며 Mail, SMS, Push와 같은 전송수단은 이벤트 핸들러에서 처리하면되므로 OCP를 지키는데도 도움이 됩니다.  


### Custom Event Example  
아래와 같이 Controller Class를 생성합니다.  
Controller가 생성될 때 ApplicationEventPublisher를 주입받도록 생성자 주입을 사용했습니다.  
localhost:8080/event URI를 POST방식으로 요청하면 Event가 발행됩니다.  


```java
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
```  

SendMessage는 String 타입의 name이라는 필드를 가진 객체입니다.  
```java
public class SendMessageEvent {
    private String name;

    protected SendMessageEvent() {
    }

    public SendMessageEvent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
```

마지막으로 이벤트 리스너를 등록하여 이벤트가 발생할경우 처리합니다.  
```java
@Component
public class MyEventListener {
    @EventListener
    public void onEvent(SendMessageEvent event) {
        System.out.println("MyEventListener.onEvent, event-name: " + event.getName());
    }
}
```

여기까지 구현한 뒤 서버를 실행합니다.  
event package에 @SpringBootApplication이 추가된 클래스에서 실행하면 됩니다.  
톰캣이 정상적으로 올라온것이 확인된다면 PostMan을 실행시킵니다.  
(다른 툴을 사용해도 되며 테스트코드에서 MockMvc를 통해 실행해도 결과는 동일합니다. 다만 PostMan이 가시성이 좋아 API 테스트하기엔 좋다고 생각됩니다.)  
Http Header 설정에서 CONTENT-TYPE을 application/json으로 설정합니다.  
![Alt](/images/2021-06/springboot-run-eventlistener-postman.png)  


Http Body 설정에서 아래와 같이 json 메시지를 작성합니다.  
예제에서 name이란 필드를 사용하고 있으므로 동일하게 key를 name으로 설정합니다.  
![Alt](/images/2021-06/springboot-run-eventlistener-postman-body.png)  


Send 버튼을 눌러 실행하면 PostMan 결과창에 아래와 같이 메시지가 출력됩니다.  
![Alt](/images/2021-06/springboot-run-eventlistener-postman-result.png)  


서버측 로그를 확인해보면 제일 하단과 같이 Event가 정상적으로 발행되었음을 확인할 수 있습니다.  
![Alt](/images/2021-06/springboot-run-eventlistener.png)  