# 프로젝트 목표
- 숙소 예약 플랫폼의 회원가입부터 숙소 결제를 구현하는 것을 목표로 합니다.
- 스프링이 제공하는 다양한 기술을 학습하며, 테스트 코드를 기반으로 정확하고 신뢰성 있는 학습을 목표로 삼았습니다.

# Issue 해결 과정
#### [- [#8] Github Actions 으로 CI 테스트 자동화 중 겪은 이슈 정리 (Embedded Redis, Profile, Spring Rest Docs)](https://dkswhdgur246.tistory.com/73)
#### [- [#7] 트랜잭션 롤백 시 결제 취소 요청하기 - @TransactionalEventListener & ApplicationEventPublisher 모킹 이슈](https://dkswhdgur246.tistory.com/72)
#### [- [#6] RestTemplate 로깅 중 404 응답에 대한 FileNotFoundException 해결](https://dkswhdgur246.tistory.com/70)
#### [- [#5] @Async 이메일 전송 고도화 : 재시도, 예외 처리, 테스트](https://dkswhdgur246.tistory.com/69)
#### [- [#4] 이메일 전송을 Async-NonBlocking 처리하기: 쓰레드 풀 설정과 API 응답 속도 99% 개선](https://dkswhdgur246.tistory.com/68)
#### [- [#3] [결제 프로세스 설계] 트랜잭션 설정 및 전파, 동기/비동기, 동시성, Facade 패턴](https://dkswhdgur246.tistory.com/66)
#### [- [#2] 테스트로 알아보는 컨트롤러의 HttpSession 주입 시점](https://dkswhdgur246.tistory.com/57)
#### [- [#1] 20만 더미 데이터 삽입을 csv파일을 이용해서 DB connection timeout → 7초로 줄이기](https://dkswhdgur246.tistory.com/47)

# 프로젝트 주요 기능 소개
### ✅숙소 예약 및 결제 기능
#### 데이터 정합성 확보
  - 스프링 AOP의 동작 원리를 학습해 트랜잭션 설계
  - 트랜잭션 전파를 사용하여 논리 트랜잭션 롤백 시 물리 트랜잭션도 롤백되도록 구현
  - 물리 트랜잭션 롤백 시 결제 취소 요청을 보내기 위해 @TransactionalEventListener 사용

#### 동시 예약 및 결제 방지
  - DB 트랜잭션 격리 수준, JPA 낙관적/비관적 락 학습
  - 유니크 제약 조건을 통해 동시 예약을 방지하고, 예외 발생 시 try-catch를 통해 결제 취소를 위한 외부 결제 API 호출
  - concurrent 패키지를 사용해 동시성 제어 로직을 테스트

#### 외부 API 사용 시 고려 사항
  - 외부 API 호출로 인한 영향을 줄이기 위해 타임아웃과 서킷 브레이커를 학습하고 타임아웃을 적용했습니다.
  - 새로운 PG사 API 추가 시 기존 PG사 API 호출와 구분되도록 별도의 RestTemplate을 생성하고, 로깅 및 4xx/5xx 응답 처리 핸들러 구현
  - Mockito의 verify를 사용해 외부 API 호출 로직이 실행되었는지 검증
  - RestClientTest로 MockServer를 구축하여 결제 승인, 취소, 4xx 응답 상황을 테스트
  - Facade 패턴을 적용해 외부 API 호출과 트랜잭션을 분리, 테스트 코드의 용이성 확보

### ✅이메일 인증 기반 회원가입/로그인 기능
#### @Async 를 사용해 이메일 전송을 비동기로 처리
  - 쓰레드 풀(ThreadPoolExecutor)의 기본 크기, 최대 크기, 큐 용량 등을 설정하며 쓰레드 풀 동작 원리를 학습
  - JMeter 를 통해 응답 속도가 99% 개선됨을 확인

#### 메일 전송 실패 시 재시도 로직
- 네트워크 오류로 인한 실패에 대해 재시도 로직 추가, @Recover를 활용해 복구 시 로그 기록
- 큐 용량 초과 예외와 재시도 복구 예외를 구분하여 운영 혼란 방지
- 비동기 처리, 재시도, 복구 로직의 테스트 중복 문제를 DynamicTest와 ParameterizedTest로 해결해 테스트 가독성을 향상
<img src="https://github.com/user-attachments/assets/d477ef8d-7d43-489c-aadf-52222a004151" alt="이미지 설명" width="350" />

### ✅210여개의 테스트와 테스트 커버리지 99%
#### 테스트 코드 작성
- JUnit5와 Mockito를 활용해 210여 개의 테스트 코드 작성, 99% 테스트 커버리지 달성
- 비동기, 재시도, 동시성, 외부 API 서버 호출을 포함하여 모든 계층 테스트 작성

<img src="https://github.com/user-attachments/assets/1cce4711-74bf-493f-9a90-bc8fcb4ca2f1" alt="이미지 설명" width="300" />


#### 테스트 최적화
- @SpringBootTest를 사용해 테스트 환경에서 하나의 서버만 실행하도록 추상 클래스를 설계, 테스트 시간 5초 단축
- Classist 원칙을 따르며, 제어할 수 없는 외부 API 호출에는 Mock을 사용

#### 테스트로 얻은 효과
- Spring Boot의 HttpSession 주입 시점을 파악하며 로직상의 버그를 수정
- WebMvcTest와 MockMvc는 내장 톰캣(Web Server)을 테스트하지 않는 것을 알게 됨
 
### ✅기타 개선 사항
#### 쿼리 최적화
- 인덱스 활용 시 WHERE 조건 비교를 동등 비교(IN절)로 변경하여 쿼리 수행 시간을 0.9초 단축

#### API 문서화
- Spring Rest Docs 를 활용한 API 문서 자동화 및 S3 를 통한 배포 [**(Link)**](http://restdocs.s3-website.ap-northeast-2.amazonaws.com/)

#### 코드 중복 제거 및 유지보수성 향상
- ArgumentResolver 를 활용해 로그인 확인 로직 중복 제거
- 일관된 응답 제공을 위해 ApiResponse<T>와 ErrorCode를 사용해 성공 및 에러 응답 형식을 통일
- Bean Validation을 재정의하여 DTO의 이메일 유효성 검사 중복 로직 제거
- Web ↔ Controller, Controller ↔ Service 간 DTO를 분리해 계층 간 참조가 역행하지 않도록 설계

#### 로컬 모니터링 환경 구축
- Docker-Compose, Prometheus, Grafana를 활용해 로컬 모니터링 서버 구축
- metric 학습

## 결제 프로세스 과정
<img src="https://github.com/user-attachments/assets/8f06de6c-3773-4313-81dd-38b7dd40fca5" alt="이미지 설명" width="600" />


## 개발환경
* JDK17 / Spring Boot 3.X / Spring Rest Docs / Spring Mail
* JPA / MySQL / Redis
* JUnit5 & Mockito
* Docker / S3 / Prometheus / Grafana

## 회고
- 스프링이 제공하는 다양한 기능을 활용해 프로젝트를 고도화할 수 있었으며, 모든 테스트 코드를 작성함으로써 운영 중 버그가 없는 안정적인 코드를 작성할 자신감을 얻을 수 있었습니다.
- 다만, 이번 프로젝트에서는 운영 환경에서 발생할 수 있는 로깅, 서버 비용 관리, 모니터링 결과 분석 등의 이슈를 이론적으로만 학습하고 실제 적용하지 못한 점이 아쉬웠습니다.
다음 프로젝트에서는 꼭 운영 환경을 경험하며 이러한 부분을 실질적으로 적용해보고 싶습니다.





