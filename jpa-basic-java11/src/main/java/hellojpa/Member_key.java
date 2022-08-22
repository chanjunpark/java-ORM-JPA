package hellojpa;

import javax.persistence.*;

@Entity
@TableGenerator(
        name = "MEMBER_SEQ_GENERATOR",
        table = "MY_SEQUENCES",
        pkColumnValue = "MEMBER_SEQ", allocationSize = 1)
public class Member_key {

    /**
     * @GeneratedValue
     * IDENTITY : 기본 키 생성을 데이터베이스에 위임 ex) MySQL auto_increment
     * SEQUENCE : Sequence Object에서 값을 가져와 PK 설정 ex) Oracle sequence
     * TABLE : 키 생성 전용 테이블을 만들어서 DB sequence를 흉내내는 전략
     */

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue(strategy = GenerationType.TABLE,
            generator = "MEMBER_SEQ_GENERATOR")
    private Long id;
    //private String id;

    private String username;

    public Member_key() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
