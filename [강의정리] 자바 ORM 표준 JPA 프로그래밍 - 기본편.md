
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

 

