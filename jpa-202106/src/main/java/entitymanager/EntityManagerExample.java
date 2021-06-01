package entitymanager;

import entitymanager.domain.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

@SpringBootApplication
public class EntityManagerExample {
    @Autowired
    private EntityManagerFactory entityManagerFactory;

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(EntityManagerExample.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        ConfigurableApplicationContext context = springApplication.run(args);

        EntityManagerExample entityManagerExample = context.getBean(EntityManagerExample.class);
        System.out.println("동일 트랜잭션 내에서 멤버 등록/조회");
        entityManagerExample.crudTestInSameTransaction();
        System.out.println("다른 트랜잭션 내에서 멤버 등록/조회");
        entityManagerExample.crudTestInDiffTransaction();
    }

    private void crudTestInSameTransaction() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            addMember(entityManager, 1L, "user1");
            Member member1 = findMember(entityManager, 1L);
            printMember(member1);

            fixMember(member1, "change_user1");
            Member member2 = findMember(entityManager, member1.getId());
            printMember(member2);
            transaction.commit();
            System.out.println("동일트랜잭션에서 같은Id로 조회한 Entity의 동일성 비교 결과: " + (member1 == member2));

        } catch (Exception e) {
            e.printStackTrace();
        }

        entityManager.close();
    }

   private void addMember(EntityManager entityManager, Long id, String name) {
       Member member = new Member(id, name);
       entityManager.persist(member);
    }

    private void fixMember(Member member, String changeName) {
       member.changeName(changeName);
    }

    private Member findMember(EntityManager entityManager, Long id) {
        return entityManager.find(Member.class, id);
    }

    private void crudTestInDiffTransaction() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Member member1 = findMemberInTransaction(entityManager, 1L);
        printMember(member1);
        entityManager.close();

        entityManager = entityManagerFactory.createEntityManager();
        Member member2 = findMemberInTransaction(entityManager, 1L);
        printMember(member2);
        entityManager.close();

        System.out.println("다른트랜잭션에서 같은Id로 조회한 Entity의 동일성 비교 결과: " + (member1 == member2));
    }

    private Member findMemberInTransaction(EntityManager entityManager, Long id) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Member findMember = entityManager.find(Member.class, id);
            transaction.commit();
            return findMember;
        } catch (Exception e) {
           e.printStackTrace();
        }
        return null;
    }

    private void printMember(Member member) {
        System.out.println(member.toString());
    }
}
