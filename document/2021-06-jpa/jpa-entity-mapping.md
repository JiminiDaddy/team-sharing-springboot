# Entity Mapping  

테이블과 엔티티는 비슷해 보이고 같은 것 처럼 사용하지만 분명 서로 다른 존재입니다.  
따라서 JPA를 사용해서 데이터베이스에 접근하려면 서로 다른 엔티티와 테이블의 관계를 정확히 매팽하는것이 중요합니다.  
테이블과 엔티티는 엔티티(Java object)와 테이블의 매핑, 식별자(PK) 매핑, 필드와 컬럼의 매핑 등을 사용하여 매핑이 가능합니다.  

---

## 테이블과 엔티티 매핑  

### @Entity  

클래스에 @Entity 애너테이션을 선언하면 해당 클래스는 JPA가 엔티티로 관리하게 됩니다.  
엔티티 이름은 기본적으로 클래스 이름을 따르지만 아래와 같이 name 옵션을 통해 변경이 가능합니다.  
```java
@Entity(name = "Members")
public class Member {
    // ...
}
```  

단, 엔티티를 정의할 때 주의사항이 있습니다.  
#### 반드시 기본 생성자가 정의되어야 합니다.  
JPA는 지연로딩이라는 방식을 사용할 수 있는데 이 경우 엔티티를 상속받아 프록시를 구현합니다.  
따라서 public이나 protected 접근제한자로 기본 생성자가 정의되어야 합니다. (private은 상속 불가능)
final 클래스도 안되며 (상속 불가능하므로), enum이나 interface, inner class에서도 사용 불가능합니다.  

---

### @Table  

엔티티와 매핑할 테이블을 설정합니다.  
기본적으로 테이블 이름은 엔티티 이름을 갖게되지만 name 속성으로 테이블 이름을 명시적으로 지정할 수 있습니다.  
uniqueConstraints 옵션을 통해 UNIQUE 제약 조건 설정도 가능합니다.  
unique 제약조건은 각 필드마다 설정해줄 수도 있지만 2개 이상의 복합 조건은 불가능하므로 이 경우 클래스에 선언하여 사용하면 됩니다.  
아래 코드는 Member 엔티티를 Members라는 테이블명으로 매핑해주고, 이름과 나이에 복합 Unique 제약조건을 추가한 예제입니다.  
```java
@Table(name = "Members", uniqueConstraints = {
    @UniqueConstraint(
        name = "NAME_AGE_UNIQUE",
        columnNames = {"name", "age"}
    )
})
@Entity
public class Member {
    // ... 코드 생략
}
```

---

## 식별자 매핑  

### @Id  

영속성 컨텍스트는 엔티티를 식별자로 구분지어 관리하므로 반드시 식별자가 명시적으로 선언되어야 합니다.  
선언된 엔티티의 식별자는 데이터베이스의 기본 키(Primary Key)로 매핑됩니다.  
JPA는 다양한 식별자 생성 전략을 제공하는데 @GenerateValue로 설정이 가능합니다.   

### @GeneratedValue  
TABLE, SEQUENCE, IDENTITY, AUTO 4가지 방식의 식별자 자동생성 기능을 제공합니다.  
자동매핑을 사용하지 않고 수동으로 식별자 생성도 가능합니다.  
단 이 경우 애플리케이션에서 식별자가 중복되지 않도록 관리하여야 합니다.  
아래 코드는 AUTO 방식으로 식별자를 생성한 예제입니다.  
```java
// ... 생략
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    // ... 생략
}
``` 
#### 식별자 매핑 시 주의사항  

식별자는 자바의 기본 타입과 참조 타입 모두 사용할 수 있습니다.  
하지만 데이터베이스에 생성된 스키마에 따라 엔티티와 매핑이 실패할 수 있으므로 주의해야 합니다.  
PK 및 UK 등 not null 옵션을 통해 강제로 설정할 순 있지만 데이터베이스 테이블 컬럼의 기본 타입은 nullable입니다.  
자바의 경우 참조 타입은 nullable이지만 기본 타입의 경우 not null입니다.  
만약 데이터베이스 테이블의 컬럼이 nullable인데 엔티티의 필드가 기본 타입으로 매핑될 경우 불일치로 인해 애플리케이션 구동이 실패할 수도 있습니다.  
또한 아래와 같이 필드에 @Column을 적용할경우에 문제가 발생할 소지가 있습니다.  

```java
public class Member {
    @Id
    @Column(name = "member_id")
    private long id;
    // ... 생략
}
```  
위 예제에서 식별자 id는 자바 기본 타입이므로 항상 not null입니다.  
따라서 개발자는 Member 엔티티에 매핑된 테이블의 PK도 not null이고 엔티티의 식별자도 not null로 예상하게 됩니다.  
하지만 @Column을 사용할경우 옵션으로 nullable 여부를 설정해주는데 기본값이 true (null 허용) 입니다.  
분명 기본 타입은 not null임에도 불구하고 @Column 선언으로 인해 식별자가 nullable이 되어 문제가 발생할 수도 있게 됩니다.  
따라서 기본 타입을 식별자로 사용할 땐 아래와 같이 명시적으로 @Column에 옵션을 not null로 설정해주는게 좋습니다.

```java
public class Member {
    @Id
    @Column(name = "member_id", nullable = false)
    private long id;
    // ... 생략
}
```  

--- 

#### IDENTITY  

애플리케이션에서 식별자를 생성하지 않고 데이터베이스로 위임합니다.  
데이터베이스가 순서대로 값을 할당해주기 때문에 중복이 발생하지 않습니다.  
데이터베이스에 데이터를 저장해야만 기본 키가 생성되므로, 엔티티의 식별자를 조회하려면 반드시 데이터베이스로 SQL을 전송해야 합니다.  
따라서 IDENTITY 전략을 사용하게되면 트랜잭션 쓰기지연을 사용할 수 없습니다.  

--- 

#### SEQUENCE  

데이터베이스에서 순서대로 유일한 값을 생성해주는 오브젝트를 사용하기때문에 시퀀스를 지원되는 데이터베이스에만 사용이 가능합니다.  
Oracle, PostgreSQL, DB2, H2는 지원하고 있습니다.  
아래와 같은 구문으로 시퀀스를 생성하고 @GeneratedValue의 옵션 중 generator에 할당합니다.  
```sql
CREATE SEQUENCE MEMBER_SEQ START WITH 1 INCREMENT BY 1;
```

```java
@SequenceGenerator(
        name = "MEMBER_SEQ_GENERATOR",
        sequenceName = "MEMBER_SEQ",
        initialValue = 1,       // 초기값
        allocationSize = 1      // 증가량
)
@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MEMBER_SEQ_GENERATOR")
    private long id;
    // ... 생략
}
```  
먼저, DB에서 SEQUENCE Object를 생성합니다.  
Entity에서는 @SequenceGenerator를 통해 생성된 SEUQNCE Object와 매핑할 Generator을 생성합니다.  
식별자의 생성 전략으로 SEQUENCE로 설정하고, generator는 위에 생성된 Generator로 설정합니다.  

SEQUENCE 전략도 IDENTITY 전략처럼 엔티티의 식별자를 생성하기위해 먼저 데이터베이스에 요청해야합니다.  
하지만 IDENTITY는 persist요청이 올 때 flush도 같이 실행되므로 바로 INSERT SQL을 데이터베이스로 전송하여 생성된 기본키를 가져와 식별자로 매핑하지만  
SEQUENCE는 SEQUENCE Object로부터 가져온 기본키를 엔티티의 식별자에 매핑합니다. (엔티티 저장할 때 INSERT SQL 전송하지 않음)    
이후 트랜잭션 커밋이 발생하면 (flush 시점) INSERT SQL을 전송하여 데이터베이스에 저장하게 됩니다.  

--- 

#### TABLE  

SEQUENCE가 지원되지 않는 데이터베이스에서 SEQUENCE를 흉내낸 방식입니다.  
SEQUENCE Object와 같은 역할을 수행하는 테이블을 하나 생성한 뒤 해당 테이블의 컬럼으로부터 기본키를 조회하고, 업데이트 하는 방식입니다.  
내부 동작은 SEQUENCE 방식과 동일합니다. (기본키로부터 식별자를 조회하여 엔티티에 저장하며, 실제 데이터베이스 반영은 트랜잭션 커밋 시점에서 진행)  
JPA가 INSERT SQL을 전송할 때, 테이블의 값을 초기화하므로 값이 없어도 자동으로 INSERT됩니다.  

SEQUENCE와 차이점이 하나 있습니다.  
테이블의 NEXT_VAL값은 INSERT가 진행될 때 마다 설정한 allocationSize만큼 증가해야 합니다.  
SEQUENCE는 데이터베이스 내부적으로 값을 갱신하겠지만 TABLE은 개발자가 직접 추가하였으므로, 업데이트의 책임이 애플리케이션에 있습니다.  
따라서 데이터가 추가될 때 마다 TABLE이 업데이트 되야하므로, 매번 UPDATE SQL이 전송되어 성능에 영향을 주게 됩니다.  
만약 UPDATE SQL을 데이터가 저장 될 때 마다 매번 전송하지 않고 일정 간격에 도달할 때마다 주고 싶다면 allocationSize를 설정해야 합니다.  
(SEQUENCE 전략도 동일합니다.)  

```sql
CREATE TABLE TB_SEQUENCE (
  SEQUENCE_NAME VARCHAR(128) NOT NULL,
  NEXT_VAL BIGINT,
  PRIMARY KEY (SEQUENCE_NAME)
)
```

```java
@TableGenerator(
        name = "MEMBER_SEQ_GENERATOR",
        table = "TB_SEQUENCE",
        pkColumnValue = "MEMBER_SEQ", allocationSize = 1
)
@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "MEMBER_SEQ_GENERATOR")
    private long id;
    // ... 생략
}
```  

---  

#### AUTO  

AUTO 전략은 특별히 다른 기능이 있는것은 아니며, 데이터베이스에 따라 IDENTITY, SEQUENCE, TABLE 전략 중 하나가 자동으로 선택됩니다.  
예를들어 MySQL을 사용하는데 AUTO 전략을 설정했다면 IDENTITY로 변환됩니다.  
사용할 데이터베이스가 변경되어도 코드가 변경되지 않는 장점이 있지만, TABLE이나 SEQUENCE 전략을 사용할 경우 미리 테이블이나 오브젝트를 생성해야 합니다.  

식별자는 엔티티를 구분해주는 값이므로 가급적 not null로 생성하며, 중복을 허용해서 안됩니다. 또한 불변을 유지해야 합니다.  

---

## 필드와 컬럼 매핑  

훨씬 많은 옵션들이 제공되겠지만 자주 사용할 수 있는 옵션에 대해 말씀드립니다.  

### @Column  

엔티티의 필드와 테이블의 컬럼을 매핑합니다.  
기본적으로 필드명과 컬럼명을 동일하게 구성하지만 name 속성을 통해 컬럼명을 별도로 설정할 수 있습니다.  
예를들어 관례상 테이블의 컬럼 네이밍은 SNAKE 방식을 사용하지만 Java는 CAMEL 방식을 사용합니다.  
null 허용여부, unique 제약조건, insert/update 허용여부, 길이 제한 등등 다양한 속성을 제공합니다.    

### @Enumerated  

Enum 필드를 컬럼에 매핑하며 2가지 방식을 제공합니다.  
EnumType.ORDINAL: Enum에 정의된 순서대로(0, 1, 2, ...) 데이터베이스에 저장합니다. (기본값, 숫자형)  
EnumType.STRING: Enum 이름을 데이터베이스에 저장합니다. (문자형)  

### @Temporal, @CreatedDate, @LastModifiedDate  

날짜/시간 속성의 필드를 컬럼과 매핑합니다.  
@Temporal는 Date 객체를 DATE, TIME, TIMESTAMP 3가지 속성으로 매핑합니다.  
LocalDateTime은 Java8에서 등장하였으므로 2013년에 릴리즈된 JPA2.1 이하 버전에서는 LocalDateTime을 사용할 수 없습니다.  

@CreatedDate, @LastModifiedDate는 생성시간과 갱신시간을 자동으로 구성하여 매핑해줍니다.  
단, 저 Annotation뿐 아니라 JPA의 자동설정 관련한 추가적인 작업이 필요합니다.  
해당 필드가 선언된 엔티티나 상위 클래스에 @EntityListeners(AuditingEntityListener.class) 옵션을 추가해야하며, 
메인 클래스에서 @EnableJpaAuditing 을 선언해야 합니다. 
예시 코드는 아래와 같습니다.  
```java
@EnableJpaAuditing
@SpringBootApplication
public class EntityMappingApplication {
    // ...
}

@EntityListeners(AuditingEntityListener.class)
public class Member {
    // ...
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime modifiedAt;
}
```
예제 코드에서는 엔티티에 @CreatedDate, @LastModifiedDate를 추가하였는데, 보통 실무에선 이렇게 사용하진 않습니다.  
생성시간, 갱신시간의 경우 여러 도메인에서 사용하는 값이므로 이런 필드를 공통으로 정의한 상위 클래스를 만들어 사용합니다.  
그리고 해당 기능이 필요한 엔티티들이 클래스를 상속받는 방식으로 구현합니다.  

### @Lob  

데이터베이스의 BLOB, CLOB 컬럼과 매핑합니다.  
CLOB은 String, char[]과 매핑이 되며 BLOB은 byte[]와 매핑됩니다.  
    
### @Transient  

엔티티에서만 사용하고 데이터베이스에는 저장하고 싶지 않은 임시 값으로 사용합니다.  
@Transient가 사용된 필드는 테이블이 자동생성 옵션으로 생성될 때 DDL문에서 제외됩니다.  

### @Access  

JPA가 엔티티의 필드에 접근하는 방식을 정의하는것으로, 필드 접근방식과 프로퍼티 접근 방식이 있습니다.  
@Access(AccessType.FIELD)  
JPA가 필드에 직접 접근하는 방식으로 접근제한자에 영향을 받지 않습니다.  
@Access(AccessType.PROPERTY)  
JPA가 Getter와 같은 메서드를 통해 간접적으로 필드에 접근합니다.  

@Access가 설정되어 있지 않다면 @Id가 선언된 위치에 따라 Access가 결정됩니다.  

아래와 같이 필드에 선언되어 있다면 필드 방식이 적용됩니다.  
```java
class Member {
    @Id
    private Long id;
}
``` 

아래와 같이 메서드에 선언되어 있다면 프로퍼티 방식이 적용됩니다.  
```java
class Member {
    @Id
    public Long getId() {
        return id;
    }
}
```  

---  

### Personal Comment  
JPA에서 제공해주는 Annotation들이 엄청 많다.  
사실 평소에 잘 사용하지 않으면 머리가 좋지 않은 이상 자꾸 까먹게 된다.  
작년에 처음 JPA를 공부했을 때 분명 코드도 많이 작성해보고, 책도 보고, 기술 블로그들도 많이 보았지만 계속해서 쓰지 않으면 자꾸 잊어먹게된다.  
특히 Annotation안에 있는 속성들은 1,2개 빼곤 자꾸 까먹는다.  
그러므로 계속해서 만져보고 실제로 테스트해보면서 눈으로 결과를 보는게 꼭 필요하다고 생각한다.  

연관관계에 들어가면 더 많은 Annotation들이 있을텐데 이 부분을 어떻게 정리해야 할지 고민이다.  
사실 필요한 부분만 딱 정해서 '~~이렇게 사용하면 됩니다.' 로 하면 분량이 많지 않을 수 있다.  
하지만 내부 동작에 대해 들어가기 시작하면.. 정말 JPA가 어려워지는 것 같다.  

이번 장을 정리하면서 식별자 생성 전략과 Unique와 같은 제약조건을 함께 적용했을 때의 테스트 코드를 작성해보았는데  
분명 공부했던 부분에도 불구하고 한 30분간 헤맷던 것 같다.  
덕분에 IDENTITY 전략방식에 대해 좀더 확실히 이해할 수 있었고, 트랜잭션 쓰기 지연에 대해서도 더욱 잘 이해할 수 있었다.  
정말 테스트는 너무너무너무 중요한 것 같다.  
함께 공부하시는 분들도 꼭 공부한 내용을 테스트 코드로 작성하여 눈으로 직접 확인하셨으면 좋겠다.  
 

