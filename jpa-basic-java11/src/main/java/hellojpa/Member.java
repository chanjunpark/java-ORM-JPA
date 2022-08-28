package hellojpa;

import javax.persistence.*;
import java.util.Date;

// @Entity
// @Table(name = "USER") //DB의 테이블 이름과 객체 명이 다른 경우 @Table 이용해 지정 가능
// @Table(uniqueConstraints = {}) // UNIQUE 제약조건 생성가능
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
    @Column(name = "name", nullable = false) // 객체에는 username을 쓰고 싶은데, DB TABLE attribute 명이 name인 경우
    private String username;

    private Integer age;

    /**
     * @Enumerated 사용 시 주의사항
     * 기본 값인 ORDINAL을 사용하면 안 됨 : integer 타입으로 설정되는데, ENUM에 새로운 값이 추가 되는 경우 순서가 바뀌기 때문에 큰 문제가 생길 수 있음
     * 따라서, 반드시 STRING으로 사용 : varchar 타입으로 설정되고, ENUM 값이 그대로 들어감.
     * */
    @Enumerated(EnumType.STRING) // DB에는 Enum 타입이 존재하지 않기 때문에 @Enumerated 어노테이션을 활용
    private RoleType roleType;

    /**
     * @Temporal
     * Java 8 이후 거의 사용할 일이 없어짐
     * LocalDate : Hibernate가 자동으로 DB date type으로 생성해줌
     * LocalDateTime : Hibernate가 자동으로 DB timestamp type으로 생성해줌
     */
    @Temporal(TemporalType.TIMESTAMP) // 날짜 타입은 @Temporal을 사용. Date, Time, Timestamp 중에 선택해서 설정해줘야함
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    /**
     * @Lob
     * 데이터베이스 BLOB, CLOB 타입과 매핑됨. 따로 지정할 수 있는 속성은 없음
     * CLOB : 매핑하는 필드 타입이 문자인 경우
     * BLOB : 나머지
     */
    @Lob // varchar를 넘어가는 큰 Contents를 저장하기 위해선 @Lob 사용
    private String description;

    @Transient // DB에서 관리하지 않고 memory 에서만 사용하고 싶을 때
    private int temp;

    public Member() {
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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }
}
