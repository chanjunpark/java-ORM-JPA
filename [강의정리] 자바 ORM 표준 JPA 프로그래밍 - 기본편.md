
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

