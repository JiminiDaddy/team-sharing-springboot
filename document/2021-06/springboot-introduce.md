# SpringBoot  

## SpringBoot란?  
Spring기반의 애플리케이션을 쉽게 만들기위해 단독으로 실행가능하게 도와주는 Spring Project  
SpringFramework을 사용하기 쉽게 대부분의 환경설정을 내부적으로 지원합니다.  


## SpringBoot의 특징   
- 단독으로 실행가능한 스프링 어플리케이션을 생성할 수 있습니다.  
- Tomcat, Jetty, Undowtow를 내장하고 있습니다.  
- 빌드구성을 단순화 하기위해 'starter' 종속성 모듈 제공합니다.  
- 가능한 스프링 및 외부 라이브러리를 자동으로 구성합니다.  
- 설정을 위한 코드 생성 및 XML 구성이 필요없습니다.  
- 통계, 상태체크, 외부설정 등 상용화에 필요한 기능 제공합니다.  


## SpringBoot는 왜 만들어졌을까?  
SpringFramework을 사용해서 애플리케이션을 개발할 경우, XML 설정으로 해줄 게 굉장히 많습니다.  
Bean, Inteceptor, Servlet, Filter, Datasource, ORM 등 환경 설정에 필요한 모듈을 사용하기위해 xml에 직접 구성하는 경우가 많으며, 일일이 xml 파일들을 뒤지면서 필요한 정보를 찾는 작업은 개발 생산성이 떨어지게됩니다.  
Spring 3.x 이후 Java Annotation으로 지원되는 부분이 많아졌으나 아직도 레거시 프로젝트 대부분은 xml로 애플리케이션의 설정을 관리하게됩니다.  


## Spring과 SpringBoot의 차이는?  
Spring은 애플리케이션 외부에 Tomcat이나 WAS가 미리 설치되어있어야 하지만 SpringBoot는 내장서버를 사용하므로 별도로 설치할 필요가 없습니다.  
Spring은 WAR로 빌드해서 정해진 경로에 리소스를 배포하지만 SpringBoot는 빌드한 Jar를 바로 배포하면 됩니다.  
Spring은 의존되는 모듈들의 버전관리를 직접해줘야하지만 SpringBoot는 'starter'를 사용할경우 자동으로 의존성관리를 하게됩니다.  


개인적으로 Springboot를 사용할 경우 XML에서 자유로워진다는게 저는 가장 큰 장점중에 하나 인 것 같습니다.  
간단한 예로 springMVC만을 사용할 경우 아래와 같이 Bean들을 xml에 기술해야 합니다.  
![Alt Text](/images/2021-06/springboot-config-xml.png)  
위 예제는 Bean 몇개를 추가한 xml인데 실제 운영중인 서버라면 수백 수천개의 Bean들을 관리할것이고.. xml 설정은 Hell이 될 수 있다.  
xml은 Bean뿐만 아니라 Servlet, Datasource 등등 애플리케이션 설정을 모두 관리하는데 Springboot를 통해 xml로부터 자유로워질 수 있다는건 큰 장점중에 하나라고 생각합니다.  


