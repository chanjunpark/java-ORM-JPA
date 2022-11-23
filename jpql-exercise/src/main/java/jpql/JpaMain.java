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
            
            Team team = new Team();
            team.setName("teamA");
            
            em.persist(team);
    
            Member member = new Member();
            member.setUsername("member1");
            member.setAge(10);
            member.setTeam(team);
    
            em.persist(member);
    
            String query = "select m from Member m inner join m.team t";
            List<Member> resultList = em.createQuery(query, Member.class)
                    .getResultList();
    
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
