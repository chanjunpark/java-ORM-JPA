
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
