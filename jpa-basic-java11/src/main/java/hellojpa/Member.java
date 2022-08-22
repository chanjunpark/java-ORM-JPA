package hellojpa;

import javax.persistence.*;
import java.util.Date;

@Entity
// @Table(name = "USER") -> DB의 테이블 이름과 객체 명이 다른 경우 @Table 이용해 지정 가능
public class Member {

    @Id
    private Long id;

    /**
     * @Column option
     * insertable, updatable : 기본은 true, 등록만 하고 이후 변경 금지하고 싶은 경우엔 updatable을 false로 설정
     * nullable : not null 제약 조건을 사용하고 싶을 때
     * unique : unique 제약 조건 사용하고 싶을 때 -> 잘 안 쓰임(제약조건 이름이 임의로 생성되기 때문에) -> Entity Class에 설정해주는게 좋음
     * columnDefinition
     * length : 길이 제약 조건
     * */
    @Column(name = "name") // 객체에는 username을 쓰고 싶은데, DB TABLE attribute 명이 name인 경우
    private String username;

    private Integer age;

    /**
     * @Enumerated 사용 시 주의사항
     *
     * */
    @Enumerated(EnumType.STRING) // DB에는 Enum 타입이 존재하지 않기 때문에 @Enumerated 어노테이션을 활용
    private RoleType roleType;

    @Temporal(TemporalType.TIMESTAMP) // 날짜 타입은 @Temporal을 사용. Date, Time, Timestamp 중에 선택해서 설정해줘야함
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    @Lob // varchar를 넘어가는 큰 Contents를 저장하기 위해선 @Lob 사용
    private String description;

    @Transient // DB에서 관리하지 않고 memory 에서만 사용하고 싶을 때
    private int temp;

    public Member() {
    }
}
