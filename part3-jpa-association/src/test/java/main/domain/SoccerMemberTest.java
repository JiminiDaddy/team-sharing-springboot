package main.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.assertThat;

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
		entityManager.persist(team);

		SoccerMember member = new SoccerMember("손흥민", team);
		entityManager.persist(member);

		SoccerMember findMember = entityManager.find(SoccerMember.class, member.getId());

		assertThat(findMember.getId()).isEqualTo(member.getId());
		assertThat(findMember.getName()).isEqualTo(member.getName());
		assertThat(findMember.getTeam().getId()).isEqualTo(team.getId());
		assertThat(findMember.getTeam().getName()).isEqualTo(team.getName());
	}

	@Test
	@DisplayName("선수와 팀 양방향 연관관계 설정")
	void createAndFind2() {
		SoccerTeam team = new SoccerTeam("바르샤");
		entityManager.persist(team);

		SoccerMember member1 = new SoccerMember("메시", team);
		SoccerMember member2 = new SoccerMember("피케", team);
		entityManager.persist(member1);
		entityManager.persist(member2);

		SoccerMember findMember1 = entityManager.find(SoccerMember.class, member1.getId());
		SoccerMember findMember2 = entityManager.find(SoccerMember.class, member2.getId());

		assertThat(findMember1.getId()).isNotNull();
		assertThat(findMember2.getId()).isNotNull();
		// SoccerMember 엔티티에만 팀을 설정하고, SoccerTeam에는 별다른 액션을 취하지 않았지만 양방향 연관관계가 정상적으로 매핑되었다.
		assertThat(findMember1.getTeam().getName()).isEqualTo(team.getName());
		assertThat(findMember2.getTeam().getName()).isEqualTo(team.getName());
	}
	@Test
	@DisplayName("소속팀 변경")
	void changeTeam() {
		SoccerTeam team1 = new SoccerTeam("바르샤");
		entityManager.persist(team1);

		SoccerMember member1 = new SoccerMember("메시", team1);
		SoccerMember member2 = new SoccerMember("피케", team1);
		entityManager.persist(member1);
		entityManager.persist(member2);

		SoccerMember findMember1 = entityManager.find(SoccerMember.class, member1.getId());
		SoccerMember findMember2 = entityManager.find(SoccerMember.class, member2.getId());

		SoccerTeam team2 = new SoccerTeam("레알마드리드");
		entityManager.persist(team2);
		// 변경 감지에 의해 persist하지않아도 update SQL이 전송된다.
		findMember1.changeTeam(team2);

		assertThat(findMember1.getName()).isEqualTo(member1.getName());
		assertThat(findMember2.getName()).isEqualTo(member2.getName());
		assertThat(findMember1.getTeam().getName()).isEqualTo(team2.getName());
		assertThat(findMember2.getTeam().getName()).isEqualTo(team1.getName());
		// 선수1의 소속팀이 팀1 -> 팀2로 변경되었으므로, 팀1과 팀2 각각 등록된 선수의 수는 1이다.
		assertThat(team1.getMembers().size()).isEqualTo(1);
		assertThat(team2.getMembers().size()).isEqualTo(1);
	}

	@Test
	@DisplayName("소속팀 변경 - 다른 영속성 컨텍스트로 테스트")
	void changeTeam2() {
		SoccerTeam team1 = new SoccerTeam("바르샤");
		entityManager.persist(team1);

		SoccerMember member1 = new SoccerMember("메시", team1);
		SoccerMember member2 = new SoccerMember("피케", team1);
		entityManager.persist(member1);
		entityManager.persist(member2);
		// 영속성 컨텍스트를 제거하였다. 따라서 team1, member1, member2는 영속성 컨텍스트에 존재하지 않는다.
		entityManager.flush();
		entityManager.clear();

		SoccerMember findMember1 = entityManager.find(SoccerMember.class, member1.getId());
		SoccerMember findMember2 = entityManager.find(SoccerMember.class, member2.getId());

		SoccerTeam team2 = new SoccerTeam("레알마드리드");
		// 새로운 영속성 컨텍스트에 team2를 영속화하였다. 따라서 현 영속성 컨텍스트에는 team2 엔티티만 존재한다.
		entityManager.persist(team2);

		// 아래 코드가 주석처리된다면?
		// team1은 현재 영속성 컨텍스트에 존재하지 않으며 준영속 상태이다. (위에서 clear하였으므로)
		// 따라서 아래 findMember1.changeTeam(team2) 가 호출되어 기존 team으로부터 member를 지우려고 시도하지만,
		// findMember1에 매핑된 team 엔티티와 준영속상태인 team1은 서로 다른 객체다.
		// 테스트 코드의 의도는 findMember1이 소속팀을 변경하면 team1 엔티티도 함께 적용되는것이었지만, team1이 준영속 상태의 엔티티이기때문에 이 의도는 실패한다.
		// 따라서 테스트 의도를 성공시키려면 아래와같이 team1 엔티티를 영속성 컨텍스트로부터 조회를 요청하여야 한다.
		// 이렇게되면 team1의 식별자와 findMember1과 매핑된 team의 식별자가 같으므로 영속성 컨텍스트에 존재하는 team(바르샤) 엔티티를 team1이 가리키게 된다.
		team1 = entityManager.find(SoccerTeam.class, team1.getId());

		findMember1.changeTeam(team2);

		assertThat(findMember1.getName()).isEqualTo(member1.getName());
		assertThat(findMember2.getName()).isEqualTo(member2.getName());
		assertThat(findMember1.getTeam().getName()).isEqualTo(team2.getName());
		assertThat(findMember2.getTeam().getName()).isEqualTo(team1.getName());
		// 선수1의 소속팀이 팀1 -> 팀2로 변경되었으므로, 팀1과 팀2 각각 등록된 선수의 수는 1이다.
		assertThat(team1.getMembers().size()).isEqualTo(1);
		assertThat(team2.getMembers().size()).isEqualTo(1);
	}
}