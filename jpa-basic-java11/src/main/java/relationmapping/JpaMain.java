package relationmapping;

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
            // N:1 저장하는 코드

            System.out.println("====START====");

            Team team = new Team();
            //team.setId(1L);
            team.setName("Blue Jays");
            em.persist(team);

            System.out.println("[Debug] ====save Team====");


            Member member = new Member();
            //member.setId(99L);
            member.setName("Hyunjin Ryu");
            member.setTeam(team);
            em.persist(member);

            System.out.println("[Debug] ====save Member====");

            em.flush();
            em.clear();

            Member findMember = em.find(Member.class, member.getId());

            //Team findTeam = findMember.getTeam();

            //System.out.println("[Debug] findTeam = " + findTeam.getName());

            List<Member> members = findMember.getTeam().getMembers();

            for (Member m : members) {
                System.out.println("[Debug] member = " + m.getName());
            }

            System.out.println("[Debug] ==== FINISH ====");
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
