### Springboot 주요 Annotation  

### @Configuration  
클래스를 설정클래스로 정의합니다.  
하나 이상의 @Bean 메서드를 선언할 수 있고, Runtime에 SpringContainer를 통해 Bean을 정의하고 관리할 수 있습니다.  
@Configuration이 없어도 @Bean을 사용하면 SpringContainer에 등록은 되지만 singleton scope를 적용받지 못합니다.  



### @EnableAutoConfiguration  
애플리케이션을 구동할 때 자동설정된 값을 사용한다.  
spring.factories 안에 자동설정값들이 기술되어있기때문에 애플리케이션이 구동될 때 해당 Bean들이 생성된다.  



### @ComponentScan   
@Component 애너테이션을 가진 Bean들을 스캔하여 등록해주는 역할을 수행합니다.  
기본적으로 애너테이션을 선언한 클래스가 속해있는 패키지를 BasePackage로잡고 패키지 하위에있는 모든 패키지들에 대해 탐색을 시도합니다.  
옵션을 주어 basePackage를 변경하거나 특정 클래스를 추가/제외하는 작업도 가능합니다.  



### @SpringBootApplication  
애플리케이션의 메인 클래스에 붙여줍니다.  
@Configuration + @EnableAutoConfiguration + @ComponentScan 3가지의 기능을 수행합니다.   
따라서 이 애너테이션을 선언한 메인 클래스는 설정 클래스의 역할도 수행이 가능하고, 자동설정을 활성화시키므로 별다른 설정없이도 애플리케이션은 기본값으로 설정되어 구동됩니다.  
또한 메인클래스의 패키지 및 하위 패키지들로부터 등록해야 할 Bean을 찾아 등록하게 됩니다.  
웹 애플리케이션을 개발할때 자주사용하는 @Controller, @Service, @Repository, @Configuration 애너테이션들은 모두 @Component를 선언하고있으므로 스캔대상이 되어 자동으로 SpringContainer에 등록됩니다.  



### @Component  
SpringContainer에 등록할 Bean 클래스임을 명시적으로 선언합니다.  
ComponentScan에 의해 탐색된 Component들은 Bean으로 등록됩니다.  
기본 scope는 Singleton입니다.  



### @Bean  
@Component와 마찬가지로 클래스를 Bean으로 등록하기위해 사용합니다.  
@Component는 개발자가 추가한 클래스에 애너테이션을 추가하여 Scanning될 수 있는 기능이라면  
@Bean은 설정 클래스(@Configuration)에서 클래스를 로드할 때 Bean으로 등록하게 됩니다.  
외부 라이브러리와 같이 개발자가 직접 코드를 수정할 수 없을 때(@Component를 붙일 수 없으므로) @Bean으로 등록하면 해당 클래스를 Spring Bean으로써 사용이 가능합니다.  


### RestController  
@Controller 와 @ResponseBody 를 합친 애너테이션으로 메서드의 결과값을 JSON 형태의 String으로 전달합니다.  
일반적인 Controller는 View를 반환하는 용도로 사용하지만 RestController는 View가 필요없는 RestAPI를 지원하는 서비스에서 사용합니다.  



### RequestMapping  
Client로부터 요청 온 URI를 파싱하여 어떤 메서드가 처리할 것인지 매핑해줍니다.  
value값으로 처리할 URI 경로가 주어지며, 주어진 경로의 요청이 올 경우 해당 메서드가 Http요청을 처리하게 됩니다.  
RequestMapping만을 사용하게 될 경우 HttpMethod(Get, Post 등등)를 추가적으로 입력해야 하는 번거로움이 있습니다.  
따라서 보통 @GetMapping, @PostMapping으로 선언하거나 둘을 혼합하여 사용합니다.  
(@RequestMapping은 공통 경로, @{Method}Mapping은 식별 경로)  



