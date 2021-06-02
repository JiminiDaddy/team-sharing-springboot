package entitymapping;

import entitymapping.domain.Member;
import entitymapping.domain.MemberRepository;
import entitymapping.domain.Role;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

@DataJpaTest
class EntityMappingTest {
    @Autowired
    private MemberRepository memberRepository;


    @Test
    @DisplayName("Member 등록")
    void addMember() {
        Member member = new Member("user1");
        member.setAge(34);
        member.setEmail("user1@test.com");
        member.setDescription("JPA는 어렵고 재미있다");
        member.setRole(Role.ADMIN);

        Member savedMember = memberRepository.save(member);

        Assertions.assertThat(savedMember).isSameAs(member);
        Assertions.assertThat(savedMember.getId()).isNotNull();
        Assertions.assertThat(savedMember.getEmail()).isSameAs("user1@test.com");

        Assertions.assertThat(memberRepository.count()).isEqualTo(1);

        try {
            Thread.sleep(1000);
            savedMember.login();
            savedMember = memberRepository.save(savedMember);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("created:  " + savedMember.getCreatedAt());
        System.out.println("modified: " + savedMember.getModifiedAt());
        System.out.println("logined : " + savedMember.getLoginAt());
        Assertions.assertThat(savedMember.getLoginAt().isAfter(savedMember.getModifiedAt()));
    }

    @Test
    @DisplayName("이름과 나이가 같은 사람은 중복이 될 수 없다")
    void notAllowDuplicatedNameAndAge() {
        Member member1 = new Member("user1");
        member1.setAge(34);
        member1.setEmail("user1@test.com");
        Member savedMember = memberRepository.save(member1);

        Member member2 = new Member("user1");
        member2.setAge(34);
        member2.setEmail("user2@test.com");

        //memberRepository.deleteById(savedMember.getId());

        Member savedMember2 = memberRepository.save(member2);

        memberRepository.findById(1L);

        // Member 엔티티는 이름+나이를 UNIQUE로 설정하여 중복을 방지하였다.
        // save()만 호출될경우 flush()되지 않으므로 위에 save()가 2번 호출될 때까지 예외가 발생하지 않는다. (DB에는 반영되지 않았으므로)
        // 하지만 findAll이 호출되면 JPA가 SQL을 전송하므로 Insert문 2개가 DB로 전송되고, 제약조건에 의해 예외가 발생한다.
        // 따라서 아래 코드는 예외가 발생해야 정상적인 코드다.
        // findAll뿐만 아니라 findByName, count 등 다른 메서드들도 SQL을 전송하므로 예외가 발생한다.

        // 그러면 왜 memberRepository.findById(1L); 와 같은 메서드는 예외가 발생하지 않는걸까?
        // 여기서부턴 내 가설이지만
        // 영속성 컨텍스트는 key=엔티티 식별자, value=엔티티 인스턴스로 1차 캐시를 구성한다.
        // 영속성 컨텍스트에서 엔티티를 조회하려면 식별자가 있어야 한다. (key:value 방식의 Map인데 당연히 key로 검색해야겠지?)
        // 따라서 식별자로 조회할경우 영속성 컨텍스트에서 해당 엔티티가 있는지/없는지 바로 확인가능하므로 있는경우 DB 호출없이 반환된다.
        // 물론 식별자를 통해 조회하지 못할경우 DB에서 조회할것이다.
        // 하지만 식별자가 아닌 다른 값으로 조회하려면 해당 값을 갖고있는 엔티티를 찾아야하는데 방법이 없으므로 DB를 조회하여 모든 데이터를 가져온다.
        // 따라서 SQL 쓰기지연 저장소에 모아두었던 INSERT 문 2개가 우선적으로 전송될것이고 중복방지 제약조건에 의해 예외가 발생할 것이다.

        // 여기서 의문이 하나 들었다.
        // Map은 values()를 통해 값들만 조회하는것이 가능한것으로 알고 있다.
        // 그럼 DB에서 조회하기전에 영속성 컨텍스트 내에 있는 엔티티들 인스턴스로부터 식별자가 아닌 값으로도 조회가 가능하지 않을까?
        // 위 예제에선 분명 조회하려는 이름+나이로 저장된 엔티티가 있을텐데 왜 거기서 안가져오고 DB로 보냈을까?

        // 위에거쓰고 30초뒤에 잠깐 생각난건데
        // UNIQUE이지만 PrimaryKey가 아니라서 그런가?
        // 분명 중복되지 못하니까 PK처럼 유일하긴하겠지만 어찌됫든 식별자는 아니니까 DB에서 혹시모를 데이터를 조회해야되니까 그런건가?
        // 하긴 그럴수 있을 것 같다. 제약조건은 중간에 생기거나 삭제될 수 있으니까 기존 데이터에도 무결성이 유지될지/안될지는 모르니까..
        // 일단 이렇게 이해하고 넘어갈거같은데 음.. 테스트하기도 어렵고 누가 좀 시원하게 가르쳐주면 좋겠다..
        Assertions.assertThatThrownBy(() -> {
            for (Member member : memberRepository.findAll()) {
                System.out.println("findMember: " + member.getName() + " " + member.getAge() + " " + member.getEmail());
            }
        }).isInstanceOf(DataIntegrityViolationException.class);
    }
}
