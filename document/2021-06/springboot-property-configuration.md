# Property Configuration  

## SpringBoot Configuration  
SpringBoot는 애플리케이션 구동을 위한 기본적인 설정이 내부적으로 자동설정되므로 개발자가 별다른 설정없이도 서버가 정상적으로 구동합니다.  
하지만 기본값 외에 다른 값을 추가하고 싶다면?  
이미 설정되어있는 값을 변경해서 사용하고 싶다면?  
application.properties 또는 application.yml 파일을 통해 커스텀한 설정이 가능합니다.  


프로젝트 소스경로를 보면 src/main/java와 src/main/resources가 있습니다.  
resources에 application.properties 파일을 하나 추가합니다.  
파일이 추가되면 아래와 같이 원하는 설정값을 추가합니다.  
```properties
# Properties for ConfigurationProperties-Sample
author.email = chpark@test.com
author.name = chpark
author.age = 34
author.married = true 
author.job = Server programmer
```

이제 애플리케이션에서 위에 설정된 값을 읽을 수 있게 됩니다.  
위의 author properties를 읽기 위해 AuthorProperties 클래스를 하나 추가합니다.  
프로퍼티를 읽는 방법으로는 @Value Annotation을 사용하는 방식과 @ConfigurationProperties 방식으로 사용이 가능합니다.  
(다른 방법이 더 있겠지만 전 이 2가지만 사용했습니다.)  

### @Value 애너테이션을 사용  
```java
@Component
public class AuthorProperties {
    @Value("${author.name}")
    private String name;

    @Value("${author.email}")
    private String email;

    @Value("${author.age}")
    private Integer age;

    @Value("${author.married}")
    private boolean isMarried;

    @Value("${author.job}")
    private String job;
}
    
```  

메인 클래스를 구현하고 AuthorProperties 객체를 주입받습니다.  
저는 생성자 주입을 선택했지만 @Autowired를 사용해도 무방합니다.  
애플리케이션이 구동된 후 프로퍼터를 읽을 예정이기때문에 ApplicationRunner 인터페이스를 구현했습니다.  
애플리케이션이 구동된 후 오버라이딩한 run() 메서드에서 결과를 확인할 수 있습니다.  


웹 애플리케이션의 설정을 None으로 설정하였는데, Spring은 크게 3가지로 서버를 구동시킬 수 있습니다.  
None : 웹 애플리케이션으로 실행되지 않습니다.
Servlet : 내장 서블릿 웹 서버가 기동되므로 서블릿 기반 웹 애플리케이션으로 실행됩니다.  
Reactive : 내장 리액티브 웹 서버가 기동되므로 비동기 방식의 웹 애플리케이션으로 실행됩니다.  


예제에서는 웹 기술을 사용하지 않으므로 None방식으로 구동하였습니다.  
웹 서버가 올라오지 않으므로 테스트를 좀 더 빠르게 진행할 수 있기 때문입니다.  

```java
@SpringBootApplication
public class PropertyApplication implements ApplicationRunner {
    private final AuthorProperties authorProperties;

    public PropertyApplication(AuthorProperties authorProperties) {
       this.authorProperties = authorProperties;
    }

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(PropertyApplication.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        application.run(args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("AuthorProperties: " + authorProperties.toString());
    }
}
```

구동하면 아래와 같이 application.properties에 기술한 설정값들이 정상적으로 출력됩니다.  
![Alt](/images/2021-06/springboot-properties-value-print.png)  


### @ConfigurationProperties 사용  
위 @Value 방식의 단점으로는 프로퍼티의 depth가 길어지거나 중복된 필드명이 있는경우, @Value의 속성값이 장황해지게 됩니다.  
또한 매 필드마다 애너테이션을 설정해줘야 하며 개발자가 직접입력한 String값이므로 오타가 발생할 수 있습니다.  
이런 문제는 @ConfigurationProperties 방식을 사용하여 해결이 가능합니다.  
위의 AuthorProperties 클래스를 아래와 같이 변경해보겠습니다.  
```java
@Component
@ConfigurationProperties("author")
public class AuthorProperties {
    private String name;

    private String email;

    private Integer age;

    private boolean isMarried;

    private String job;
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public void setAge(Integer age) {
        this.age = age;
    }
    
    public void setMarried(boolean married) {
        isMarried = married;
    }
    
    public void setJob(String job) {
        this.job = job;
    }
}
```

@ConfigurationProperties("author") 애너테이션을 추가하면 properties파일에서 author로 시작하는 프로퍼티들을 가져옵니다.  
@Value를 사용했을 땐 각 필드에 애너테이션을 추가해야했지만 이 방식에선 그런 작업이 필요없습니다.  
단, 프로퍼티의 값을 객체로 매핑해야하므로 Setter 메서드를 구현해야 합니다.  
예제에는 Setter 메서드로 인해 코드가 지저분해졌지만 이후 lombok 라이브러리를 사용하면 자동으로 Setter가 생성되므로 문제되지 않습니다.  
실행하면 결과는 동일한값으로 출력됩니다.  

이번엔 여기에 Validation을 한번 적용해보겠습니다.  
Validation을 사용하려면 Springboot 종속성 모듈이 추가로 필요합니다.  
SpringBoot 2.2.x까지는 web모듈만 사용하면 Validation모듈이 내부적으로 함께 따라왔지만, 2.3부터는 Validation이 분리되었습니다.  
[공식문서 참고](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.3-Release-Notes#validation-starter-no-longer-included-in-web-starters)

따라서 pom.xml에 아래와 같이 validation 모듈을 추가합니다.  
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

AuthorProperties 클래스에 Validation을 적용하기위해 아래와 같이 필드들에 설정을 추가합니다.  
이름은 빈 값이 올 수 없도록, 이메일은 공백이 없도록, 나이는 0~200세 사이로만 설정하는 제약을 추가해보았습니다.  

```java
@Validated
@Component
@ConfigurationProperties("author")
public class AuthorProperties {
    @NotEmpty
    private String name;

    @NotBlank
    private String email;

    @Min(value = 0, message = "0세부터 입력 가능합니다.")
    @Max(value = 200, message = "200세까지만 입력 가능합니다")
    private Integer age;

    private boolean isMarried;

    private String job;
    
    // Setter 생략
}
```

만약 제약조건을 어긴 값을 application.properties에 추가할 경우 아래와 같이 애플리케이션이 실행되지않고 오류가 발생합니다.  
![Alt](/images/2021-06/springboot-properties-validation-error.png)  

다시 제약조건을 제대로 지키면 아래와 같이 성공한 메시지를 볼 수 있습니다.  
![Alt](/images/2021-06/springboot-properties-validation-success.png)