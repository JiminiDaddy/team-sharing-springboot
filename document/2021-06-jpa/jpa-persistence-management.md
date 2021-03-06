# 영속성 관리  

프로그래머가 Database로부터 데이터를 조회하기 위해서는 크게 2가지 작업이 필요합니다.  
1. 테이블 설계 및 생성  
2. 테이블 조회 쿼리 작성 및 실행  

JPA의 기능도 유사합니다.  
JPA도 크게 엔티티와 테이블을 매핑하는 설계부분과 엔티티를 사용하는 부분으로 나눌 수 있습니다.  
그리고 엔티티의 CRUD(Create, Read, Update, Delete)작업 수행은 엔티티 매니저라는 객체가 담당하게 됩니다.

---

## EntityManagerFactory, EntityManager  

JPA를 통해 데이터를 관리하려면 엔티티 매니저를 사용해야하는데, 이 엔티티 매니저는 엔티티 매니저 팩토리를 통해 생성할 수 있습니다.  
엔티티 매니저 팩토리는 하이버네이트를 직접 사용할 경우 설정 파일의 persistence-unit의 이름을 가져와서 생성할 수 있고, SpringBoot를 사용하면 자동으로 생성합니다.  
보통 사용하는 데이터베이스 1개당 엔티티 매니저 팩토리는 1개만 생성하고 애플리케이션 전체에서 공유하여 사용합니다.    
왜냐하면 엔티티 매니저 팩토리를 생성하는 비용이 크기 때문입니다.  
따라서 싱글톤 패턴으로 생성하고 필요한 부분에서 의존성 주입을 통해 참조하여 사용해야합니다.  
엔티티 매니저 팩토리는 Thread-safe하기때문에 멀티 스레드 환경에서 동기화 이슈를 생각하지 않아도 됩니다.  

엔티티 매니저는 엔티티 매니저 팩토리로부터 생성되는데 Thread-safe하지 않아 동시성 문제가 발생할 수 있습니다.  
따라서 스레드에서 공유하지않고, 사용후엔 즉시 제거합니다.  

아래는 엔티티 매니저를 생성하는 예제 코드입니다.  
SpringBoot를 사용하여 EntityMangerFactory는 의존성 주입을 통해 사용하도록 구현하였습니다.  

```java
@SpringBootApplication
class EntityManagerExample {
    @Autowired
    private EntityManagerFactory entityManagerFactory;

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(EntityManagerExample.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        ConfigurableApplicationContext context = springApplication.run(args);

        EntityManagerExample entityManagerExample = context.getBean(EntityManagerExample.class);
        
        EntityManager entityManager = entityManagerExample.entityManagerFactory.createEntityManager();
    }
}
```  

Spring-Boot-Data를 사용하여 EntityManagerFactory를 자동으로 생성할 경우 기본값으로 'default' 란 이름으로 생성됩니다.  
아래와 같이 SpringBoot가 로딩되면 EntityManagerFactory의 이름이 'default'로 초기화 되는 것을 알 수 있습니다.  
![Alt](/images/2021-06-jpa/jpa-entitymanagerfactory-initialized-default.png)  

---

엔티티 매니저는 getTransaction() 메서드를 통해 트랜잭션을 가져올 수 있으므로 생성된 즉시 데이터베이스의 Connection을 얻지않고  
실제로 사용하는 시점에서 Connection을 얻어옵니다.  
참고로 Hibernate와 같은 ORM 프레임워크들은 매번 사용할 때 마다 데이터베이스와 Connection을 맺는것이 아니라 미리 Connection Pool을 만들어 사용합니다.  
Connection Pool은 엔티티 매니저 팩토리가 생성될 때 함께 생성됩니다.  

---

## 영속성 컨텍스트 (Persistence Context)  
JPA에서 가장 중요한 항목으로, 엔티티를 영구적으로 저장해주는 도구, 환경입니다.  
데이터베이스에 ROW를 저장하고, 조회하는 것과 같이 JPA에서 엔티티를 저장하고 조회하는 역할을 수행합니다.  
JPA는 persist() 메서드를 제공하여 엔티티를 저장할 수 있는데 엔티티를 저장한다는 것과 DB 테이블의 ROW를 저장하는 것은 다릅니다.  

JPA는 애플리케이션 영역과 JDBC Driver 사이에 존재하여 JDBC를 통해 DataSource에 접근하는 인터페이스를 추상화하였습니다.  
즉 JPA는 경우에따라 엔티티를 데이터베이스에 바로 저장할 수도 있지만 목적에 따라 저장하지 않을 수 있습니다.  
왜냐하면 JPA에서 저장공간은 데이터베이스가 아니라 영속성 컨텍스트이기 때문입니다.  

다시말해 엔티티 매니저를 통해 원하는 엔티티를 저장한다는것은, 엔티티를 영속성 컨텍스트에 저장한다고 말할 수 있습니다.  

영속성 컨텍스트는 데이터베이스의 캐시와 비슷하다고 생각하면 됩니다.  
매번 데이터베이스로부터 데이터를 조회한다면 한정된 커넥션으로 인해 성능 이슈가 발생할 수 있습니다.  
JPA는 이런 문제를 해결하기위해 한번 조회한 데이터는 영속성 컨텍스트의 1차 캐시에 보관하고,  
같은 트랜잭션 내에서 동일한 엔티티에 대해 조회 요청이 오면 데이터베이스로 요청하는것이 아니라 1차 캐시에 저장되어있는 엔티티를 반환합니다.  

---

### 엔티티의 생명주기  
엔티티는 아래의 4가지 상태중 하나의 상태를 갖게 됩니다.  
1. 비영속(new / transient)  
   한번도 영속화 된 적 없는 일반 객체로써, 영속성 컨텍스트와 전혀 관계 없는 상태  
   식별자(Id) 생성 전략에 따라 식별자가 있을수도 있고 없을 수도 있습니다.   
2. 영속(managed)  
   엔티티 매니저에 의해 영속성 컨텍스트에 저장되거나 조회된 상태  
   * JPQL을 통해서 조회된 엔티티 또한 영속 상태가 됩니다.  
3. 준영속(detached)  
   영속성 컨텍스트에 저장되었다가 엔티티 매니저로부터 close(), clear(), detach()가 호출되어 더 이상 관리되지 않는 상태  
   영속성 컨텍스트가 관리하지 않으므로 1차캐시, 트랜잭션 쓰기 지연, 지연 로딩, 변경 감지 등등 영속성 컨텍스트가 제공하는 기능을 사용할 수 없습니다.  
   영속화가 되었던 엔티티이기 때문에 반드시 식별자는 가지고 있습니다.  
4. 삭제(removed)  
   영속성 컨텍스트 및 데이터베이스로부터 엔티티가 삭제된 상태  
   트랜직션 쓰기 지연으로 인해 영속성 컨텍스트에서는 제거되어도 데이터베이스에는 남아있을 수 있습니다.   
   
---

## 영속성 컨텍스트의 특징  

모든 엔티티는 반드시 식별자를 갖고 있어야 합니다.  
왜냐하면 영속성 컨텍스트가 식별자를 통해 엔티티를 구분하기 때문입니다.  
만약 식별자가 없으면 다음과 같은 예외가 발생합니다.  

![Alt](/images/2021-06-jpa/jpa-entity-must-setting-id.png)  

---

영속성 컨텍스트에 저장된 엔티티는 트랜잭션을 커밋할 때 데이터베이스로 SQL을 전송하여 반영하며 이 과정을 flush라고 합니다.  

### 영속성 컨텍스트를 사용하면 아래와 같은 장점이 있습니다.  

#### 1차 캐시  

영속성 컨텍스트 내에 존재하는 엔티티는 엔티티 매니저가 조회요청을 하더라도 데이터베이스를 호출하지 않고 영속성 컨텍스트에 저장된 엔티티를 반환합니다.  
저장된 엔티티가 없는 경우에만 데이터베이스를 호출하므로 시스템의 조회 성능이 향상됩니다.  
이것이 가능한 이유는 영속성 컨텍스트가 내부적으로 캐시로 사용할 Map을 갖고 있기 때문입니다.  
이 Map은 Key를 @Id로, Value는 Entity Instance로 저장되어 관리됩니다.  
데이터베이스에서 조회한 데이터로 엔티티를 생성하면 이 때 영속성 컨텍스트의 1차 캐시에 저장되며, 저장된 엔티티를 반환하게 됩니다.  
  
#### 동일성 보장  

같은 트랜잭션 내에서 영속성 컨텍스트로부터 조회된 엔티티는 항상 동일성이 보장됩니다.  
영속성 컨텍스트는 엔티티를 Collection으로 보관하기 때문에 같은 Index의 엔티티는 항상 동일한 객체가 반환됩니다.   
  
#### 트랜잭션을 지원하는 쓰기 지연  

엔티티를 저장할 때 즉시 데이터베이스로 SQL을 전송하지 않고 꾸준히 모은 뒤 트랜잭션 커밋시점이되면 한번에 전송합니다.  
이로인해 DB 접근 횟수를 줄이들어 쓰기 성능이 향상됩니다.  
엔티티가 영속성 컨텍스트에 저장될 때 등록 Query가 만들어지는데, 이 Query는 커밋 전까지 쓰기지연 SQL 저장소에 보관됩니다.  
정확히는 트랜잭션이 커밋 될 때 엔티티 매니저가 영속성 컨텍스트를 flush하는데, flush에 의해 등록 Query가 데이터베이스에 반영됩니다.  
flush란 영속성 컨텍스트와 데이터베이스의 동기화 작업을 말하며, 쓰기지연 SQL 저장소에 있는 Query들을 데이터베이스에 반영함으로써 동기화가 이루어집니다.   
트랜잭션 쓰기 지연이 가능한 이유는 등록(Insert) Query가 호출 될 때가 아니라 Commit이 될 때 데이터베이스에 반영되기 때문입니다.  
따라서 커밋 전까지 등록 Query를 모아둔 뒤 한번에 SQL들을 전송하고 커밋해도 트랜잭션이 성공하게 됩니다.   
삭제의 경우도 커밋 전까지 SQL 쓰기지연 저장소에 Delete Query를 모은 뒤 한번에 전송합니다.  
단, 삭제 시점에서 엔티티는 영속성 컨텍스트에서는 제거됩니다.  
  
#### 변경 감지(Dirth Checking)  

엔티티 매니저는 Update를 위한 인터페이스가 존재하지 않습니다.  
영속성 컨텍스트로부터 엔티티를 조회한 시점에 스냅샷을 찍은 뒤,  
flush 시점에 엔티티와 스냅샷을 비교하여 엔티티의 변경여부를 체크하여 업데이트 여부를 결정합니다.  
단, 변경 감지 기능은 영속 상태의 엔티티에 한해서만 적용됩니다.  
만약 영속화 후 close, detach로 엔티티를 준영속 상태로 변경했다면 이 후 엔티티의 값을 변경해도 변경 감지는 동작하지 않습니다.  
참고로 변경 감지는 기본적으로 엔티티의 모든 필드에 대해 업데이트를 진행합니다.  
만약 필드가 10개라고 할 때 1개의 필드값만 변경된다 하더라도 10개 필드에 대한 Updade SQL문이 생성됩니다.  
이 경우 데이터베이스에 전송하는 데이터량은 증가할 수 있지만 한가지 큰 장점이 존재합니다.  
전체 필드를 업데이트하므로 필드에 종속적인 SQL을 생성하지 않아도 됩니다.  
이것은 변경 감지가 적용될 때마다 SQL문을 만들 필요가 없고, 미리 만들어진 SQL을 호출하면 됨을 의미합니다.  
실제로 JPA는 애플리케이션이 로딩 될 때 Update Query를 미리 만들어두고 변경 감지가 적용될 때마다 같은 Query를 재사용합니다.  
데이터베이스도 동일한 Query가 요청되면 이를 캐싱하여 재사용하므로 성능상 이득을 볼 수 있습니다.  
  
#### 지연 로딩  

연관 관계가 이루어진 엔티티에 대해 조회할 필요가 없는경우 불필요한 Join을 줄여 조회 성능이 향상됩니다.  
해당 엔티티에 직접적으로 접근할 때 SQL이 호출됩니다.  
예를들어 엔티티A와 B가 매핑되어 있을 때 각각 1000개씩 저장되어 있다고 가정하겠습니다.  
A의 정보만 조회하고 싶은데, 즉시로딩을 사용한다면 최대 1,000,000 건의 조회가 이루어지겠지만(모든 B가 A와 연관된 경우 가정) 지연 로딩을 사용하여 A데이터 1000건만 조회할 수 있습니다.   
지연로딩은 JPA가 영속성 컨텍스트로부터 엔티티를 조회할 때 프록시를 사용하여 연관관계로 매핑된 해당 엔티티를 상속받은 프록시 객체를 반환함으로써 이루어집니다.  
이에 대한 자세한 내용은 다음번 파트에서 자세히 설명드리도록 하겠습니다.     

--- 

### Flush 원리  
영속성 컨텍스트와 데이터베이스의 동기화를 수행하는 flush를 실행시키는 방법은 몇 가지가 있습니다.  
1. 엔티티 매니저가 flush() 메서드를 직접 호출  
  거의 사용되지 않는 방식이지만 간혹 테스트에서 사용합니다.  
2. 트랜잭션 커밋과정에서 flush 자동 호출  
  JPA는 트랜잭션이 커밋 될 때 먼저 flush를 호출합니다.  
  ~~아마도 AOP를 이용해서 트랜잭션 커밋시점을 뺏은 후 flush를 먼저 실행하게하고 트랜잭션을 커밋하겠지?~~  
3. JPQL 실행 시 flush 자동 호출  
  JPQL은 실행 시점에 SQL로 변환한 뒤 데이터베이스로부터 엔티티를 조회합니다.  
  만약 JPQL이 실행되기 전에 영속화된 엔티티가 있고 커밋이 실행되기 전이라고 가정하면,  
  해당 엔티티는 flush가 되지 않았으므로 영속성 컨텍스트에만 존재하고 데이터베이스에는 존재하지 않습니다.  
  이러한 경우에 JPQL이 해당 엔티티의 조회 Query를 실행했다면, 영속성 컨텍스트에 저장되어있음에도 불구하고  
  데이터베이스에 존재하지 않으므로 엔티티를 조회하지 못하는 문제가 발생할 수 있습니다.  
  JPA는 이러한 문제가 발생하지 않도록 JPQL이 실행될 때 flush를 자동으로 호출하여  
  SQL 쓰기지연 저장소에 있는 Query들을 먼저 데이터베이스로 전송한 뒤 JPQL을 실행합니다.   
  
---

### 병합 (MERGE)  
준영속 상태의 엔티티는 다시 영속화 될 수 있는데 이 때에는 persist가 아닌 merge를 사용합니다. 
Spring-Data-JPA를 사용할 경우 save() 메서드는 아래와 같이 분기를 통해 persist와 merge를 결정합니다.  
```java
public class SimpleJpaRepository<T, ID> implements JpaRepositoryImplementation<T, ID> {
    // ... 코드 생략
	@Transactional
	@Override
	public <S extends T> S save(S entity) {

		Assert.notNull(entity, "Entity must not be null.");

		if (entityInformation.isNew(entity)) {
			em.persist(entity);
			return entity;
		} else {
			return em.merge(entity);
		}
	}
}
```
isNew를 통해 해당 엔티티의 식별자를 체크한 뒤 식별자가 없거나 0인경우엔 persist, 그 외에는 merge가 실행됩니다.  
즉 요청온 엔티티의 상태로 구분짓는것이 아니라 식별자의 유무로 구분지으므로 비영속 상태의 엔티티도 merge가 실행될 수 있습니다.  

**여기서 한가지 큰 차이점이 있는데 입력으로 들어온 엔티티와 반환될 엔티티의 동일성의 차이가 있습니다.**  

persist는 식별자 생성 전략에 따라 식별자를 처리한 후 영속성 컨텍스트에 엔티티를 저장합니다.  
그리고 해당 엔티티를 반환합니다.  
따라서 영속화를 요청한 엔티티와 반환된 엔티티가 동일합니다.  
하지만 merge의 경우 영속화 후 영속성 컨텍스트에 저장된 새로운 엔티티를 반환합니다.  
따라서 준영속 상태였던 엔티티와 영속화된 엔티티는 서로 다른 엔티티이므로 동일성이 보장되지 않습니다.  

__이런 이유로 인해 merge를 요청했던 엔티티는 merge 이후에도 여전히 준영속 상태이므로 영속성 컨텍스트의 기능을 사용할 수 없습니다.__  

[persist vs merge 참고](https://jiminidaddy.github.io/dev/2020/10/20/dev-jpa-difference-persist-merge)  


