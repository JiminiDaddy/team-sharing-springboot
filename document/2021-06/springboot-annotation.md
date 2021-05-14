### Springboot 주요 Annotation  

### @Configuration  
클래스를 설정클래스로 정의합니다.  
하나 이상의 @Bean 메서드를 선언할 수 있고, Runtime에 SpringContainer를 통해 Bean을 정의하고 관리할 수 있습니다.  
@Configuration이 없어도 @Bean을 사용하면 SpringContainer에 등록은 되지만 singleton scope를 적용받지 못합니다.  



### @EnableAutoConfiguration  
애플리케이션을 구동할 때 자동설정된 값을 사용하니다.  
spring.factories 안에 자동설정값들이 기술되어있기때문에 애플리케이션이 구동될 때 해당 Bean들이 생성됩니다.



### @ComponentScan   
@Component 애너테이션을 가진 Bean들을 스캔하여 등록해주는 역할을 수행합니다.  
기본적으로 애너테이션을 선언한 클래스가 속해있는 패키지를 BasePackage로잡고 패키지 하위에있는 모든 패키지들에 대해 탐색을 시도합니다.  
옵션을 주어 basePackage를 변경하거나 특정 클래스를 추가/제외하는 작업도 가능합니다.  
대개 @SpringBootApplication이 선언되어 있는 메인 클래스는 최상위 패키지나 최상위 패키지의 다음 패키지에 구현하므로, 대부분의 Component는 Scan대상입니다.  



### @SpringBootApplication  
애플리케이션의 메인 클래스에 붙여줍니다.  
@Configuration + @EnableAutoConfiguration + @ComponentScan 3가지의 기능을 수행합니다.   
따라서 이 애너테이션을 선언한 메인 클래스는 설정 클래스의 역할도 수행이 가능하고, 자동설정을 활성화시키므로 별다른 설정없이도 애플리케이션은 기본값으로 설정되어 구동됩니다.  
또한 메인클래스의 패키지 및 하위 패키지들로부터 등록해야 할 Bean을 찾아 등록하게 됩니다.  
웹 애플리케이션을 개발할때 자주사용하는 @Controller, @Service, @Repository, @Configuration 애너테이션들은   
모두 @Component를 선언하고있으므로 스캔대상이 되어 자동으로 SpringContainer에 등록됩니다.  



### @Component  
SpringContainer에 등록할 Bean 클래스임을 명시적으로 선언합니다.  
ComponentScan에 의해 탐색된 Component들은 Bean으로 등록됩니다.  
기본 scope는 Singleton입니다.  
참고로 bean scope는 singleton외에도 prototype, request, session 등이 있습니다.  



### @Bean  
@Component와 마찬가지로 클래스를 Bean으로 등록하기위해 사용합니다.  
@Component는 개발자가 추가한 클래스에 애너테이션을 추가하여 Scanning될 수 있는 기능이라면  
@Bean은 설정 클래스(@Configuration)에서 클래스를 로드할 때 Bean으로 등록하게 됩니다.  
외부 라이브러리와 같이 개발자가 직접 코드를 수정할 수 없을 때(@Component를 붙일 수 없으므로) @Bean으로 등록하면 해당 클래스를 Spring Bean으로써 사용이 가능합니다.  
SpringContainer로부터 Bean을 관리받고 싶다면 반드시 @Bean은 @Configuration이 붙은 설정 클래스 내에 추가되어있어야 합니다.  
그 이유에 대해선 Bean Management 파트에 따로 정리하였습니다.  



### @RestController  
@Controller 와 @ResponseBody 를 합친 애너테이션으로 메서드의 결과값을 JSON 형태의 String으로 전달합니다.  
일반적인 Controller는 View를 반환하는 용도로 사용하지만 RestController는 View가 필요없는 RestAPI를 지원하는 서비스에서 사용합니다.  
RestController를 사용할 때 자동으로 JSON이 반환되는 이유는 @RestController가 @ResponseBody를 갖고있기 때문입니다.  
(과거에는 @Controller + @ResponseBody의 조합으로 Controller에서 json을 반환할 수 있었습니다.)  



### @Service  
클라이언트의 Http요청을 처리하기 위해 보통 Controller, Service, Domain을 구현하게됩니다.  
보통 컨트롤러 클래스는 @Controller 또는 @RestController를, 서비스 역할을 하는 클래스에는 @Service를 선언합니다.  
하지만 @Service는 Bean으로 등록한다는 것 외에는 별다른 기능은 없습니다.  
아마 개발자들끼리 명시적으로 Service Class라는 것을 강조하기위해 추가된게 아닌가 생각됩니다.  



### @RequestBody  
Client가 Post방식으로 HttpBody에 데이터를 실어 보냈을 때, Server에서 이를 객체로 매핑해주는 역할을 수행합니다.  
HttpMessageConverter를 사용하며, MediaType이 json인 경우, 이를 구현한 MappingJackson2HttpMessageConverter가 사용됩니다.  
따라서 서버에서 json type의 데이터를 Java Object로 매핑하기 위해서는 Jackson 라이브러리가 추가되어있어야 합니다.  
(SpringBoot를 사용할 경우 기본적으로 jackson이 의존성으로 인해 추가됩니다.)  

무조건 Jackson만 실행되는것은 아니고, 지원되는 ObjectType과 MediaType에 따라 구현체가 선택됩니다.  

만약 기본 값 외에 커스텀된 MessageConvert를 사용하고 싶다면, WebMvcConfigurer 인터페이스를 구현하고, configureMessageConverts 메서드를 Overriding하여 원하는 MessageConvert 구현체를 등록하면 됩니다.  
~~MessageConverter에 대한 내용은 추후 따로 정리하겠습니다.~~

참고로 Get방식으로 HttpBody에 데이터를 실어 보내면 POST로 매핑된다고 합니다.  
Tomcat은 기본적으로 Post방식만 HttpBody의 데이터를 파싱할 수 있으며, 나머지 방식들은 server.xml에서 아래와 같은 옵션을 설정해야 파싱이 가능합니다.  
```xml
<Connector parseBodyMethods="POST,PUT,DELETE" />
```  

Tomcat 서버에 커넥션을 맺게 될 때 사용되는 Connector 객체는 다음과 같이 초기화합니다.  
```java
public class Connector extends LifecycleMBeanBase {
    public Connector(String protocol) {
        this.service = null;
        this.allowTrace = false;
        this.asyncTimeout = 30000L;
        this.enableLookups = false;
        this.xpoweredBy = false;
        this.proxyName = null;
        this.proxyPort = 0;
        this.discardFacades = RECYCLE_FACADES;
        this.redirectPort = 443;
        this.scheme = "http";
        this.secure = false;
        this.maxCookieCount = 200;
        this.maxParameterCount = 10000;
        this.maxPostSize = 2097152;
        this.maxSavePostSize = 4096;
        this.parseBodyMethods = "POST";
        // ...
    }
}
```  
parseBodyMethods 변수는 HttpBody를 파싱할 수 있는 HttpMethod인데 기본적으로 POST로 설정되어 있습니다.  
실제로 HttpBody가 파싱되는 코드는 아래와 같이 구현되어 있습니다.  
```java
public class Connector extends LifecycleMBeanBase {
    // ...
    public void setParseBodyMethods(String methods) {
        HashSet<String> methodSet = new HashSet();
        if (null != methods) {
            methodSet.addAll(Arrays.asList(methods.split("\\s*,\\s*")));
        }

        if (methodSet.contains("TRACE")) {
            throw new IllegalArgumentException(sm.getString("coyoteConnector.parseBodyMethodNoTrace"));
        } else {
            this.parseBodyMethods = methods;
            this.parseBodyMethodsSet = methodSet;
            this.setProperty("parseBodyMethods", methods);
        }
    }
}    
```  
요청온 Http Method를 methodSet에 추가합니다. Set이므로 이미 저장된 값이라면 덮어써질 것입니다.  
TRACE 타입을 제외하고는 새롭게 들어온 Method를 파싱 타입으로 설정하여 파싱을 진행하게 됩니다.  



### @ResponseBody  
@RequestBody와 반대되는 역할로써 Java Object를 MessageConvert를 통해 정해진 타입의 데이터를 HttpResponse Body에 넣어 반환합니다.  
Default MessageConverter가 Jackson이므로 json 타입의 데이터를 반환합니다.  
@RequestBody와 기능이 반대될 뿐 사용하는 방식은 모두 동일합니다.  



### RequestMapping  
Client로부터 요청 온 URI를 파싱하여 어떤 메서드가 처리할 것인지 매핑해줍니다.  
value값으로 처리할 URI 경로가 주어지며, 주어진 경로의 요청이 올 경우 해당 메서드가 Http요청을 처리하게 됩니다.  
RequestMapping만을 사용하게 될 경우 HttpMethod(Get, Post 등등)를 추가적으로 입력해야 하는 번거로움이 있습니다.  
따라서 보통 @GetMapping, @PostMapping으로 선언하거나 둘을 혼합하여 사용합니다.  
(@RequestMapping은 공통 경로, @{Method}Mapping은 식별 경로)  



### Autowired  
자동 의존주입을 위해 사용합니다.  
Spring에서 사용하는 자동 의존주입은 크게 3가지가 있습니다.  
1. @Autowired  
2. 설정 메서드(Setter)를 사용  
3. 생성자 주입  

@Autowired는 Type으로 조회합니다.  
예를들어 아래와 같은 코드가 있다고 가정하겠습니다.  
```java
public class MemberService {
    @Autowired
    private MemberRepository memberRepository;
}
```  
MemberService는 memberRepository Bean을 자동으로 의존을 주입받고 있습니다.  
이 때 MemberRepository.class 타입으로 Bean을 조회하여 의존성을 주입합니다.  
만약 MemberRepository를 구현한 하위 클래스들이 있고, 그 클래스들이 모두 Bean으로 등록되었다면 문제가 발생합니다.  
SpringBoot의 과거버전에서는 기본값이 Overriding (마지막에 등록된 Bean이 주입됨)이었지만  
2.x 버전에서는 기본값은 서버가 구동될 때 SpringBoot가 강제로 오류를 발생시켜 서버를 종료시킵니다.  
단 프로파일에 아래와 같은 옵션을 설정함으로써 기존과 동일하게 Overriding이 가능합니다.  
```properties
pring.main.allow-bean-definition-overriding=true
```

