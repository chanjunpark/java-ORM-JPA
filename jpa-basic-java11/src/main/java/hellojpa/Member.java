package hellojpa;

import javax.persistence.*;
import java.util.Date;

@Entity
// @Table(name = "USER") -> DB의 테이블 이름과 객체 명이 다른 경우 @Table 이용해 지정 가능
public class Member {

    @Id
    private Long id;

    @Column(name = "name") // 객체에는 username을 쓰고 싶은데, DB TABLE attribute 명이 name인 경우
    private String username;

    private Integer age;

    @Enumerated(EnumType.STRING) // DB에는 Enum 타입이 존재하지 않기 때문에 @Enumerated 어노테이션을 활용
    private RoleType roleType;

    @Temporal(TemporalType.TIMESTAMP) // 날짜 타입은 @Temporal을 사용. Date, Time, Timestamp 중에 선택해서 설정해줘야함
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    @Lob // varchar를 넘어가는 큰 Contents를 저장하기 위해선 @Lob 사용
    private String description;
}
