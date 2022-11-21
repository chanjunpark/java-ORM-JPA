package jpql;

import javax.persistence.*;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        // code
        try {

            Member member = new Member();
            member.setUsername("member1");
            member.setAge(10);
            em.persist(member);

            TypedQuery<Member> query1 = em.createQuery("select m from Member m", Member.class);
            //Query query2 = em.createQuery("select m.username, m.age from Member m", Member.class);

            // 여러개 가져올 때
            List<Member> resultList = query1.getResultList();

            for (Member member1 : resultList) {
                System.out.println("member1 = " + member1);
            }

            // 하나만 출력할 때
            // Spring Data JPA 에서는 Optional 이나 null 을 반환하고 예외를 던지지 않음
            Member singleResult = query1.getSingleResult();


            tx.commit();
        } catch (Exception e) {
            System.out.println("Exception() : " + e.getCause());
            tx.rollback();
        } finally {
            System.out.println("em.close()");
            em.close();
        }

        emf.close();
    }
}
