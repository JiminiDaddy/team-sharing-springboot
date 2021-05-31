# JPA ?  

## JPA란? 

Java Persistence API  
Java로 구성된 ORM(Object Relation Mapping) 기술 표준으로써 Java Object와 Database Mapping을 수행합니다.    
JPA는 Java ORM기술에 대한 API표준 명세일 뿐 실제 구현체는 아니다.  
따라서 JPA를 사용하기위해서는 Hibernate와 같이 JPA 인터페이스를 구현한 ORM 프레임워크를 사용해야 합니다.  
실제로 Spring-Data-JPA 프로젝트를 사용해서 개발을한다면 내부적으로 Hibernate 구현체를 사용합니다.   
구현체와 인터페이스를 분리하면 객체지향적으로 한가지 좋은점이 있는데 상위 레이어에서 하위 레이어에 의존하지 않는 장점이 있습니다.  
필요에 따라 다른 구현체를 사용하고 싶다면 설정에서 ORM 구현체만 변경해주면 됩니다.  
구현체는 반드시 JPA 인터페이스를 구현해야하므로 상위 애플리케이션 레이에서는 코드의 변경이 필요없게 됩니다.  
(OCP, DIP 준수)  



### 패러다임 불일치  

애플리케이션 개발자는 Java라는 언어를 사용해 객체지향이란 패러다임으로 개발합니다.    
객체란 소프트웨어에서 특정한 역할을 수행하고 역할에 따른 책임을 가진 독립적인 개체입니다.  
역할은 또다른 역할로 추상화가 가능하며, 객체들은 소프트웨어의 목적을 달성하기 위해  
각자 맡은 책임을 수행하거나 다른 객체로 위임하는 등 협력관계를 갖습니다.  

반면 관계형 데이터베이스의 테이블은 데이터를 중심으로 구조화된 하나의 큰 데이터 집합입니다.  
테이블은 특정 주제를 표현하기위해 세부적인 필드로 구성되어 있습니다.  
객체가 협력이 필요한 객체끼리 연관관계를 갖는다면 테이블은 외래키(FK)를 이용해 서로 다른 테이블의 연관관계를 가질 수 있습니다.  

이렇게 객체와 데이터베이스 간의 지향하는 방식이 다른 현상을 패러다임의 불일치라 말하며, Java진영에서 이것을 해결하기위해 JPA가 등장하였습니다.  

### 객체 그래프 탐색  

객체는 참조를 통해 협력관계를 가진 수많은 객체들과 연관관계를 맺고 있습니다.  
그리고 필요한 정보를 조회하기위해 참조하고 있는 객체들을 동적으로 탐색하면서 원하는 객체를 찾아 목적을 달성핧 수 있습니다.  
이것을 객체 그래프 탐색이라고 말합니다.  

반면 데이터베이스는 SQL 실행시점에 연관관계가 확정되기 때문에 실행한 SQL에 따라 객체 그래프 탐색이 정해집니다.  
이 경우 아래와 같이 개발자가 작성한 코드 레벨에서 디버깅이 불가능한 단점이 발생합니다.  
```java
class Memberservice {
    public void find(String memberId) {
        Member member = memberDAO.find(memberId);
        // team 객체가 항상 탐색 가능할까?   
        // Member에 매핑된 Team이 없다면 아래 코드가 정상적으로 실행될 수 있을까?  
        Team team = member.getTeam();
    }
}
``` 

### JPA를 사용하면 장점  

1. 위에서 설명한 것과 같이 JPA를 사용하면 객체지향과 데이터중심의 서로 다른 패러다임을 하나로 연결시킬 수 있습니다.  
   (1)에 의해 개발자는 데이터에 접근할 때도 객체지향 프로그래밍이 가능해지므로 비즈니스 로직에 집중할 수 있습니다.  
2. 객체 그래프 탐색을 통해 필요한 시점에 필요한 정보를 조회할 수 있습니다.  
3. 엔티티에 필드가 추가/삭제될경우 해당 필드만 변경하면되므로 SQL문을 새로 작성해야 할 필요가 없어집니다.  
   이것은 유지보수가 엄청나게 좋아집니다.  
4. 일반 자바 객체를 다루듯이 사용해도 DDL, DML문이 자동으로 생성되어 작성해야 할 SQL이 줄어드므로 생산성이 향상됩니다.   
   ~~개발자의 시간은 매우매우 비싼 자원입니다!!~~  
5. 같은 트랜잭션 내에서 동일한 엔티티를 조회할 경우 영속성 컨텍스트로부터 데이터를 가져오므로 DB 호출이 줄어들 수 있습니다.      

   
### 즉시로딩과 지연로딩  

SQL로 데이터를 조회하면 SQL 실행시점에 테이블 간 연관관걔가 확정되므로 항상 즉시로딩으로 조회됩니다.  
이 경우 한번에 애플리케이션에서 DB로 호출하는 횟수를 줄일 수 있는 장점이있지만 필요하지 않는 정보도 함께 조회할 수 있는 문제도 발생합니다.  
예를들어, 축구선수라는 테이블과 팀이라는 테이블이 매핑 관계를 맺고있을 때 
메시라는 축구선수를 조회할 때 팀의 정보까지 함께 조회하고 싶을수도, 싶지 않을수도 있습니다.  
이것을 SQL로 사용한다면 각각의 경우에 대해 Query를 작성해야 할 것입니다.  
하지만 JPA를 사용하면 굳이 두 경우를 나누어 개발을 할 필요가 없어집니다.  
축구선수라는 객체를 가져오고, 팀 정보는 필요할 때 조회하면 된다. (필요없으면 조회X)  
위 예시를 JPA는 아래와 같이 사용됩니다.  
```java
class SoccerMemberService {
    private final SoccerMemberRepository soccerMemberRepository;
    // ... 의존성 주입 생략
    public void findMember(String memberId) {
        SoccerMember member = soccerMemberRepository.findById(memberId);
        // 만약 팀의 정보도 필요하다면?
        SoccerTeam team = member.getTeam();
        System.out.println("team: " + team.getName());
    }
}
```  

JPA를 사용하면 위와같이 필요할경우 Team의 정보를 조회하면 됩니다.  
이렇게 필요한 시점에 객체를 조회하는 방식을 지연로딩이라고 하니다.  
~~JPA를 사용해 테이블과 매핑한 객체를 Entity라고 하는데 이것은 추후 다시 설명하도록 하겠습니다.~~   


### 동일성, 동등성 비교  

Java Object를 비교하는 방법에는 크게 2가지가 존재합니다.  
참조하는 객체의 주소값끼리 비교하는 동일성 비교와 객체의 값끼리 비교하는 동등성 비교가 있습니다.  
동일성 비교는 == 으로 비교하며, 동등성 비교는 equals() 메서드를 통해 비교합니다.  
Java에서는 이렇게 두 가지로 객체를 비교할 수 있기 때문에 테이블에서 Row를 비교하는것과 결과가 다르게 나올 수 있습니다.  

SQL로 MemberID를 통해 SoccerMember라는 데이터를 조회할 경우, 같은 MemberId를 사용해 조회하면 두 데이터의 값은 같습니다.  
따라서 동등성 비교는 성공합니다.  
하지만 동일성 비교는 성공할까요?  
아래 예제를 실행하면 동등성 비교는 성공하더라도 동일성 비교는 실패합니다.  

DAO 객체는 SQL-Mapper를 사용한 DAO라고 가정하겠습니다.  
```java
class SockerMemberDAO {
    public SoccerMember getMember(String memberId) {
        String sql = "SELECT * FROM SOCCER_MEMBER WHERE MEMBER_ID = ? ";
        // result = ... JDBC API 호출
        return new SoccerMember(result);
    }
}
class SoccerMemberService {
    @Transactional
    public void findMember(String memberId) {
        SoccerMember member1 = soccerMemberDAO.getMember(memberId);
        SoccerMember member2 = soccerMemberDAO.getMember(memberId);
        System.out.println(member1.equals(member2));    // 동등성 성공
        System.out.println(member1 == member2);         // 동일성 실패
    }
}
```  

같은 테이블의 같은 로우를 반환함에도 불구하고 동일성 비교가 실패하는 이유는 단순합니다.  
데이터를 조회할때마다 new 키워드를 통해 SoccerMember 객체를 새로 생성하기 때문입니다.  

만약 SoccerMember를 List와 같은 Collection으로 보관할 수 있다면 두 객체의 동일성 비교는 성공하겠지만 데이터베이스에서 위와같은 상황을 동일성 비교하는것은 쉽지 않습니다.  
하지만 JPA의 경우 같은 트랜잭션일 때 같은 객체가 조회되는것을 보장합니다.  
왜냐하면 JPA는 내부적으로 Collection을 사용해 객체들을 관리하기 때문입니다.  
예시로 아래와 같은 케이스의 동등성 및 동일성 비교가 항상 성공합니다.  
__단, 같은 트랜잭션 내에서 실행되어야 합니다.__

```java
class SoccerMemberService {
    private final SoccerMemberRepository soccerMemberRepository;
    // ... 의존성 주입 생략
    @Transactional
    public void findMember(String memberId) {
        SockerMember member1 = soccerMemberRepository.findById(memberId);
        SockerMember member2 = soccerMemberRepository.findById(memberId);
        System.out.println(member1.equals(member2));    // 동등성
        System.out.println(member1 == member2);         // 동일성
    }
}
```  
 
### 개인적인 의견  
많은 분들이 ORM과 SQL-Mapper 방식을 혼동하는 것 같다.  
특히 ORM에 대해 설명하면서 MyBatis를 함께 말씀해주시는 분들이 있는데 엄연히 둘은 다르다.  
ORM은 객체와 RDBMS의 데이터를 매핑한것이고, SQL-Mapper는 SQL문과 데이터를 매핑한다.  
RDBMS에 따라 일부 SQL 문법이 다를 수 있기때문에 SQL-Mapper를 사용하면 DB에 종속적이게 된다.  
(DIP 위반)  

JPA와 같은 ORM을 사용하면 SQL을 직접사용하지 않는 장점이 있지만 그렇다고해서 SQL을 몰라도 되는건 아니다.  
오히려 SQL을 안쓰고도 SQL이 어떻게 구현되는지 예상할 수 있어야하므로 더 어려운 면도 있다.  
하지만 일부 개발자들은 마치 JPA를 사용하면 SQL로부터 해방된다고 생각하는데 개인적으로 잘못된거라고 생각한다.  

