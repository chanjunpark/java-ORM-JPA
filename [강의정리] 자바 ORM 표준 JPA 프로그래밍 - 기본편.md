
## [1] JPA 소개
---
### ✅ SQL 중심적인 개발의 문제점
>  **관계형 데이터베이스의 모델링** 과 **객체지향 모델링** 기법 간의 차이로 인해 객체답게 모델링 할수록 매핑 작업만 늘어나게 되고, 결국 데이터 모델링에 맞춰 프로그램을 설계할 수 밖에 없어진다.

#
### ✅ 객체를 Collection에 저장하듯이 DB에 저장할 수 없을까?
> Java ORM 표준인 JPA를 이용하면 가능하다. JPA는 애플리케이션과 DB 사이에서 동작하며 이 둘을 매핑해주는데, 이를 통해 패러다임의 불일치를 해결하고, SQL 중심적인 개발에서 객체 중심 개발로 나아갈 수 있다.

#
### ✅ JPA의 성능 최적화 기능
 1. 1차 캐시와 동일성(identity) 보장
> - 같은 트랜잭션 안에서는 같은 엔티티를 반환 : 약간의 조회성능 향상
> - DB Isolation Level이 Read Commit 이어도 애플리케이션에서 Repeatable Read 보장
 2. 트랜잭션을 지원하는 쓰기 지연(transactional write-behind)
> - 트랜잭션을 커밋할 때까지 INSERT SQL을 모음
> - JDBC BATCH SQL 기능을 사용해서 한번에 SQL 전송
> - UPDATE, DELETE로 인한 Row lock 시간 최소화
> - 트랜잭션 커밋 시 UPDATE, DELETE SQL 실행하고 바로 커밋
 3. 지연 로딩(Lazy Loading)
> - 지연 로딩 : 객체가 실제 사용될 때 로딩
>> ex. SELECT * FROM MEMBER; SELECT * FROM TEAM;
> - 즉시 로딩 : JOIN SQL로 한번에 연관된 객체까지 미리 조회
>> ex. SELECT M.*, T.* FROM MEMBER JOIN TEAM ...

#
## [2] JPA 시작하기
---
### ✅ JPA 설정 중 중요한 부분
- JPA 설정은 /META-INF/persistence.xml 에 위치해야 함
- javax.persitence로 시작하는 건 JPA 표준속성, hibernate로 시작하는건 하이버네이트 전용 속성임
- **hibernate.dialect 속성** 지정
    > 💡 _데이터베이스 방언이란?_  
    > SQL 표준을 지키지 않는 DB만의 고유한 기능을 방언이라고 하며, JPA는 특정 데이터베이스에 종속되지 않기 때문에 이러한 데이터베이스 방언을 지원해야 한다. 실제 Hibernate는 40가지 이상의 데이터베이스 방언을 지원한다.
    > - MySQLDialect : MySQL SQL 생성
    > - OracleDialect : Oracle SQL 생성
    > - H2Dialect : H2 SQL 생성  
- java 11 오류 해결 : https://www.inflearn.com/questions/13985

### ✅ JPA 구동 방식
- Java의 Persistence 클래스가 META-INF/persistence.xml에 저장된 설정 정보를 조회한다.
- 설정에 따라 EntityManagerFactory를 생성한다.
    > EntityManagerFactory는 하나만 생성해서 어플리케이션 전체에 공유함
- EntityManagerFactory는 EntityManager 들을 생성한다.
    > EntityManager는 쓰레드 간에 공유가 되지 않으며, JPA의 모든 데이터 변경은 트랜잭션 안에서 실행됨

### ✅ JPA 어노테이션
- @Entity : JPA가 관리할 객체
- @Id : 데이터베이스 PK와 매핑 

### ✅ JPQL
- 객체를 대상으로 검색하는 객체 지향 쿼리
- 뒤에서 아주 자세히 다룰 예정이기 때문에 이런걸 사용한다는 정도만 알고 갈 것 

#
## [3] 영속성 관리 - 내부 동작 방식 
---
### ✅ 영속성 컨텍스트(PersistenceContext)
- 영속성 컨텍스트는 논리적인 개념으로 눈에 보이지 않는다.
- JPA는 EntityManager를 통해 PersistenceContext에 접근한다.
- J2SE 에서는 1:1로 매핑되고, J2EE/스프링 같은 컨테이너 환경에선 N:1로 매핑된다.
> **엔티티 생명주기**
> - 비영속 : 영속성 컨텍스트와 전혀 관계가 없는 새로운 상태 
> - 영속 : 영속성 컨텍스트에 관리되는 상태 // em.persist(member)
> - 준영속 : 영속성 컨텍스트에 저장되었다가 분리된 상태 // em.detach(member)
> - 삭제 : 삭제된 상태 // em.remove(member)

> **영속성 컨텍스트의 이점**
> - 1차 캐시 : 조회 성능 개선
>   - 1차 캐시에서 조회해오는 것과 같지만, 성능 향상이 효과는 미미함.
>   - 트랜잭션 단위로 생성되었다가 사라지기 때문에 효과가 크지 않음.
> - 1차 캐시 : 동일성 보장
>   - 1차 캐시로 반복 가능한 읽기(REPEATABLE READ) 등급의 트랜잭션 격리 수준을 어플리케이션 차원에서 제공함. 
> - 트랜잭션을 지원하는 쓰기 지연
>   - 커밋 이전에는 DB에 insert 하지 않다가, commit 시점에 한 번에 쓰기 작업을 수행
> - 변경 감지(Dirty Checking)
>   - setter 연산을 통해 객체의 정보를 변경한 후에 em.update 와 같은 별도의 작업 없이도, 영속성 컨텍스트에서 관리되고 있는 객체의 변경 내용을 감지하여 반영함
> - 지연 로딩(Lazy Loading)

### ✅ 플러시(flush)
> 영속성 컨텍스트의 변경내용을 데이터베이스에 반영함.
> 
> 플러시에는 세 가지 방법이 있음.
> - em.flush() : 직접 호출
> - 트랜잭션 커밋 : 플러시 자동 호출
> - JPQL 쿼리 실행 : 플러시 자동 호출
>   - JPQL 쿼리 실행 시점에 플러시가 호출되지 않는다면, 영속성 컨텍스트에 저장한 이후 커밋 이전에 JPQL을 사용해 해당 객체를 select 하고자 하면 원하는 객체를 찾을 수 없을 것이다. 

> 플러시의 특성
> - 영속성 컨텍스트를 비우지 않음
> - 영속성 컨텍스트의 변경내용을 DB에 동기화 함
> - 데이터 영속성 관리를 위해서는 결국 트랜잭션이라는 작업 단위가 가장 중요하다. 트랜잭션이 커밋되지 않으면 변경내용이 큰 의미가 없기 때문에, 커밋 직전에만 동기화 하면 된다. 따라서 영속성 컨텍스트와 트랜잭션 주기를 맞춰서(영속성 컨텍스트를 생성하며 트랜잭션을 시작하고 커밋하며 컨텍스트를 날리는?) 설계하고 개발해야 한다. 또한, 이런 이유로 JPA를 사용하면서 데이터 동기화를 너무 크게 생각하지 않아도 된다.

### ✅ 준영속 상태(detached)
- 영속 상태의 엔티티가 영속성 컨텍스트에서 분리되고, 영속성 컨텍스트가 제공하는 기능(Dirty check 등)을 사용하지 못하게 됨.
> 준영속 상태로 만드는 법
> - em.detach(entity)
> - em.clear()
> - em.close()

#
## [4] 엔티티 매핑 
---
JPA에서 제일 중요하게 봐야하는 2가지
1. 메커니즘적 측면 : 영속성 컨텍스트, JPA 내부 동작 방식
2. 설계적 측면 : 객체와 관계형 DB를 어떻게 매핑해서 사용하는지 
=> 이번 장에서는 설계적 측면에서 어떻게 엔티티를 매핑하는지 살펴보겠음

### ✅ 엔티티 매핑
- 객체와 테이블 매핑 : @Entity, @Table
- 필드와 컬럼 매핑 : @Column
- 기본 키 매핑 : @Id
- 연관관계 매핑 : @ManyToOne, @JoinColumn
    - ex) Member <-> Team 간의 연관관계를 어떤식으로 매핑해야 하는지

### ✅ 객체와 테이블 매핑
- @Entity가 붙은 클래스는 JPA가 관리하며, 엔티티라고 부른다.
- JPA를 사용해서 테이블과 매핑할 클래스는 반드시 @Entity를 사용한다.
- 주의사항
    - 기본 생성자 필수(파라미터가 없는 public 또는 protected 생성자)
    - final 클래스, enum, interface, inner 클래스 사용 안 됨
    - DB 저장할 필드에 final 사용하면 안 됨
- @Entity
    - option: name
    - default는 클래스명과 같고, 보통은 사용하지 않지만 다른 패키지에 같은 이름을 가진 클래스가 존재하는 경우 구분을 위해 사용할 수 있음
- @Table
    - option : name
    - @Table option으로 지정해준 이름을 가진 테이블과 엔티티를 매핑해줌

### ✅ 데이터베이스 스키마 자동 생성
- 어플리케이션 로딩 시점에 자동으로 DDL을 생성함
    - 운영에선 절대 create, create-drop, update 사용하면 안 됨
    - 개발환경에서 가볍게 사용할 수 있음
    - 테이블 중심 -> 객체 중심
- <property name = "hibernate.hbm2ddl.auto" value="create" />
    - value 에 다양한 값을 설정해서 여러 속성으로 활용 가능함
    - 딱히 쓸 일 없을 것 같아서 정리는 안하니 필요하면 강의안 참고

### ✅ 필드와 컬럼 매핑
- @Column
    - 다양한 옵션들
    > - insertable, updatable : 기본은 true, 등록만 하고 이후 변경 금지하고 싶은 경우엔 updatable을 false로 설정
    > - nullable : not null 제약 조건을 사용하고 싶을 때
    > - unique : unique 제약 조건 사용하고 싶을 때 -> 잘 안 쓰임(제약조건 이름이 임의로 생성되기 때문에) -> Entity 단위의 @Table annotation에 uniqueConstraints 설정해주는게 좋음
    > - columnDefinition
    > - length : 길이 제약 조건
- @Enumerated 
    - 사용 시 주의사항
    > - 기본 값인 ORDINAL을 사용하면 안 됨 : integer 타입으로 설정되는데, ENUM에 새로운 값이 추가 되는 경우 순서가 바뀌기 때문에 큰 문제가 생길 수 있음
    > -  따라서, 반드시 STRING으로 사용 : varchar 타입으로 설정되고, ENUM 값이 그대로 들어감.
- @Temporal
    > - Java 8 이후 거의 사용할 일이 없어짐
    > - LocalDate : Hibernate가 자동으로 DB date type으로 생성해줌
    > - LocalDateTime : Hibernate가 자동으로 DB timestamp type으로 생성해줌
- @Lob
    > - 데이터베이스 BLOB, CLOB 타입과 매핑됨. 따로 지정할 수 있는 속성은 없음
    > - CLOB : 매핑하는 필드 타입이 문자인 경우
    > - BLOB : 나머지
- @Transient
    > - 매핑하고 싶지 않을 때 사용
    > - DB에서 관리하지 않고 memory 에서만 사용하고 싶을 때

### ✅ 기본 키 매핑
- @GeneratedValue
    > strategy 옵션으로 아래의 값들을 사용할 수 있음
    > - IDENTITY : 기본 키 생성을 데이터베이스에 위임 ex) MySQL auto_increment
    > - SEQUENCE : Sequence Object에서 값을 가져와 PK 설정 ex) Oracle sequence
    >   - SEQUENCE 사용하는 경우 필드 타입이 숫자형 이어야 하는데, Long을 쓰는걸 추천함. Integer의 경우 10억을 넘어가면 사용하기가 힘든데, Long을 씀으로써 발생하는 성능저하가 운영환경에서 Data type을 변경하는 작업의 어려움에 비하면 trade-off 에서 이점이 있기 때문임.
    >   - Entity 단위에서 @SequenceGenerator 를 이용해 테이블마다 별도의 sequence 객체를 생성할 수도 있음
    >   - allocationSize 를 늘려(default = 50), DB I/O time을 줄일 수 있음. 매번 nextval 를 하지 않고 한번 접근할 때 50개를 생성하고 memory에 올려두고 사용하다가 모두 소진되면 다시 DB에 접근해서 50개를 가져오는 방식. 여러 대의 서버에서도 동시성 이슈 없이 사용 가능함.
    >   - Q... 근데 이거 동시성 문제는 없을 지라도 Key가 중구난방으로 생성되는 문제는 생길 수 있는거 아닌감??
    > - TABLE : 키 생성 전용 테이블을 만들어서 DB sequence를 흉내내는 전략
    >   - 장점 : 모든 데이터베이스에 적용 가능함
    >   - 단점 : 성능(운영에서 사용하기엔 부담스러움)
- 권장하는 식별자 전략(PK 설정 전략)
    > - Long + 대체키 + 키 생성전략 사용
    > - 결론
    >   1. auto_increment나 sequence 중 사용하거나
    >   2. 랜덤 값을 조합한 회사 내의 채번 Rule에 따른 값 사용
    > - 절대 비즈니스 유효값을 Key로 사용하지 말자 ex) 주민등록번호를 회원 테이블의 Key에 쓰지 말자

### ✅ 실전 예제 1 - 요구사항 분석과 기본 매핑
- SpringBootApplication으로 실행하는 경우 스프링프레임워크가 java의 카멜케이스 표기법을 따른 변수를 DB의 언더스코어 표기법으로 자동으로 바꿔줌(기본 설정)
- 처음 매핑한 코드를 보면 뭔가 이상함을 알 수 있다
    - Order를 찾아와서, 해당 주문을 요청한 고객을 찾고 싶다면, Order table을 key로 조회하고, 결과로 얻은 Order 객체에서 member ID를 받아와 다시 Member 테이블에서 고객을 찾아와야 한다.
    - 데이터 중심 설계의 문제점
        - 객체지향적으로 설계되지 않고, 객체 설계를 테이블 설계에 맞춘 방식
        - 테이블의 외래키를 객체에 그대로 가져옴
        - 객체 그래프 탐색이 불가함
        - 참조가 없기 때문에 UML도 잘못됨
    - 다음 시간부터는 이러한 데이터 중심 설계의 문제점을 해결하기 위한 연관관계 매핑을 살펴보도록 하겠음

#
## [5] 연관관계 매핑 기초
---

### 학습목표
    - 객체와 테이블 연관관계의 차이를 이해
    - 객체의 참조와 테이블의 FK를 매핑
    - 용어의 이해
        - 방향 : 단방향, 양방향
        - 다중성 : N:1, 1:N, 1:1, N:M
        - 연관관계의 주인(Owner) : 객체 양방향 연관관게는 관리 주인이 필요함

### ✅ 단방향 연관관계
- 단순히 FK 저장해두고, 해당 FK 이용해 다른 테이블을 조회하는 게 아니라 보다 객체지향적인 관점에서 어플리케이션을 설계할 수 없을까?
    - 어플리케이션 설계에서 가장 어려운 부분임. 객체가 지향하는 패러다임과 관계형 DB가 지향하는 패러다임이 다르기 때문에 둘 간의 차이에서 오는 어려움이 있음
    - 한번 잘 배워두면 도움이 될 주제임
- 객체지향 설계의 목표는 자율적인 객체들의 협력 공동체를 만드는 것이다.
    - 기존 데이터베이스 모델 기반의 설계로는 협력관계를 반영하기 어려움
- @ManyToOne
>   - N:1 관계에서 N 쪽에 붙여줌
>   - @JoinColumn(name="")
>       - name 속성을 이용해 참조하는 테이블의 어떤 값을 이용해 매핑해줄지를 결정함(FK-PK 관계와 유사)
>   - 객체를 바로 꺼내쓸 수 있게 됨

### ✅ 양방향 연관관계와 연관관계의 주인1 - 기본
- JPA 기본 강의에서 가장 중요한 내용임
- 기존 단방향 연관관계와 테이블 모델링에는 아무런 변화가 없음
    - why? 테이블의 모델링에서는 FK 하나로 양방향 연관관계가 성립하기 때문임
- 문제는 객체 모델링임
    - 이전 예제에서 Member는 Team을 필드로 가졌고, Member에서는 Team의 정보를 불러올 수 있었음.
    - 그러나, Team은 Member를 가지지 못했기 때문에, Team에서는 Member를 참조하는 것이 불가능 했음.
    - 따라서, Team에 List<Members>를 넣어줘야지만 양방향 참조가 가능해짐.
- 결론 : 객체 모델링과 데이터(테이블) 모델링의 가장 큰 차이는 객체의 경우 양방향 참조를 위해서는 각각의 객체에 연관관계를 포함시켜줘야 하지만, 테이블의 경우 FK 만을 이용해도 양방향 연관관계 표현 및 참조가 가능함.
- @OneToMany(mappedBy = "")
>   - mappedBy 옵션에 반대편 Side의 객체에서 참조되는 변수명을 값으로 지정해주면 양방향 연관관계 매핑이 이뤄짐
>   - 이를 통해 반대 방향으로도 객체 그래프 탐색이 가능해짐
- 연관관계의 주인과 mappedBy
    - mappedBy : JPA 멘탈붕괴 난이도
    > - 앞서 살펴본 바와 같이 객체와 테이블이 관계를 맺는 방식에는 차이가 존재한다. 테이블은 1개의 연관관계 FK만을 갖고도 양방향 참조가 가능하지만, 객체의 경우 단방향 연관관계 두개를 각각 지정해줘야 양방향 참조가 가능해진다.
    > - 다시 말해, 객체의 양방향 관계는 사실 양방향 관계가 아닌 서로 다른 단방향 관계 2개를 의미한다. 반면, 테이블은 FK 하나로 두 테이블 간의 연관관계를 관리한다.
    > - 그럼 2개의 단방향 관계 중 어느걸로 테이블의 FK를 관리해야 할까? 연관관계의 주인(Owner)를 지정해줘야 한다.
    - 연관관계의 주인(Owner)
    > - 객체의 두 관계 중 하나를 연관관계의 주인으로 지정해준다.
    > - 연관관계의 주인만이 외래키를 관리한다(등록 및 수정 가능)
    > - 연관관계에서 주인이 아닌 쪽은 읽기만 가능하다.
    > - 주인은 mappedBy 속성을 사용하지 않는다.
    > - 주인이 아닌 쪽에서 mappedBy 속성을 이용해 주인을 지정한다.
    - 주인을 지정하는 방법
    > - 데이터(테이블) 모델에서 외래키를 가지는 테이블과 매핑된 객체를 주인으로 정해라
    > - 강의 예제에서는 Member.team이 연관관계의 주인이 된다.
    > - 양방향 매핑 시 연관관계의 주인에 값을 입력해야 한다.

### ✅ 양방향 연관관계와 연관관계의 주인2 - 주의점, 정리
- 순수 객체 상태를 고려해서 항상 두 객체 모두에 값을 설정해야 한다. 
    - EntityManager에 등록된 정보가 DB에 저장되고, DB조회를 통해 새로 객체를 생성하는 경우 문제가 없다. JPA에서 연관관계 FK를 통해 양방향 탐색이 가능하도록 설정을 맞춰주기 때문에.
    - 반면, 아직 DB에 저장되지 않은 상태로 EntityManger에 남아있는 캐시에서 객체를 불러와서 양방향 탐색을 시도한다면? 연관관계의 주인인 객체(Member)에서는 소속된 팀을 세팅하고 조회할 수 있지만, 연관관계의 주인이 아닌 객체(Team)에서는 소속 선수를 조회할 수 없다. → 데이터가 꼬여서 이상하게 보이는 상황이 발생함.
    ```java
    Team team = new team();
    Member member = new member();
    ...
    // 연관관계 매핑은 Member의 FK 값을 이용해 되어 있지만 값 설정은 두 객체 모두에 해줘야 함
    member.setTeam(team);
    team.getMembers().add(member)
    ```
- 연관관계 편의 메서드를 생성하는게 좋다.
    - 위에서 살펴본 바와 같이 순수 객체 상태를 고려하면 연관관계의 주인 설정과는 별개로 항상 두 객체 모두에 값을 설정해줘야 하는데, 이걸 매번 빼먹지 않고 하기가 힘들기 때문!
    - 💡 How? Entity(Domain) layer에서 메서드를 생성해준다. 
    - 🚨 연관관계 주인을 설정하는 것과는 별개이니 헷갈리지 않도록 주의한다.
    ```java
    @Entity
    public class Member {
        
        @Id @GeneratedValue
        @Column(name = "MEMBER_ID")
        private Long id;

        ...

        @ManyToOne
        @JoinColumn(name = "TEAM_ID")
        private Team team;

        /**
        * 한쪽에서만 값을 변경해도 양쪽에 값이 설정됨
        * 반대쪽(Team)에 addMember(Member member) 메서드 추가해줘도 됨
        * 양쪽에 다 설정해두면 문제가 될 수도 있으니 상황에 따라 적절한 편의 메서드를 작성하고 반대쪽 객체는 작성하지 않는다 → 잘못하면 무한루프 걸림
        */
        public void changeTeam(Team team) {
             this.team = team; // 메서드를 호출한 객체의 팀을 설정
             team.getMembers().add(this); // team에서 불러온 멤버 리스트에 메서드를 호출한 객체를 추가
        }
    
    }
    ```
    - 기존 리스트에서 null 체크하고, 객체를 삭제하고 새로운 객체를 넣어주는 등의 복잡한 작업까지는 강의에서 안 다루고, 궁금한 경우 책을 보면 도움이 된다. 실무에서는 강의 내용 정도까지만 알아도 큰 무리없음.
- 양방향 매핑 시 무한루프를 조심하자
    - 예) toString(), lombok, JSON 생성 라이브러리
    - IntelliJ Constructor 이용해 toString() 생성하는 경우
    ```java
    @Entity 
    public class Member {
        @Override
        public String toString() {
            return "Member{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", team=" + team + // 여기서 team 넣는다는 건 이 때 team.toString()을 또 호출한다는 의미임
                    '}';
        }
    }
    ```
    - 위와 같은 코드를 작성한 뒤 Team에도 똑같이 toString()을 작성하려고 하면 Team에서도 Member의 toString()을 호출하는 메서드가 생성됨
    - 따라서 양쪽에서 서로 반대편 객체의 toString()을 무한으로 호출하게 됨 → StackOverFlow 발생함
    - 언제 자주 발생하나? Controller에서 Entity를 바로 반환하는 경우 Json 변환 과정에서 위와 같은 무한루프 생기는 경우가 대다수임
    - 어떻게 해결하나? 
        1. Lombok에서 제공하는 toString() 사용을 지양해야함.
        2. Controller에서 절대 Entity를 그냥 반환하지 말아야 함.
            - Entity를 Json으로 반환할 때 루프 문제가 생길 수 있다.
            - Entity는 언제든 변경가능한데, 엔티티를 그대로 반환하면 Entity가 변경될 때마다 API spec이 따라서 바뀌게 된다.
            - 💡 따라서 Entity는 DTO로 변환해서 반환하는걸 추천함!!!
- 🚨 양방향 매핑 정리
    - **단방향 매핑만으로도 이미 연관관계 매핑은 완료된 것이다**
    > - 즉, 단방향 매핑으로 설계는 완료되어야 함! (처음에는 양방향 매핑 하지말 것)
    > - 양방향 매핑은 반대 방향으로 조회할 수 있는 기능(객체 그래프 탐색)이 추가되는 것 뿐이다.
    > - 설계 이후 개발하다 보면 JPQL을 이용해 역방향으로 탐색할 일이 많다.
    > - 단방향 매핑을 잘 하고 양방향은 필요할 때 추가해도 된다.
    >    - JAVA 코드만 수정하면 되고 Table 설계는 변경되지 않기 때문.

    - 연관관계의 주인을 정하는 기준
    > - 비즈니스 로직을 기준으로 연관관계의 주인을 선택하면 안 됨.
    > - 연관관계의 주인은 외래 키의 위치를 기준으로 정해야 함.
    >   - 비즈니스 요구사항이 반대의 작업(예. Team에 Member를 추가)을 많이 요구한다면 앞에서 공부한 편의 메서드를 작성하는 방식으로 풀어내면 됨.

### ✅ 실전 예제 2 - 연관관계 매핑 시작
- 설계 단계에서 객체 매핑 시에 적당한 연관관계 선에서 끊어내는 것도 중요함
    > 💡 특정 회원의 주문목록을 모두 보여주고 싶다면?  
    > 이미 ORDER가 member_id 정보를 FK로 갖고 있기 때문에, 집계 연산을 하더라도 ORDER 테이블에서 수행하는 게 맞음. 굳이 MEMBER 테이블에서 양방향으로 List<ORDER> 를 바라보는 식으로 매핑해둘 필요는 없음.  
    > 💡 반대 사례로, ORDER 입장에선 ORDER_ITEM 을 역으로 조회할 수 있는게 좋음  
    > 비즈니스적으로 가치있는 연관관계임. 주문과 연관된 상품 목록을 조회하는 경우가 많기 때문에.  
- 실무에서는 복잡한 JPQL 작성을 하기 위해 양방향 연관관계를 사용하게 되는 경우가 많음

#
## [6] 다양한 연관관계 매핑
---

### ✅ 연관관계 매핑시 고려사항 3가지
- 다중성
    > - @ManyToOne, @OneToMany, @OneToOne, @ManyToMany
    > - 헷갈릴 수도 있지만 JPA가 제공하는 다양한 어노테이션은 결국 DB 와 매핑을 위해 존재하기 때문에, 데이터베이스 관점의 관계(다중성)를 기준으로 생각하면 됨. 
    > - 관계가 헷갈릴 때는 역관계를 생각해보면 됨. 예) 회원-팀이 헷가릴 땐 팀-회원 관계를 생각해보기(항상 역이 성립하기 때문)
    > - @ManyToMany는 실무에서는 사용하면 안 됨. 왜 쓰면 안되는지는 뒤에서 설명할 예쩡
- 단방향, 양방향
    > - 테이블 : FK 하나로 양방향 조인 가능, 방향의 개념이 없음
    > - 객체 : 참조 필드가 있는 쪽으로만 참조 가능, 한쪽만 참조가능하면 단방향, 양쪽이 서로 참조하면 양방향
- 연관관계의 주인
    > - 위 차이로 인해 객체에서는 연관관계의 주인이 필요해진다.

### ✅ 다대일[N:1]
- Team(1) : Member(N) 예시
- 연관관계의 주인이 되는 객체에서 테이블의 FK(TEAM_ID)에 해당하는 필드(TEAM)에 @ManyToOne을 사용해서 객체를 매핑 (@JoinColumn(name="TEAM_ID"))
- 반대편에서도 연관관계를 맺고 싶을 때는 테이블 컬럼에 변경 없이 List members 와 @OneToMany(mappedBy="team") 만 추가해주면 됨.

### ✅ 일대다[1:N]
- 권장하진 않지만 표준으로 제공되는 스펙이기 때문에 다뤄볼 예정
- 객체와 테이블의 차이 때문에 반대편 테이블의 외래 키를 관리하는 특이한 경우(객체는 1이 연관관계의 주인 테이블은 항상 N 쪽에 외래키가 있음)
- @JoinColumn을 꼭 사용해야 함. 그렇지 않으면 조인 테이블 방식을 사용함(중간 테이블 새로 생성).
- 다대일 관계에서 양방향 매핑을 추가해서 사용하는 편이 더 나음
    > - 객체지향 설계 관점에서 조금 손해를 보더라도 DB 설계 관점에서 유지보수가 용이해짐(trade-off 관계)
- 일대다 매핑의 단점
    > - 엔티티가 관리하는 FK가 다른 테이블에 있음
    > - 연관관계 관리를 위해 추가로 update 쿼리 실행
  
### ✅ 일대일[1:1]
- 다대일 매핑과 마찬가지로 FK 가 위치한 곳이 연관관계의 주인이 됨
- 어느 쪽에 FK를 둘지 정답은 없지만 비즈니스 로직에서 자주 사용하게 되는 쪽(많이 Access 하게 되는 테이블 = 주테이블)에 FK를 두는게 편함(영한님 생각)
    > - 장점 : 주 테이블만 조회해도 대상 테이블에 데이터가 있는지 확인 가능(예. Member만 조회해도 회원이 Locker를 보유했는지 별도의 Join 없이 확인이 가능함)
    > - 단점 : 값이 없으면 외래 키에 null을 허용
- DB 모델링 관점에선 대상 테이블에 FK를 두는편을 선호할 수 있음
    > - 장점 : 주 테이블과 대상 테이블의 관계가 일대일에서 일대다로 변경될 때 테이블 구조를 유지할 수 있음
    > - 단점 : 프로시 기능의 한계로 지연로딩으로 설정해도 항상 즉시 로딩됨

### ✅ 다대다[M:N]
- 관계형 DB는 정규화된 테이블 2개로 다대다 관계를 표현할 수 없음
- 연결 테이블을 추가해서 일대다, 다대일 관계로 풀어내야 함
- 객체는 컬렉션을 사용해서 객체 2개로 다대다 관계가 가능함
    - @ManyToMany, @JoinTable 을 통해서 매핑이 가능하긴 함.
    - 그러나, 사용해서는 안 됨...!! -> 일대다, 다대일로 풀어내자!!
- 사용하면 안 되는 이유
    - 실무에서는 연결 테이블에 여러 정보가 담기게 됨(실제 비즈니스는 생각보다 매우 복잡하다)
- 💡 다대다 한계를 극복하는 방법 : 일대다, 다대일로 풀어내기
    - 연결테이블을 @Entity로 승격
    - DB 설계 관점에서 보면 연결테이블 만들 때 양 쪽의 PK 2개를 FK를 가져와서 결합한 복합키로 PK를 설정할 수도 있지만, 경험상 PK는 GeneratedValue로 사용하는 것이 좋음. -> 나중에 유연성이 생김 :-)

### ✅ 실전 예제 3 - 다양한 연관관계 매핑
- 다대다(@ManyToMany) 실전에서는 사용하지 말자
- @ManyToOne의 주요속성인 fetch, cascade 는 뒤에서 자세히 다룰 예정임

#
## [7] 고급 매핑 
---

### ✅ 상속관계 매핑
- 관계형 DB에는 상속 관계가 없음
- 단, 슈퍼타입 서브타입 관계를 맺는 모델링 기법이 객체 상속과 유사
- 상속관계 매핑 : 객체의 상속과 DB 논리모델 상의 슈퍼타입,서브타입 관계를 매핑
- DB 슈퍼타입, 서브타입의 물리모델 모델링 전략
    1. 조인 전략 : 각각 테이블로 변환(완전한 정규화)
        - 장점
            > - 데이터가 정규화되어 있음
            > - FK 참조 무결성 제약조건 활용가능(외부 도메인에서 슈퍼타입 테이블 하나만 보면 됨)
            > - 저장공간 효율화
        - 단점
            > - 조회 시 조인을 많이 사용(성능 저하)
            > - 조회 쿼리가 복잡함
            > - 저장 시 INSERT SQL 2번 호출
    2. 단일 테이블 전략 : 통합된 하나의 테이블로 변환(하나의 테이블이 모든 서브타입 속성들을 가짐)
        - 장점
            > - 조인이 필요 없으므로 조회 성능이 빠름
            > - 조회 쿼리가 단순함(한 테이블만 보면 됨)
        - 단점
            > - 자식 엔티티가 매핑한 컬럼은 모두 Nullable로 설계해야 함 (데이터 무결성 관점에서 애매함)
            > - 테이블이 커질 수 있고 상황에 따라서 오히려 조회 성능도 저하될 수 있다(단, 이런 일은 잘 없음)
    3. 구현 클래스별 테이블화 전략 : 각각의 서브타입 테이블이 슈퍼타입 속성을 동일하게 가지게끔 변환
        - **이건 쓰면 안 되는 전략임**, DB 설계자와 ORM 전문가 둘 다 추천하지 않는 전략임
        - 장점
            > - 서브타입을 명확하게 구분해서 처리할 때 효과적, Not null 제약조건 사용 가능
        - 단점
            > - UNION SQL 사용으로 여러 자식테이블 함께 조회할 때 성능이 느림
            > - 자식테이블을 통합해서 쿼리를 수행하기 어려움(매출 정산 이런거 어려움...)
- **JPA의 기본 매핑 전략(단순히 @Entity만 사용하는 경우)은 단일 테이블 전략임**
- 주요 어노테이션
    - @Inheritance(stratege=InheritanceType.XXX)
        > - JOINED : 조인 전략
        > - SINGLE_TABLE : 단일 테이블 전략
        > - TABLE_PER_CLASS : 구현 클래스마다 테이블 전략
    - @DiscriminatorColumn(name="DTYPE")
    - @DiscriminatorValue("XXX")
- 데이터 모델이 변경 되어도 코드 수정 없이 어노테이션만 바꿔서 반영이 가능하다 ➡️ JPA의 큰 장점
- 기본적으로 조인 전략을 염두에 두고, 단일 테이블 전략과의 트레이드 오프를 고려하여 선택하면 됨
    - 💡 영한님 Tip 💡 
    > 단순하고, 확장 가능성도 적은 경우에 단일테이블 사용. 비즈니스적으로 중요하고 복잡한 경우 조인전략을 사용

### ✅ Mapped Superclass - 매핑 정보 상속
- 작성자, 작성시간, 최종수정자, 최중수정시간 등의 속성을 모든 테이블에서 같이 사용하고 싶을 때 쓸 수 있는 어노테이션
```java
    
    @Getter @Setter
    @MappedSuperclass
    public abstract class BaseEntity { // 공통 속성을 관리하는 클래스
        
        private String createdBy;
        private LocalDateTime createdDate;
        private String lastModifiedBy;
        private LocalDateTime lastModifiedDate;

    }

    @Entity
    public class Member extends BaseEntity { // 이를사용하는 엔티티

        @Id @GeneratedValue
        @Column(name = "MEMBER_ID")
        private Long id;

        ...

    }
```
- 상속관계 매핑이 아님
- 엔티티가 아니기 때문에 테이블과 매핑되지 않음(테이블과 관계 없고 단순히 엔티티가 공통으로 사용하는 매핑 정보를 모으는 역할)
- 부모 클래스를 상속 받는 자식 클래스에 매핑 정보만 제공
- 조회, 검색 불가(em.find(BaseEntity.class) 불가함)
- 직접 생성해서 사용할 일 없으므로 추상 클래스 권장함 (💡실무에서 유용하게 쓰기 좋음)
- 참고 : @Entity 클래스는 엔티티나 @MappedSuperclass로 지정한 클래스만 상속 가능함

### ✅ 실전 예제 4 - 상속관계 매핑

#
## [8] 프록시와 연관관계 정리
---

### ✅ 프록시
- em.getReference() 는 데이터베이스 조회를 미루는 가짜(프록시) 엔티티 객체를 조회한다.
    > cf) em.find()는 데이터베이스를 통해 실제 엔티티 객체를 조회한다.
- 프록시의 특징 (1)
    - 실제 클래스를 상속받아 만들어짐(하이버네이트가 내부적으로 프록시 라이브러리를 사용해 프록시 객체 생성)
    - 사용하는 입장에선 진짜 객체인지 프록시 객체인지 구분하지 않고 사용하면 됨(이론상)
    - 프록시 객체는 실제 객체의 참조(target)를 보관
    - 프로그램에서 프록시 객체를 호출하면 프록시 객체는 실제 객체의 메소드 호출
- 프록시 객체의 초기화
    1. getName() 메서드 호출
    2. MemberProxy에 target이 비어있는 상태라면, JPA가 영속성 컨텍스트에 이를 조회함
    3. 영속성 컨텍스트가 DB를 조회해서 정보를 가져옴
    4. DB에서 가져온 정보를 갖고 실제 Entity를 생성
    5. target에 실제 Entity를 연결(target.getName()을 통해 사용자의 getName()을 반환)
- 프록시의 특징 (2)
    - 프록시 객체는 처음 사용할 때 한번만 초기화 된다(여러번X). **이 때 프록시 객체가 실제 엔티티로 바뀌는 것은 아님**
    - 프록시 객체는 원본 엔티티를 상속받기 때문에 타입 체크시 주의해야함(== 비교실패, instance of 사용)
    ```java

        Member member1 = new Member();
        member1.setUsername("member1");
        em.persist(member1);

        Member member2 = new Member();
        member2.setUsername("member2");
        em.persist(member2);
        
        Member m1 = em.find(Member.class, member1.getId());
        Member m2 = em.find(Member.class, member2.getId());

        System.out.println("m1 == m2 : " + (m1.getClass() == m2.getClass())); // true 리턴

        // 여기서 만약 m2를 getReference 프록시로 가져온다면?
        Member m1 = em.find(Member.class, member1.getId());
        Member m2 = em.getReference(Member.class, member2.getId());

        System.out.println("m1 == m2 : " + (m1.getClass() == m2.getClass())); // false 리턴

        // 실제로는 아래와 같은 메서드를 이용해 비교가 이루어지기 때문에 인자로 어떤 객체가 들어올지 모르니 타입비교는 == 으로 하지말자 
        private static void checkType(Member m1, Member m2) {
            System.out.println("m1 == m2 : " + (m1.getClass() == m2.getClass()));
        }

        // instanceof 를 활용한 타입 체크
        private static void checkType(Member m1, Member m2) {
            System.out.println("m1 instance of Member : " + (m1 instanceof Member));
            System.out.println("m2 instance of Member : " + (m2 instanceof Member));
        }

    ```
    - 영속성 컨텍스트에 찾는 엔티티가 이미 있으면 em.getReference() 호출 시에도 프록시가 아닌 실제 엔티티 반환
        - 반대도 마찬가지임. 처음에 프록시로 조회하면 em.find() 로 이후에 조회해도 프로시로 반환
        - 💡 tip) 이런 특성으로 인해 개발자 관점에서는 프록시든 실제엔티티든 문제가 생기지 않도록 개발하는 것이 중요함
    - **영속성 컨텍스트의 도움을 받을 수 없는 준영속 상태일 때 프록시를 초기화하면 문제 발생**
        - org.hibernate.LazyInitializationException 예외를 터트림
        - 실무에서 정말 많이 마주하게 됨!
        - 보통 트랜잭션 시작과 끝에 영속성 컨텍스트의 시작과 끝을 맞추게 되는데, 트랜잭션이 끝난 후에 프록시를 조회하려고 하면 Exception이 터지는 경험을 하게 될 것임.
        ``` java
            try {
                ...

                Member refMember = em.getReference(Member.class, member1.getId());  
                System.out.println("refMember = " + refMember.getClass()); // proxy 리턴

                em.detach(refMember); // em.close() em.clear() 등도 동일함.

                // 이후 refMember는 영속성 컨텍스트의 도움을 못받게 됨.

                refMember.getUsername(); //org.hibernate.LazyInitializationException 발생
            
            } catch (Exception e) {
                e.printStackTrace();
            }

        ```
- 프록시 확인
    - 프록시 인스턴스의 초기화 여부 확인 (프록시가 초기화 된 경우 true 그렇지 않은 경우 false)
        - emf.getPersistenceUnitUtil().isLoaded(Object refEntity); // EntityManagerFactory 메서드
    - 프록시 클래스 확인 방법
        - entity.getClass()
    - 프록시 강제 초기화
        - Hibernate.initiallize(refEntity)
        - 참고) JPA 표준은 강제 초기화 없으니 member.getName() 같은 메서드 강제 호출해야 함
- 💡 tip) 프록시(getReference)를 실무에서 직접 사용하는 일은 거의 없다. 다만, 뒤에 이어질 즉시 로딩과 지연 로딩을 깊이 있게 이해하기 위해서는 프록시의 특징을 잘 알고 있어야 한다!!

### ✅ 즉시 로딩과 지연 로딩
- 지연로딩(LAZY)을 사용해서 프록시로 조회
    - 비즈니스 로직에서 Member와 Team을 함께 사용할 일이 적다면 지연로딩 전략이 효율적이다.
    - Member를 조회하며 Team은 프록시로 가져오고, 실제 Team을 사용하는 시점에 이를 초기화(DB조회)
    ```java

        public class Member {
            @Id
            @GeneratedValue
            private Long id;

            @Column(name = "USERNAME")
            private String name;

            @ManyToOne(fetch = FetchType.LAZY)
            @JounColumn(name = "TEAM_ID")
            private Team team;
        }


        Member member = em.find(Member.class, 1L);
        Team team = member.getTeam();

        ...


        team.getName(); // 이 지점에서 초기화(DB조회)

    ```
- 즉시로딩(EAGER)을 사용해서 조회
    - 반면, 비즈니스 로직에서 Member와 Team을 계속 함께 사용할 때는 즉시로딩 전략이 더 효율적이다.
    - Member를 가져올 때 Team까지 조인을 해서 함께 가져옴.
    ```java

        public class Member {
            @Id
            @GeneratedValue
            private Long id;

            @Column(name = "USERNAME")
            private String name;

            @ManyToOne(fetch = FetchType.EAGER)
            @JounColumn(name = "TEAM_ID")
            private Team team;
        }


        Member member = em.find(Member.class, 1L); // 여기서 Member와 Team을 Join해서 한번에 모든 필드를 가져옴

    ```
(💡정말 중요한 내용) 
- **실무에서는 즉시로딩을 사용하면 안 됨!!** ➡️ 즉시 로딩은 상상하지 못한 쿼리가 나간다  
- **모든 연관관계에 지연 로딩을 사용해라!**
- JPQL fetch 조인이나, 엔티티 크래프 기능을 사용하라(뒤에서 설명)
- 이유
    1. 즉시로딩을 사용하면 전혀 예상치 못한 SQL 이 수행됨
    2. 즉시로딩은 JPQL에서 N+1 문제를 야기함
    ```java

        List<Member> members = em.createQuery("select m from Member m", Member.class)
                                .getResultList();
        // 수행된 SQL을 보면 EAGER로 셋팅 된 상태에서도 select가 두번 실행되는걸 확인할 수 있다. 
        // SQL : select * from Member
        // SQL : select * from Team where TEAM_ID = xxx

    ```
    - em.find()는 PK를 찍어서 조회하기 때문에 JPA가 내부적으로 최적화할 수 있음. 반면, JPQL 같은 경우에는 SQL로 번역되어 DB에 나간 이후 추가적으로 필요한 SQL을 한번 더 수행함.
    - N+1 문제를 해결하는 방법 : 모든 연관관계를 지연로딩으로 설정한 뒤 세가지 방법 사용 가능(뒤에 JPQL 파트에서 자세히 다룸)
        1. 패치조인 : 런타임에 동적으로 원하는 애들을 한번에 가져옴 (대표적인 방법)
- @ManyToOne, @OneToOne은 기본이 즉시로딩 ➡️ LAZY로 설정해줘야 함.
- @OneToMany, @ManyToMany는 기본이 지연로딩

### ✅ 영속성 전이(CASCADE)와 고아 객체
- 영속성 전이(CASCADE)
    ```java

       public class Parent {
        
        @Id
        @GeneratedValue
        private Long id;

        private String name;

        @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
        private List<Child> childList = new ArrayList<>();
    
       }
        
    ```
    - 연관관계 셋팅과 아무 상관 없음
    - 특정 엔티티를 영속화하는 과정에서 연관된 엔티티도 함께 영속화하고 싶을 때 사용
    - 소유자가 하나일 때는 상관 없지만, 다른 엔티티와도 연관관계를 맺고 있을 때는 CASCADE를 사용하지 말아야 한다.
    - 사용조건
        1. Parent 와 Child의 Life cycle이 동일할 때
        2. 단일 소유자(Parent만이 Child를 갖고 있을 때)
- 고아 객체
    - 참조가 제거된 엔티티는 다른 곳에서 참조하지 않는 고아 객체로 보고 삭제하는 기능
    - 참조하는 곳이 하나일 때 사용해야 함 (예. 게시판의 첨부파일)
    - 특정 엔티티만(개인) 소유하고 있을 때 사용해야 함
- 영속성 전이 + 고아 객체 개념을 모두 사용하는 경우
    - 스스로 생명주기를 관리하는 엔티티는 em.persist()로 영속화, em.remove()로 제거
    - 두 옵션 모두 활성화 하면 부모 엔티티를 통해 자식의 생명주기를 관리할 수 있음
    - 도메인 주도 설계(DDD)의 애그리거트 루트 개념을 구현할 때 유용함.

#
## [9] 값 타입
---

### ✅ 기본값 타입
- 값 타입이란 ? 단순히 값으로 사용하는 자바 기본 타입이나 객체로 식별자가 없기 때문에 변경시 추적이 불가함.
- 값 타입 분류
    - 기본값 타입 : 자바 기본 타입(int, double), 래퍼클래스(Integer, Long), String
        - 생명주기를 엔티티에 의존 // 예) 회원을 삭제하면 이름, 나이 필드도 함께 삭제
        - 값 타입은 공유하면 안 됨 // 예) 회원 이름 변경 시 다른 회원의 이름도 변경되어서는 안 됨
        - (참고) int, double 같은 primitive type은 항상 값을 복사함. 
        - 반면, Integer 같은 래퍼클래스나 String 같은 특수한 클래스는 공유는 가능하지만 변경은 불가함 
        - 자바에선 공유를 막을 수는 없기 때문에 변경을 불가하게 해서 side effect를 막음

    - 임베디드 타입(embedded type, 복합 값 타입)
    - 컬렉션 값 타입(collection value type)


### ✅ 임베디드 타입
- 임베디드 타입 사용법
    - @Embeddable : 값 타입을 정의하는 곳에 표시
    - @Embedded : 값 타입을 사용하는 곳에 표시
    - 기본 생성자 필수
- 임베디드 타입의 장점
    - 재사용
    - 높은 응집도
        - 예) Period.isWork() 처럼 해당 값 타입만 사용하는 의미있는 메서드를 만들 수 있음
    - 임베디드 타입을 포함한 모든 값 타입은 값 타입을 소유한 엔티티의 생명주기에 의존함 
- 임베디드 타입과 테이블 매핑
    - 객체와 테이블을 아주 세밀하게 매핑하는 것이 가능해짐
    - 잘 설계한 ORM 어플리케이션은 매핑한 테이블의 수보다 클래스의 수가 더 많음
- @AttributeOverride/@AttributeOverrides : 속성 재정의
    - 한 엔티티에서 같은 값 타입을 사용해서 컬럼명이 중복될 때 사용해서 컬럼명 속성을 재정의

### ✅ 값 타입과 불변 객체
- 값 타입 공유 참조
    - 임베디드 타입 같은 값 타입을 여러 엔티티에서 공유하면 위험함 ➡️ 부작용 발생!! (만약 의도했다면 값 타입이 아닌 엔티티를 사용해야 함)
- 객체 타입의 한계 = 객체의 공유 참조(참조 값을 직접 할당하는 것)는 피할 수 없다.
    - 객체의 값을 복사하는 게 아니라 참조를 전달함
    - 기본타입 vs 객체타입
    ```java

        // 기본타입(primitive type)
        int a = 10;
        int b = a; // 기본 타입은 값을 복사
        b = 4; 
        // b의 값만 4로 변경되고, a는 여전히 10을 값으로 가짐

        // 객체타입
        Address a = new Address("Old");
        Address b = a; // 객체 타입은 참조를 전달
        b.setCity("New")
        // b만 New 로 바꾸고 싶었겠지만 a도 New로 변경됨

    ```
- 불변 객체
    - 객체 타입을 수정할 수 없게 만들면 부작용을 원천 차단함
    - 값 타입은 불변 객체(immutable object)로 설계해야함
    - 불변 객체 : 생성 시점 이후 절대 값을 변경할 수 없는 객체
    - 생성자로만 값을 설정하고, 수정자(setter)를 만들지 않으면 됨
    - 참고) Integer, String은 자바가 제공하는 대표적인 불변 객체
- **💡 불변이라는 작은 제약으로 부작용이라는 큰 재앙을 막을 수 있다..!**
    - **값 타입은 무조건 불변으로 만들어야 함**
- 진짜로 값을 바꾸고 싶을 때는 어떻게 해야하나?
    - 새로 객체를 생성해야 함!!! (예들 들면 DB update..?)

### ✅ 값 타입의 비교
- equals() 재정의는 IDE에서 지원해주는 방식을 사용하는걸 추천함
- equals() 구현하면 그에 맞게 hashCode() 도 구현해줘야 함

### ✅ 값 타입 컬렉션
- 값 타입을 하나 이상 저장할 때 사용
- @ElementCollection, @CollectionTable 사용
- 값 타입 컬렉션의 생명주기 역시 값 타입을 소유한 엔티티의 생명주기에 의존함 
- 기본적인 FetchType 은 Lazy로 지연로딩 전략을 사용함
- 값 타입 수정 시에는 새로운 객체를 생성해서 전체를 교체하는 전략을 선택한다.
    ``` java

        Member member = em.find(Member.class, member.getId());
        Address address = member.getAddress();
        member.setAddress(new Address("newCity", address.getStreet(), address.getZipcode()))

    ```
- 값 타입 컬렉션 제약사항
    - 값 타입 컬렉션에 변경 사항이 발생하면, 주인 엔티티와 연관된 모든 데이터를 삭제하고, 값 타입 컬렉션에 있는 현재 값을 모두 다시 저장한다. ➡️ 💡 쓰면 안 됨
    - 값 타입 컬렉션을 매핑하는 테이블을 모든 컬럼을 묶어서 기본 키를 구성해야 함: null x, 중복저장 x

- 값 타입 컬렉션의 대안
    - 💡 **실무에서는 값 타입 컬렉션 대신 1:N 관계를 고려하자**
    - 값 타입 컬렉션 언제 쓰나? 정말 단순한 정보 선택하는 경우(고객 취향조사 정도...)

### ✅ 마무리
- 엔티티 타입
    - 식별자가 존재한다
    - 생명주기를 관리한다
    - 공유가 가능한다
- 값 타입
    - 식별자가 없다
    - 생명주기를 엔티티에 의존한다
    - 공유하지 않는 것이 안전하다
    - 불변객체로 만드는 것이 안전하다
- 값 타입은 정말 값 타입이라 판단될 때만 사용한다 ex) Position(x,y)
- 엔티티와 값 타입을 혼동해서 엔티티를 값 타입으로 만들면 안 된다
- 식별자가 필요하고 지속해서 값을 추적/변경해야 한다면 그것은 값 타입이 아닌 엔티티다

#
## [10] 객체지향 쿼리 언어1 - 기본 문법
---

### ✅ 소개

- JPA는 다양한 쿼리 방법을 지원
    - JPQL
    - QueryDSL
    - 네이티브 SQL
    - JDBC API 직접 사용, MyBatis, SpringJdbcTemplate 함께 사용
- JPQL 소개
    - 객체 지향 SQL
    - JPA를 사용하면 엔티티 객체를 중심으로 개발
        > 이 때의 문제는 검색쿼리. 검색을 할 때도 테이블이 아닌 엔티티 객체를 대상으로 검색해야 하기 때문임
        > 허나, 모든 DB 데이터를 객체로 변환해서 검색하는 것은 불가능 함
        > 따라서 어플리케이션이 필요한 데이터만 DB에서 불러오려면 결국 검색 조건이 포함된 SQL이 필요하게 됨
    - JPA는 SQL을 추상화한 JPQL 이라는 객체 지향 쿼리 언어를 제공
        > SQL과 문법이 유사하고 엔티티 객체를 대상으로 쿼리를 작성
        ```java
            List<Member> result = em.createQuery(
                "select m from Member m where m.username like '%kim%'",
                Member.class
            ).getResultList();
        ```
    - SQL을 추상화했기 때문에 특정 데이터베이스 SQL에 의존하지 않음
- JPQL 단점 
    - 단순 문자열이기 때문에 동적쿼리 생성이 어려움
    - JPA는 Criteria 를 제공하지만 실무에서는 잘 쓰이지 않음(복잡하고 실용성이 없음)
- QueryDSL
    - 동적쿼리 작성이 편리함
    - 컴파일 시점에 문법 오류를 찾을 수 있음
    - 단순하고 쉽기 때문에 실무에 사용하길 권장
- JDBC 직접 사용
    - 영속성 컨텍스트를 적절한 시점에 강제로 플러시 해야함
    예) JPA를 우회해서 SQL을 실행하기 직전에 영속성 컨텍스트 수동 플러시

### ✅ 기본 문법과 쿼리 API
- select m from Member as m where m.age > 18
- 엔티티와 속성은 대소문자를 구분한다(Member, age)
- JPQL 키워드는 대소문자를 구분하지 않는다(select, from, where)
- 테이블 이름이 아닌 엔티티 이름을 사용한다(Member)
- alias 는 필수이며 as 는 생략 가능하다(m)

- TypeQuery, Query
    - TypeQuery : 반환 타입이 명확할 때 사용
    - Query : 반환 타입이 명확하지 않을 때 사용

- 결과 조회 API
    - query.getResultList(): 결과가 하나 이상일 때, 리스트 반환. 결과가 없는 경우 빈 리스트 반환
    - query.getSingleResult(): 결과가 정확히 하나, 단일 객체 반환. 결과가 없는 경우 NoResultException, 둘 이상인 경우 NonUniqueResultException

- 파라미터 바인딩 : 이름기준, 위치기준 으로 적용가능
    - 이름 기준 바인딩 적용 예시
    ```java
        Member result = em.createQuery("select m from Member m where m.username = :username", Member.class)
        .setParameter("username", "jayden")
        .getSingleResult();
    ```
    - 위치 기준은 웬만해선 쓰지 않는 것을 추천(순서가 바뀌면 오류 가능성이 높아짐)

### ✅ 프로젝션
- 여러 값 조회
    - new 명령어로 조회 하는 방식 추천
    ```java
        em.createQuery("select new jpql.MemberDTO(m.username, m.age) from Member m", MemberDTO.class)
                    .getResultList();
    ```
        - 패키지 명을 포함한 전체 클래스명 입력
        - 순서와 타입이 일치하는 생성자 필요

### ✅ 페이징
- JPQL 쿼리에서 setFristResult와 setMaxResults 메서드 체인을 사용해 페이징 처리 가능
    ```java
        List<Member> result = em.createQuery("select m from Member m order by m.age desc", Member.class)
                            .setFirstResult(1)
                            .setMaxResults(10)
                            .getResultList();
    ```

### ✅ 조인

### ✅ 서브 쿼리
- FROM 절의 서브 쿼리는 현재 JPQL에서 작성 불가능
    - 조인으로 풀 수 있으면 풀어서 해결

#
## [10] 객체지향 쿼리 언어2 - 중급 문법
---

### ✅ 경로 표현식
- 경로 표현식이란? .(점)을 찍어 객체 그래프를 탐색하는 것
    ```java
        select m.username // 상태 필드
        from Member m
        join m.team t // 단일 값 연관 필드
        join m.orders o // 컬렉션 값 연관 필드
        where t.name = 'teamA'
    ```
- 용어정리
    - 상태필드(state field) : 단순히 값을 저장하기 위한 필드
    - 연관필드(association field) : 연관관계를 위한 필드
        - 단일 값 연관 필드 : @ManyToOne, @OneToOne, 대상이 엔티티(m.team)
        - 컬렉션 값 연관 필드 : @OneToMany, @ManyToMany, 대상이 컬렉션(m.orders)
- 경로 표현식의 특징
    - 상태 필드 : 경로탐색의 끝. 더 이상의 탐색 불가능
    - 단일 값 연관 경로 : 묵시적 내부 조인(inner join) 발생, 탐색가능
    - 컬렉션 값 연관 경로 : 묵시적 내부 조인 발생, 탐색 불가능
- 💡 웬만해선 묵시적 조인이 발생하지 않도록 JPQL을 작성할 것
    - Then -> FROM 절에서 명시적 조인을 통해 별칭을 얻으면 별칭을 통해 탐색 가능
- 예제
    - select o.member.team from Order o : 성공
    - select t.members from Team t : 성공
    - select t.members.username from Team t : 실패
    - select m.username from Team t join t.members m : 성공
- 💡 실무 조언
    - 가급적 묵시적 조인 대신 명시적 조인 사용
    - 조인은 SQL 튜닝에 중요한 포인트이고, 묵시적 조인은 조인이 일어나는 상황을 한눈에 파악하기 어렵기 때문

### ✅ 패치 조인 1 - 기본
**💡실무에서 정말 중요함**
- 페치 조인(fetch join)
    - JPQL 에서 성능 최적화를 위해 제공하는 기능
    - 연관된 엔티티나 컬렉션을 SQL 한 번에 함께 조회하는 기능
    - join fetch 명령어 사용
    - 페치 조인 ::= [LEFT [OUTER] | INNER] JOIN FETCH 조인경로
    

