# SpringBoot Bean Management  

## SpringBoot에서 빈을 관리하는 방법  

SpringBoot에서는 크게 2가지 방법으로 Bean을 등록합니다.  

### @Component
첫 번째는 Bean으로 등록할 클래스에 @Component Annotation을 붙여줍니다.  
__@Component는 개발자가 작성한 클래스를 명시적으로 "이 클래스는 Bean으로 등록하겠다." 라는 의미입니다.__  
SpringContainer가 기동되면 Bean으로 등록할 클래스를 탐색하게되는데, @Component가 붙여진 클래스를 발견하면 해당 클래스를 Bean으로 등록하니다.  
자동으로 되는건 아니고 Application 내에 @ComponentScan이 붙여진 클래스가 있을 때만 가능합니다.  
이름에서 볼 수 있듯이 @ComponentScan은 @Component를 탐색하는 역할을 수행하는데, 탐색 기준은 Annotation이 붙여진 클래스의 패키지부터 그 하위 패키지들 모두 입니다.  
옵션을 주어 특정 패키지나 클래스를 탐색범위에 추가할 수도 있고, 제외시킬 수도 있습니다.  
예를들어 아래와 같은 패키지 구조를 가지고 있다고 가정하겠습니다.  
![Alt](/images/2021-06/springboot-bean-componentscan-package.png)  

HelloApplication은 @SpringBootApplication이 붙여진 메인 클래스입니다.  
@SpringBootApplication은 @ComponentScan을 포함하고 있는데 @ComponentScan의 옵션은 별도로 설정하지 않았습니다.  
따라서 hello 패키지와 그 하위인 hello.web 패키지에 대해서 탐색을 수행하며 hello.web 패키지에 있는 HelloController가 @Component가 선언되어 있다면 Bean으로 등록합니다.  
아래는 HelloController 클래스입니다.  
![Alt](/images/2021-06/springboot-bean-restcontroller.png)  

클래스에 @Component가 붙여있지 않은데 그럼 이 클래스는 Bean으로 등록되지 않는걸까요?  
확인을 위해 애플리케이션이 실행된 후 탐색된 빈을 출력해보겠습니다.  
먼저 HelloApplication을 아래와 같이 Bean을 출력할 수 있도록 구현해보았습니다.  
```java
@SpringBootApplication
public class HelloApplication {
    public static void main(String[] args) {
        // 웹 서버를 띄우고 싶다면 아래와 같이 기본값으로 실행
        //ApplicationContext context = SpringApplication.run(HelloApplication.class, args);

        // 웹 서버는 띄우지 않겠다면 아래와 같이 옵션을 설정하여 실행
        SpringApplication application = new SpringApplication(HelloApplication.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        ApplicationContext context = application.run(args);
        printRegisteredBeans(context);
    }

    private static void printRegisteredBeans(ApplicationContext context) {
        for (String beanName : context.getBeanDefinitionNames()) {
            System.out.println("name: " + beanName);
        }
    }
}
```  
편의상 웹 서버를 안띄우는 옵션을 넣었습니다.  
웹 서버가 실행이 안된다고 Bean이 등록되지 않는건 아니기때문에 테스트에는 영향없기 때문입니다.  
아래는 printRegisteredBeans 메서드에서 출력된 Bean들의 이름입니다.  
![Alt](/images/2021-06/springboot-bean-print-allbeans.png)  
정상적으로 Bean으로 등록되었음을 확인할 수 있습니다.  

분명 HelloController에서는 @Component가 없었는데 어떻게 Bean으로 등록될 수 있었을까요?  
그 이유는 Annotation은 @RestController는 @Controller를 선언하여 포함하고있으며, @Controller는 다시 @Component를 포함하고 있기 때문입니다.  
Annotation에 다른 Annotation을 추가하면 해당 Annotation을 상속받은 것과 동일한 기능이 수행됩니다.  

<hr>

### @Bean  
두번째는 @Bean Annotation을 선언함으로써 Bean을 등록할 수 있습니다.  
단, @Bean은 단독으로는 사용될 수 없으며 설정 클래스 안에서 선언된 @Bean들만 정상적으로 SpringContainer에 등록됩니다.  
이전에 Profile 정리자료에서 사용한 LocalConfiguration도 @Bean을 사용한 대표적인 예입니다.  
설정 클래스 예제 코드를 작성해보겠습니다.
```java
@Configuration
public class AppConfig {
    @Bean
    public String intro() {
        return "intro";
    }
}
``` 

AppConfig 클래스는 @Configuration이 선언되어 있으므로 SpringBoot에 의해 설정 클래스로 등록됩니다.  
그리고 내부에 있는 intro는 @Bean이 선언되어 있으므로 정상적으로 Bean으로 등록됩니다.  
Bean 출력 결과 메인 클래스, 설정 클래스, 컨트롤러 클래스, intro까지 4개의 Bean이 등록되었습니다.  
(편의상 Bean 이름을 정렬하였습니다.)
![Alt](/images/2021-06/springboot-bean-print-use-config.png)  

만약 AppConfig 클래스에서 @Configuration없이 @Bean만 사용한다면 어떻게될까요?  
__위 코드에서 @Configuration을 주석처리한다면 AppConfig는 더이상 설정 클래스도 아니며 Component도 아니기 때문에 Bean으로 등록되지 않습니다.__  
따라서 intro 역시 Bean으로 등록되지 않습니다.  
하지만 메인 클래스에서 AppConfig를 설정 클래스로 추가하면 @Configuration없이도 Bean으로 등록은 가능합니다.  
```java
@SpringBootApplication
public class HelloApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(HelloApplication.class, AppConfig.class);
        // ... 생략
    }
}
```  

__하지만 이 경우 한가지 문제점이 생깁니다.__  
Spring Bean에는 Scope가 존재하며 기본적으로 SpringContainer는 singleton scope를 사용하여 Bean을 관리합니다.  
@Configuration 없이 일반 클래스를 위와같이 Bean으로 등록할 경우 @Bean으로 선언한 클래스들은 singleton scope를 보장받지 못합니다.  
scope 내용은 이번 정리에서 내용을 많이 벗어날 것 같으니 다음번에 조금 더 깊게 정리하도록 하겠습니다.  

<hr>

### 그래서 결론은?  

__SpringBoot를 사용해서 Bean으로 등록하는 방법은 @Component를 선언하는 것과, @Configuration + @Bean 조합으로 선언하는 방법이 있습니다.__  
개발자가 직접 구현한 클래스는 @Component를 명시적으로 선언함으로써 쉽게 Bean으로 생성할 수 있습니다.  
하지만 외부 라이브러리에서 작성된 클래스는 @Component를 붙일 수 없으므로 이런 경우 @Bean을 사용하여 등록할 수 있습니다.  

<hr>

### 주의!  
SpringBoot 2버전 이상에서는 기본적으로 Bean의 중복 등록을 불허합니다.  
(~~정확히 몇버전인지는 기억안납니다. 이후 찾아보고 문서 수정하겠습니다.~~)   
만약 하나의 클래스를 @Component와 @Bean을 사용해서 등록하려 한다면 SpringBoot는 애플리케이션 구동을 강제로 실패시킵니다.  
Property에 spring.main.allow-bean-definition-overriding=true 옵션을 추가하면 강제로 Overriding 시키므로 구동은 가능하지만 의도치 않은 Bean이 생성될 수 있습니다.   
