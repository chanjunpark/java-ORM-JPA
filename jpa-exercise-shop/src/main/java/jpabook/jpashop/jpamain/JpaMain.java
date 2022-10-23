package jpabook.jpashop.jpamain;

import jpabook.jpashop.domain.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.time.LocalDateTime;

public class JpaMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        // code
        try {

            System.out.println("===start===");

//            System.out.println("===사용자생성===");
//            Member member = new Member("user_A");
//            em.persist(member);
//            System.out.println("===사용자저장===");
//
//            System.out.println("===상품생성===");
//            Item item = new Item("티셔츠");
//            em.persist(item);
//            System.out.println("===상품저장===");
//
//            System.out.println("===주문생성===");
//            Order order = new Order(member, LocalDateTime.now());
//            em.persist(order);
//            System.out.println("===주문저장===");
//
//            System.out.println("===주문-상품생성===");
//            OrderItem orderItem = new OrderItem(item);
//            em.persist(orderItem);
//            System.out.println("===주문-상품저장===");
//
//            System.out.println("===save orderItem===");
//            order.addOrderItem(orderItem);

            Book book = new Book();
            book.setName("JPA");
            book.setAuthor("김영한");

            em.persist(book);


            System.out.println("===finish===");
            tx.commit();

        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }

}
