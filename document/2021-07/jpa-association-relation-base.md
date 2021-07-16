# JPA 연관관계

## 연관관계의 필요성  

데이터베이스는 두 테이블이 연관관계를 맺기위해 FK(Forign key)와 PK(Primary Key)를 사용한다.  
예를들어 축구선수와 축구팀이라는 테이블이 있고, 한 선수는 하나의 팀에 소속된다고 가정하겠다.  
축구선수로부터 소속팀을 조회하는 SQL은 아래와 같이 작성될 수 있다.  

```sql
SELECT T.NAME 
FROM SOCCER_TEAM T 
JOIN SOCCER_MEMBER M ON T.ID = M.TEAM_ID
WHERE M.NAME = '손흥민';
``` 

위와같은 결과를 객체로 구현한다면 아래의 절차가 필요할 것이다.  
<소속팀 등록>  
1. 팀 객체를 생성한다.
2. 소속팀 이름을 설정한다.
3. 팀을 영속화한다.  
4. 선수 객체를 생성한다.
5. 선수의 이름을 설정한다.  
6. (3) 결과 생성된 소속팀Id를 선수의 팀Id로 설정한다.  
7. 선수를 영속화한다.  
 
<소속팀 조회>  
1. 축구선수 이름으로부터 선수 정보를 조회한다.
2. 축구선수 정보로부터 팀 ID를 추출한다.
3. 팀 ID로부터 소속팀 정보를 조회한다.

위 절차를 코드로 나타내보면 아래와 같이 작성될 수 있겠다.  

```java
@Repository
public class SoccerMemberRepository {
	@PersistenceContext
	private EntityManager em;
	
    public void saveTeam(String teamName, String memberName) {
    	SoccerTeam team = new Team();
    	team.setName(teamName);
    	em.persist(team);
    	SoccerMember member  = new Member();
    	member.setName(memberName);
    	member.setTeamId(team.getId());
    	em.persist(member);
    }
    
     public Team findTeam(Long id) {
        SoccerMember member = em.find(SoccerMember.class, id);
        Long teamId = member.getTeamId();
        SoccerTeam team = em.find(SoccerTeam.class, teamId);
    }
}
```

팀을 조회할 때, 굳이 선수에 저장된 팀Id를 추출해서 다시 팀을 조회하는 일이 조금 불편하지않을까?  
객체는 참조를 통해 다른 객체로 메시지를 보냄으로써 협력관계를 맺게되는데, 위 방식은 참조를 맺지않기때문에 객체지향적이지 못한 문제가 있다.  
객체를 조회해서 Id를 꺼낸 후 다시 객체를 조회하는.. Id꺼내는 불필요한 작업이 발생한다.  

연관관계를 맺게되면 위와같이 Id를 통해 객체들을 연결시키지 않고, 참조를 통해 객체 그래프 탐색이 가능해진다.  
선수가 갖고있는 팀Id를 통해 다시 팀을 조회하지 않고, 그냥 조회한 선수에 매핑된 팀을 조회하면 된다.  

JPA는 이러한 연관관계를 통해 엔티티들을 매핑시켜준다.  

연관관계는 크게 단방향과 양방향으로 구성될 수 있으며, 다시 1:1, 1:N, N:1, N:M 과 같은 관계로 매핑될 수 있다.  


## 단방향 연관관계1 (X to 1)

축구선수와 소속팀 예제의경우 여러명의 선수가 하나의 팀에 소속될 수 있으므로, 축구선수 입장에서 선수와 팀의 연관관계는 N:1이다.  

### 객체 연관관계  

단, 축구선수와 소속팀은 단방향 연관관계를 맺습니다.
축구선수 엔티티의 필드로 소속팀 객체가 정의되어 있으므로 축구선수 엔티티는 참조를 통해 소속팀에 접근할 수 있기 때문입니다.    
다시말해 축구선수는 본인이 속한 소속팀을 알 수 있지만, 팀은 어떤 선수들이 소속되어있는지 알 수 없습니다.  

### 테이블 연관관계  

축구선수 테이블은 소속팀 테이블의 PK인 TEAM_ID를 FK로 갖음으로써 두 테이블간의 연관관계가 맺어집니다.  
이 경우 축구선수와 소속팀은 양방향 관계를 갖게되는데, 축구선수 JOIN 소속팀 또는 소속팀 JOIN 축구선수 둘다 가능하기 때문입니다.  

### 객체 연관관계 vs 테이블 연관관계  
위에서 설명했듯이 참조를 통한 객체의 연관관계는 단방향이고 JOIN을 통한 테이블의 연관관계는 양방향입니다.  
물론 소속팀 엔티티에서도 축구선수 엔티티를 필드로 정의하여 등록된 선수들을 관리할 수 있습니다.  
이렇게되면 마치 양방향 관계처럼 보일 수 있지만, 정확히 표현하면 2개의 단방향 관계입니다.  
(축구선수 -> 소속팀, 소속팀 -> 축구선수 라는 2가지 방향이 존재함)  

### 엔티티 예제  

```java
// TODO 축구선수, 소속팀 엔티티 클래스
```

__@ManyToOne__  
N:1 연관관계를 표현하며, 아래와 같은 속성들이 존재합니다.  
optional: 기본값은 true이며 false로 설정하면 연관관계를 맺을 엔티티가 반드시 존재해야 합니다. (필수 생성)  
fetch: 글로벌 패치 전략을 설정합니다. toOne은 기본적으로 즉시로딩이며 toMany는 지연로딩입니다.  
cascade: 영속성 전이 기능을 사용합니다. ALL, PERSIST, REMOVE 등 전이 조건을 설정할 수 있습니다.  

__@JoinColumn(name = "team_id")__  
Foriegn Key를 매핑할 때 사용하며, FK 컬럼명을 name 속성으로 정의합니다.  
name은 생략 가능하지만 생략할경우 {필드명}_{참조할 엔티티의 PK 컬럼명} 으로 자동으로 설정됩니다.  
(예를들어 필드명이 team이고 팀 테이블의 PK가 TEAM_ID이면 FK 컬러명은 TEAM_TEAM_ID가 됩니다.)  

@ManyToOne 연관관계를 맺을 때 optional=false로 설정한다면 반드시 해당 엔티티를 영속화하기전에 연관관계가 맺어진 엔티티를 참조해야하며, 참조할 엔티티는 영속화되어야 합니다.  
만약 참조할 엔티티가 영속화되지 않았다면 아래와 같이 예외가 발생하게 됩니다.  
즉, 연관된 엔티티들은 영속 상태여야합니다.  

```java
@Transactional
@SpringBootTest
class SoccerMemberTest {
	@PersistenceContext
	private EntityManager entityManager;

	@Test
	@DisplayName("선수와 소속팀 생성 및 조회")
	void createAndFiind() {
		SoccerTeam team = new SoccerTeam("토트넘");
		// Member에서 설정한 Team 연관관계에서 optional=false로 줄 경우, Member는 반드시 Team 엔티티를 포함해야 한다.
		// 하지만 아래 코드를 생략하면 team이 영속성 컨텍스트에 없으므로 Member를 영속화할 때 예외가 발생한다.
		//entityManager.persist(team);

		SoccerMember member = new SoccerMember("손흥민", team);
		entityManager.persist(member);

		SoccerMember findMember = entityManager.find(SoccerMember.class, member.getId());

		assertThat(findMember.getId()).isEqualTo(member.getId());
		assertThat(findMember.getName()).isEqualTo(member.getName());
		assertThat(findMember.getTeam().getId()).isEqualTo(team.getId());
		assertThat(findMember.getTeam().getName()).isEqualTo(team.getName());
	}
}
```

![Alt](/images/2021-07-part3-jpa/part3-jpa-association-optional-false-error.png)


### 연관관계의 사용 방법  

__저장__  
아래와 같이 엔티티 객체를 생성한 후, 엔티티 매니저의 persist를 호출하여 영속화합니다.   
```java
public class SoccerMember {
    public void saveMember() {
        SoccerMember member = new SoccerMember("손흥민", team);
        entityManager.persist(member);
    }
}
```

__조회__  
아래와 같이 엔티티 매니저의 find를 호출하여 영속화된 엔티티를 조회합니다.  
```java
public class SoccerMember {
    public SoccerMember findMember(Long id) {
        SoccerMember findMember = entityManager.find(SoccerMember.class, id);
        SoccerTeam findTeam = findMember.getTeam();
        return findMember;
    }
}
```

위 예제는 팀을 조회하기위해 객체 그래프 탐색을 사용했습니다.  
이 외에도 JPQL을 사용해서 SQL과 유사하게 직접 엔티티를 조회하는것도 가능합니다.  
~~JPQL은 이번 범위에 벗어날 것 같으니 자세한 내용은 생략하고 추후 JPQL Part를 작성할 수 있게되면 정리하도록 하겠습니다.~~  

__수정__  
선수가 이적되어 팀을 변경하는 예제를 작성해보겠습니다.
```java
public class SoccerMember {
    public SoccerMember changeTeam(Long id, String teamName ) {
        SoccerTeam newTeam = new SoccerTeam(teamName);
        entityManager.persist(newTeam);
        SoccerMember member = entityManager.find(SoccerMember.class, id);
        member.setTeam(newTeam); 
    }
}
```

위 코드에서는 persist와 영속화 메서드를 사용하지 않았고, update와 같은 메서드 또한 사용하지 않았습니다.  
하지만 위 코드를 실행하면 SQL update 구문이 데이터베이스로 전송됩니다.  
그 이유는 트랜잭션이 시작된 이후에 영속화된 엔티티를 조회할 경우, 해당 엔티티를 스냅샷을 찍어두고 트랜잭션이 커밋될 때 엔티티와 스냅샷을 비교하기 때문입니다.  
스냅샷과 엔티티가 다르다면 엔티티 매니저의 flush가 발생하여 변경감지 기능이 동작하는데, 이 때 update 구문을 전송하게 됩니다.  

__삭제__  
연관관계로 매핑된 엔티티를 null로 참조하게끔 설정하면 연관관계가 제거됩니다.  
```java
public class SoccerMember {
    public SoccerMember removeTeam(Long id) {
        SoccerMember member = entityManager.find(SoccerMember.class, id);
        SoccerTeam team = member.getTeam();
        member.setTeam(null);
        entityManager.remove(team);
    }
}
```

주의할 점은 EntityManager가 remove 메서드로 team을 영속성 컨텍스트에서 제거하기 전에 team을 참조하는 member 엔티티들이 team에 대한 참조를 끊어야 합니다.  

---

## 양방향 연관관계

JPA에서 @ManyToMany를 지원하지만 N:M 양방향 관계는 오히려 로직을 복잡하게 만들 수 있습니다.  
따라서 @OneToMany와 @ManyToOne 2개의 단방향을 통해 양방향을 흉내내는 방식으로 구현합니다.  
기존 예제에서 SoccerMember와 SoccerTeam은 N:1관계를 맺고 있으며,서 SoccerMember 엔티티에서만 연관관계를 매핑했습니다.  
이번에는 SoccerTeam에서 1:N 연관관계를 맺어보도록 하겠습니다.  
SoccerTeam 엔티티 코드에서 아래 필드만 추가하면 됩니다.  
```java
public class SoccerTeam {
	// ..... 생략
	@OneToMany(mappedBy = "team")
	private List<SoccerMember> members = new ArrayList<>();
}
```

mappedBy는 매핑된 엔티티의 필드명을 사용하면 됩니다.  
(SoccerMember 엔티티가 team 이라는 필드명을 통해 SoccerTeam 엔티티를 참조하므로, team 이라고 사용하면 됩니다.)  

### 양방향 연관관계의 규칙  

참고로 1:N, N:1 2가지로 매핑된 연관관계에서 N:1로 매핑하고 있는 엔티티가 연관관계의 주인이 됩니다.  
연관관계의 주인으로 설정된 엔티티는 조회 뿐 아니라 생성,삭제 할 수 있지만 반대쪽 엔티티는 조회만 할 수 있습니다. 
mappedBy는 주인이 아닌 엔티티가 연관관계의 주인을 명시적으로 지정하는 역할을 수행합니다.   

이제 SoccerMember도 SoccerTeam을 참조하며, SoccerTeam도 SoccerMember를 참조하게 됩니다.  
JPA는 List, Set, Map 등 다양한 컬렉션을 지원하는데 목적에 맞는 것을 사용하면 됩니다.  
* proxy와 같은 기능은 컬렉션마다 다르게 동작하여 성능의 이슈가 발생할 수 있으니 반드시 연관관계와 어울리는 컬렉션을 사용해야 합니다.  

연관관계의 주인이 무엇인지 애매하다고 느낀다면 FK를 관리하는 엔티티를 연관관계의 주인이라고 정하면 됩니다.  

### 양방향 연관관계의 주의점  

단방향과 다르게 양쪽 엔티티에서 모두 연관관계로 매핑된 엔티티를 관리하게 되므로 한 가지 주의점이 있습니다.  
주인이 아닌 엔티티에만 값을 저장할 경우, 데이터가 제대로 저장되지 않는 문제가 발생합니다.  
예를들어 소속팀에는 선수1, 선수2 엔티티를 저장하도록 구현하고, 각 선수들 엔티티에는 소속팀을 배정하지 않는다면 선수를 조회할 때 소속팀이 함께 조회되지 않습니다.  

따라서 양쪽 엔티티 모두 연관관계로 매핑된 엔티티에 대해 값을 저장해주어야 합니다.  

하지만 이렇게 항상 양쪽 엔티티에 각각 값을 저장한다면, 당연히 개발자가 실수로 한쪽만 저장하는 실수가 발생할 수 있습니다.  
이런 실수를 최대한 줄이려면 한쪽 엔티티에서 값을 모두 저장하도록 구현하는것이 유리합니다.  

예를들어 아래와 같이 선수 엔티티에 소속팀을 배정하는 메서드를 구현하면 소속팀 엔티티에는 별다른 코드를 구현할 필요가 없습니다.  

```java
public class SoccerMember {
    // ... 코드 생략	
    public SoccerMember(String name, SoccerTeam team) {
        this.name = name;
        this.team = team;
        this.team.addMember(this);
    }
}

public class SoccerTeam {
    // ... 코드 생략	
    public void addMember(SoccerMember member) {
    	members.add(member)	;
    }
}
```

위 코드의 경우 선수 엔티티를 생성할 때, 반드시 소속팀을 배정해야 합니다.  
그리고 생성자에서 소속팀 엔티티의 addMember 메서드를 호출하여 선수 자신을 팀에 등록합니다.  
이렇게 구현하면 소속팀에서 추가적인 코드를 구현할 필요없이 양쪽 엔티티 모두 매핑된 엔티티의 값을 저장하므로 누락되는 문제가 사라집니다.  
이러한 방식을 연관관계 편의 메서드라고 합니다.  

단, 소속팀이 변경될 경우 기존 팀으로 매핑된 관계를 제거해야주어야 합니다.  
예를들어 선수1이 팀A에 소속되었었는데 팀B로 변경한다고 가정하겠습니다.  
위 코드의 경우 선수1이 팀B로만 변경하게 되는데, 기존 팀A의 입장에서는 여전히 선수1을 멤버로 가지고 있으므로 이 관게를 제거해주어야 합니다.  
따라서 선수의 생성자 코드는 아래와 같이 변경해주어야 합니다.

```java
public class SoccerMember {
    // ... 코드 생략	
    public void changeTeam(SoccerTeam team) {
        if (this.team != null) {
            this.team.getMembers().remove(this);
        }
        this.team = team;
        this.team.addMember(this);
    }
}
```

changeTeam 메서드는 선수가 기존에 등록된 팀이 있는경우 팀에 등록된 자신을 제거하고, 새로운 팀에 자신을 등록합니다.  
이 메서드가 호출되지 않는다면 기존팀과 새로운 팀 모두 해당 선수 엔티티를 등록하게되므로 중복된 연관관계가 발생합니다.  

---

### 중요한 사실  

단방향이든 양방향이든 중요한건 연관관계의 주인은 단 하나다.  
(N:1 관계에서 N인 엔티티가 주인이 되는데 그 이유는 FK를 N인 엔티티가 갖고있기때문이다. FK가 없는 엔티티가 주인이 되면 데이터가 추가될 때, 매핑된 엔티티의 FK값을 별도로 업데이트해야하는 불필요한 작업이 발생할 수 있다.)  

양뱡향은 단지 주인이 아닌 엔티티가 주인 엔티티를 참조하여 객체 그래프 탐색을 할 수 있는것일 뿐, 비즈니스 로직을 수행하면 안된다.  

### 무한반복 주의  

양방향 연관관계로 매핑된 엔티티는 서로를 참조하므로 무한루프에 빠질 위험이 언제든 발생할 수 있다.  
A엔티티의 K메서드가 B엔티티의 L메서드를 호출하고, B엔티티의 L메서드가 다시 A엔티티의 K메서드를 호춣한다면 무한루프에 빠질 수 있다.  
이렇게 순환 참조가 발생할 수 있으므로 항상 무한루프가 생기지 않도록 구현에 주의해야 한다.  

---

### Comments  

단순히 엔티티 코드에 Annotation 갖다 붙여 연관관계를 매핑해주는 작업은 얼마 걸리지 않는다.  
하지만 하나하나씩 뜯어가며 로직을 이해하는건 정말 어려운 것 같다.  
이번 글을 작성하면서 테스트 코드를 함께 진행했는데 당연히 아는것이라고 생각했던 것들도 한 depth만 더 깊이 들어가도 당황하였다.  
~~이럴땐 역시 시간 투자해서 이해갈때까지 계속 실험해보는게 좋은 것 같다. 단 시간(삽질)이 많이 들어간다..~~  
 


