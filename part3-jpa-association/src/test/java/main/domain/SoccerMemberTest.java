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
}