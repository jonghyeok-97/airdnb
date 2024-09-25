# Airbnb Clone Project
Airbnb 웹 사이트의 로그인/회원가입 부터 숙소 등록, 숙소 예약에 대한 API 를 제공하고자 합니다.

### 개발환경
* JDK17
* SpringBoot3.3
* Spring Data JPA
* MySQL
* Spring Rest Docs 3.0.1

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
- 총 5개인 필드명, 타입, 선택여부, 양식(날짜 등), 설명을 API문서에 녹였습니다.
- 공통 응답 필드 및 에러코드 예시를 추가하였습니다.

## #주요 기능 & 구현 의도
### 1. ERD 설계
![image](https://github.com/user-attachments/assets/ce315dbe-1d22-4156-8058-6079317f7e13)
- 각 엔티티간 생명주기를 고려하여 참조를 끊으며 보수적으로 접근
  - 회원, 숙소, 예약 각각은 생명주기가 다르기 때문에 참조를 끊음
  - 숙소와 숙소 이미지는 숙소가 등록될 때 같이 생성된다는 점에서 생명주기가 같다고 판단하여 참조 연결
  - 예약(Reservation)과 예약 날짜(Reservation Date) 는 생명주기가 다르다고 판단하여 참조를 끊음

### 2. 단위 테스트 & 통합 테스트 with DynamicTest
![image](https://github.com/user-attachments/assets/fa9175f5-fee4-4b28-afec-d7d1bb26b683)

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




