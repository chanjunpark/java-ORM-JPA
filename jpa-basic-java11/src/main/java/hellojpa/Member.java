package hellojpa;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
// @Table(name = "USER") -> DB의 테이블 이름과 객체 명이 다른 경우 @Table 이용해 지정 가능
public class Member {

    @Id
    private Long id;
    //@Column(name = "username") -> DB의 컬럼명과 객체의 필드 이름이 다른 경우 @Column 이용해 지정 가능
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
