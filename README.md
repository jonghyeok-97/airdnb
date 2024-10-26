## 프로젝트 목표
- 에어비앤비 같은 숙소 예약 서비스를 구현하기 입니다.
- 문서화, 테스트에 높은 우선순위를 두어 기능 추가 및 유지보수가 쉬운 프로젝트를 만들고자 합니다.

## Issue 해결 과정
- [#5] 예약 ~ 결제 승인 / 결제 취소에 대한 프로세스(트랜잭션 전파/Facade패턴/동시성 제어)
  
  [https://dkswhdgur246.tistory.com/66](https://dkswhdgur246.tistory.com/66)
- [#4] 이메일 인증 기반 로그인/회원가입 소개

  [https://dkswhdgur246.tistory.com/50](https://dkswhdgur246.tistory.com/50)
- [#3] 테스트로 알아보는 컨트롤러의 HttpSession 주입 시점
  
  [https://dkswhdgur246.tistory.com/57](https://dkswhdgur246.tistory.com/57)
- [#2] 이메일 에러를 디버거로 코드를 분석 후, 해결

  [https://dkswhdgur246.tistory.com/49](https://dkswhdgur246.tistory.com/49)
- [#1] 20만 더미 데이터 삽입을 csv파일을 이용해서 DB connection timeout → 7초로 줄이기

  [https://dkswhdgur246.tistory.com/47](https://dkswhdgur246.tistory.com/47)

## 프로젝트 중점사항
- 결제 승인 실패시 트랜잭션 전파를 사용해서 DB 레코드 롤백
- 결제 승인 요청을 트랜잭션에서 분리하기 위해 Facade 패턴 사용
- 유니크 제약조건을 사용한 예약 동시성 제어와 그에 따른 장단점
- MySQL의 인덱스 설정과 실행계획 분석 후 쿼리 튜닝
- 200여 개의 테스트 코드 작성, 100% 테스트 커버리지 달성
- @SpringBootTest가 있는 추상클래스를 사용해서 전체 테스트 시간 5초 감소 
- ApiResponse<T> 와 ErrorCode 를 사용한 일관된 API 응답 제공
- Spring Mail을 활용한 이메일 인증 기반의 회원가입 구현
- Custom Bean Validation을 활용한 이메일 유효성 범위 설정
- 세션과 ArgumentResolver를 활용한 로그인 기능 구현
- [Rest Docs를 활용한 API 자동 문서화](http://restdocs.s3-website.ap-northeast-2.amazonaws.com/)
  - S3로 배포
  - "필드명, 타입, 선택여부, 날짜 양식, 설명"을 API문서에 추가
- Docker-Compose와 Prometheus와 Grafana를 사용해 로컬에 모니터링 서버 구축
- Layer 간 참조 방향이 역행하지 않도록 ServiceDto 와 ControllerDto 분리

## 결제 프로세스 과정
![스크린샷 2024-10-27 165212.png](..%2F..%2F..%2FDesktop%2F%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202024-10-27%20165212.png)

## 개발환경
* JDK17 / Spring Boot 3.X / Spring Rest Docs / Spring Mail
* JPA / MySQL / Redis
* JUnit5 & Mockito
* Docker / S3 / Prometheus / Grafana






