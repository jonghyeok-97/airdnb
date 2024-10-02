# Airbnb Clone Project
Airbnb 웹 사이트의 로그인/회원가입 부터 숙소 등록, 숙소 예약에 대한 API 를 제공하고자 합니다.

### 개발환경
* JDK17
* SpringBoot3.3
* Spring Data JPA
* MySQL 8.0
* Spring Rest Docs 3.0.1
* Spring Data Redis
* JUnit5 & Mockito

## #실행 방법
### with mysql
1. application-local.yml 을 참고하여 설정 or 수정
```
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/airbnb
    username: team04
    password: 1234
```
2. 실행
```
> ./gradlew clean bootRun 
```

- 회원가입 중 이메일 인증번호를 보내는 기능 사용 시 yml 추가 방법
1. main/reousources 에 application-email.yml 을 만든다.
2. application-email.yml 에 아래 설정을 한다.
```
spring:
  email:
    username: 네이버이메일
    password: 네이버비밀번호
```
## #API 문서
### [Rest Docs](http://restdocs.s3-website.ap-northeast-2.amazonaws.com/)
- html파일을 S3를 이용하여 배포했습니다.
- 총 5개인 필드명, 타입, 선택여부, 필드 양식(날짜 등), 설명을 API문서에 추가 했습니다.
- 공통 응답 필드 및 에러코드 예시를 추가하였습니다.

## #주요 기능 & 구현 의도
### 1. ERD 설계
![image](https://github.com/user-attachments/assets/ce315dbe-1d22-4156-8058-6079317f7e13)
- 각 엔티티간 생명주기를 고려하여 연관관계를 끊으며 보수적으로 접근
  - 회원, 숙소, 예약 각각은 생명주기가 다르기 때문에 연관관계를 끊음
  - 숙소와 숙소 이미지는 숙소가 등록될 때 같이 생성된다는 점에서 생명주기가 같다고 판단하여 연관관계 설정

### 2. JUnit5 & Mockito 등을 활용한 테스트 코드
* Presentation ~ Persistence Layer 까지 160여개의 테스트 코드 작성, 테스트 커버리지 94% 달성하여 리팩토링 시 코드 안정성 확보
![image](https://github.com/user-attachments/assets/11b78d0d-4c90-452c-8c3a-a4388d558013)
![image](https://github.com/user-attachments/assets/70ab9a5b-5255-42ad-8fa3-a68829fed047)

* DynamicTest, ParameterizedTest 를 활용한 테스트 코드의 중복을 제거, 시나리오 기반 테스트를 통해 테스트 코드  리팩토링
![image](https://github.com/user-attachments/assets/fa9175f5-fee4-4b28-afec-d7d1bb26b683)

* 테스트 하기 쉬운 환경을 위해 외부 의존성 및 제어할 수 없는 부분을 격리
  * 이메일 인증 번호를 보내는 로직 중, 외부 이메일 API 를 사용하는 기능을 `MailClinet`로 분리한 후, Bean 으로 설정하여 `Service`에 DI 로 구현함. 이 덕분에 해당 `Service` 로직을 테스트 할 때, `MaliClinet` 를 Mocking 후 테스트 진행
  * LocalDateTime.now() 같이 테스트 시마다 달라지는 코드를 `Presentation Layer` 까지 격리
  
* 테스트 코드를 통해 Tomcat 이 관리하는 Session 을 Spring Boot의 컨트롤러에서 사용 시 주의사항을 알 수 있었습니다. [Blog Link](https://dkswhdgur246.tistory.com/57)
### 3. 지속 가능한 개발
- Restful API 기반의 로그인 인가를 위해 Interceptor 대신 ArgumentResolver 를 활용한 로그인 기능[[Blog Link]](https://dkswhdgur246.tistory.com/61)
- 사용자 편의성까지 고려하여 구현한 회원가입 시 이메일 인증 기능 [[Blog Link]](https://dkswhdgur246.tistory.com/50)
- 세션을 활용한 로그인 인가의 확장성 문제를 극복하기 위해 Nginx와 Redis를 사용한 서버 인프라를 설계
  - 세션의 첫 번째 단점은 로그인한 사용자가 많아질 경우, 서버 부하 문제가 발생합니다.
  - 두 번째 단점은 서버를 확장할 경우, 각 서버의 세션 정보를 동기화 해야합니다.
- 이를 극복하고자 서버를 확장했을 경우, 세션을 메모리가 아닌 캐시DB 용도의 Redis 에 저장을 하고, 확장된 서버에게 요청을 분산시키는 로드밸런서 역할의 Nginx 를 앞 단에 두어 배포할 수 있습니다.
![확장 서버 drawio](https://github.com/user-attachments/assets/34246eb6-4ca1-4137-bed9-a72f917c3522)

### 3. 지속 가능한 개발
- Custom 한 에러코드와 응답 API를 제공하여 일관된 API 응답 제공
- 가독성과 구현 의도를 알리기 위해 custom annotation 에 javadoc 및 주석 작성
  - 회원가입 시 이메일 인증할 때 고려한 것들 [[Blog 링크]](https://dkswhdgur246.tistory.com/50)
  - ex) main/java/airdnb/be/annotation/validation/VaildEmail
```
@Documented
@Constraint(validatedBy = ValidEmailValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ValidEmail {

    String regexp() default "^[^@]+@[^@\\-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
    ... 중략
}

/**
 * 모든 이메일에 인증 메일을 전송하지 않게 하여 서버의 부하를 낮추고자 함.
 * Gmail 경우, 로컬에 + 에 포함 가능 -> 도메인 부분만 엄격하게 검증
 *
 * 로컬 : @ 문자를 제외한 모든 문자가 하나 이상 포함
 * @ : 전체 문자에서 단 하나
 * 도메인
 *       [^@\-] : @ 뒤에 @ 나 - 로 시작하지 않음
 *       [A-Za-z0-9-]+ :  알파벳, 숫자가 1개 이상 포함되어야 함
 *       (\.[A-Za-z0-9-]+)* : 위의 표현식이 . 을 포함해서 0번이상 반복될 수 있음
 *       (\.[A-Za-z]{2,})$ : 끝은 알파벳 2자리 이상으로 끝나야 함
 *
 */
```
- 가독성과 안전성을 위한 람다, 스트림을 적극 활용
- Layer 간 참조 방향이 역행하지 않도록 구현
  -  Layer 간 dto 를 구분
  - Presentation dto 네이밍 : ~request
  - Service dto 네이밍 : ~ServiceRequest / Response

### 4. Spring 활용
- ArgumentResolver, custom Bean Validation 을 활용한 로그인 기능 구현
- ExceptionHandler 와 custom ErrorCode 를 활용한 일관된 에러 처리




