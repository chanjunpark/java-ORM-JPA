package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        // code
        try {
            /**
             * INSERT 쿼리
             */
            //Member member = new Member();
            //member.setId(2L);
            //member.setName("HelloB");
            //em.persist(member);

            /**
             * JPQL 쿼리
             */
            //List<Member> result = em.createQuery("select m from Member as m", Member.class)
            //                .setFirstResult(0) // 페이징 시작위치
            //                .setMaxResults(10) // offset
            //                .getResultList();

            //for (Member member : result) {
            //    System.out.println("member.name = " + member.getName());
            //}

            /**
             * 필드와 컬럼 매핑 - Member Insert 예시
             */
            //Member member = new Member();
            //member.setId(1L);
            //member.setUsername("david");
            //member.setRoleType(RoleType.USER);
            //em.persist(member);

            /**
             *  기본 키 매핑 - Member Insert 예시
             */
            Member_key member = new Member_key();
            member.setUsername("James");
            em.persist(member);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();


    }
}
