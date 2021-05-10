# HelloWorld 예제 프로젝트 작성  

## 프로젝트 생성   
회사에서는 이클립스를 사용하지만 저는 개인적으로 인텔리제이를 선호합니다.  
이클립스에 비해 가볍고(사실 최근 인텔리제이는 기능이 많아져서 무거웠다고하네요) 자동완성 기능도 좋습니다.  
무료버전도 있으니 집에서 개발해보실 분들은 인텔리제이 사용을 추천드립니다.  
이클립스 사용법은 모두 다 아실테니 아래 설명은 인텔리제이 기준으로 설명하였습니다.  

먼저 새 프로젝트를 선택합니다.  
(상단 File메뉴 -> New -> Project)  
Maven을 선택합니다.  
![Alt](/images/2021-06/springboot-new-project-maven.png)  
groupId, artifactId를 설정합니다.  
예제이므로 그냥 편한 네이밍을 선택하시면 됩니다.  
![Alt](/images/2021-06/springboot-new-project-maven-setting.png)  
프로젝트 생성 경로를 확인한 후 Finish버튼을 선택해 프로젝트를 생성합니다.  
![Alt](/images/2021-06/springboot-new-project-maven-finish.png)  


## 종속성 추가  
maven을 사용하므로 아래와 같이 pom.xml에 필요한 종속성을 추가합니다.  
저는 MainProject를 하나 만들고 예제로 활용할 모듈을 추가 생성하는 방식을 사용했습니다.  
(이후 추가 예제들도 모두 모듈로 관리할 예정입니다. 모듈을 사용하고 싶지않다면 모듈 생성안하고 바로 MainProject의 pom.xml을 작성하면 됩니다.)  

MainProject에 있는 pom.xml에 아래와 같이 작성합니다.  
모듈안에 있는 pom.xml에 작성해도 무관합니다.  
단 저는 다른 예제프로젝트에서도 spring-boot-starter 패키지를 사용할것이므로 중복을 제거하고자 공통부분은 메인프로젝트에 작성하였습니다.  
parent tag는 이름에서 유추할수 있듯이 종속성을 상속하겠다는 의미입니다.  

저와 같이 프로젝트 안에 모듈을 생성할경우 모듈의 pom.xml에는 아래와 같은 코드가 추가될 것입니다.  
![Alt](/images/2021-06/springboot-maven-child-module-pom-parant-tag.png)  
이 모듈은 team-sharing-springboot 라는 프로젝트를 상속받았고, 프로젝트에서 추가한 종속성을 그대로 상속받아 사용하겠다는 의미입니다.  

프로젝트에 있는 pom.xml는 아래와 같은 코드를 추가하였습니다. (parent, dependencies)  
parent에 Springboot version을 명시하였으므로 이 프로젝트는 해당 버전의 Springboot에 종속됩니다.  
다른 버전의 스프링 버전을 사용하고 싶다면 parent TAG안에 있는 spring-boot-starter-parent의 버전을 변경하시면 됩니다.  
이제 이 프로젝트는 parent에 기술한 Springboot 버전에 종속됩니다.  
dependency에 종속성 모듈을 불러올 때 버전을 따로 설정하지 않아도 위에 정의한 Springboot 버전에 호환되는 버전을 사용하게 됩니다.  
그리고 spring-boot-starter-web 패키지를 종속성에 추가하였으므로 Springboot는 내장 Tomcat 서버를 사용해 웹 서버와 스프링을 함께 실행시킵니다.  
```xml
<project>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.3.0.RELEASE</version>
        <relativePath/>
    </parent>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>
</project>
```

## 메인 애플리케이션 코드 작성  
종속성을 추가하였다면 자바 디렉토리에 패키지를 하나 추가하고, 클래스를 하나 작성합니다.  
저는 HelloWorld 애플리케이션이므로 HelloApplication이라 작성하였습니다. 
아래와 같이 작성하면 웹 애플리케이션을 실행할 준비가 완성됩니다.  
```java
@SpringBootApplication
public class HelloApplication {
    public static void main(String[] args) {
        SpringApplication.run(HelloApplication.class, args);
    }
}
```

## 애플리케이션 실행  
상단 Run 메뉴 > Run HelloApplication을 선택하거나 단축키 Command + Shift + R(Windows의 경우 Control + Shift + R)을 눌러서 애플리케이션을 실행합니다.  
아래와 같은 로그가 출력된다면 정상적으로 웹 애플리케이션이 구동된 것입니다.  
![Alt](/images/2021-06/springboot-run-helloapplication.png)  
로그를 확인해보니 8080포트로 Tomcat 서버가 올라간것을 확인할 수 있습니다.  
스프링부트는 애플리케이션 구동에 필요한 정보를 기본값으로 미리 설정하는데 포트번호도 그 중 하나입니다. 기본값은 8080입니다.  
물론 아직은 웹브라우저를 통해 어떤 경로를 입력해도 오류가 발생합니다. 
요청된 경로를 처리할 Controller가 없기 때문입니다.  
이번엔 /hello 라는 URL을 브라우저에서 요청했을 때 "Hello World" 라는 문자열을 반환하는 RestAPI를 구현해보겠습니다.  

메인 패키지에 web이라는 패키지를 하나 추가한 후, HelloController라는 Class를 하나 생성합니다.  
Class가 생성되었다면 클래스 상단에 RestController 애노테이션을 추가합니다.  
이 애노테이션 추가 하나만으로 RestAPI를 구성이 가능합니다.  
```java
@RestController
public class HelloController {
}
```  

그리고 String 반환타입의 hello() 메서드를 아래와 같이 구현합니다.  
```java
@RestController
public class HelloController {
    @GetMapping("/hello")
    public String hello() {
        return "Hello World";
    }
}
```

이제 /hello 라는 경로에 대해 Get요청이 오면 우리가 만든 애플리케이션은 "Hello World" 문자열을 반환합니다.  
애플리케이션이 아직 실행중이라면 중지하고, 새롭게 실행합니다.  

웹 브라우저에 localhost:8080/hello 라고 치면 아래와 같이 원하던 결과가 출력됩니다.  
![Alt](/images/2021-06/springboot-run-hello-get.png)  

### 만약 이것들을 SpringFramework만을 이용해서 구현했다면..? 
Controller 클래스를 구현하는 것 이외에 이런 작업들을 진행해야합니다.  
1. Tomcat 서버 설치 및 server.xml 설정  
2. web.xml 설정 (servlet)  
3. application-context.xml (bean)  

하지만 SpringBoot를 사용하면 클래스 2개 생성, 애너테이션 2개 추가, 메서드 1개추가만으로 간단한 RestAPI를 구현할 수 있게되었습니다.
