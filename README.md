## 프로젝트 목표
- 에어비앤비 같은 숙소 예약 서비스를 구현하기 입니다.
- 다양한 기술을 적용하며, Edge Case테스트에 높은 우선순위를 두어 기능 추가 및 유지보수가 쉬운 프로젝트를 만들고자 합니다.

## Issue 해결 과정
- [[#7] 비동기 메일 전송 기능 고도화 : 재시도, 예외 처리, 테스트](https://dkswhdgur246.tistory.com/69)

- [[#6] @Async로 이메일 전송 비동기 처리하기: 쓰레드 풀 설정과 API 응답 속도 개선](https://dkswhdgur246.tistory.com/68)

- [[#5] 예약 ~ 결제 승인 / 결제 취소에 대한 프로세스(트랜잭션 전파/Facade패턴/동시성 제어/외부 API 호출 동기 or 비동기)](https://dkswhdgur246.tistory.com/66)
  
- [[#4] 이메일 인증 기반 로그인/회원가입 소개](https://dkswhdgur246.tistory.com/50)
  
- [[#3] 테스트로 알아보는 컨트롤러의 HttpSession 주입 시점](https://dkswhdgur246.tistory.com/57)
  
- [[#2] 이메일 에러를 디버거로 코드를 분석 후, 해결](https://dkswhdgur246.tistory.com/49)
  
- [[#1] 20만 더미 데이터 삽입을 csv파일을 이용해서 DB connection timeout → 7초로 줄이기](https://dkswhdgur246.tistory.com/47)

## 프로젝트 중점사항
- 결제 승인 실패 시 트랜잭션 전파 설정을 통해 DB 레코드 롤백 처리
- 결제 승인 요청을 트랜잭션과 분리하기 위해 Facade 패턴 적용
- 유니크 제약 조건을 사용한 예약 동시성 제어 및 이에 따른 장단점 분석
- MySQL 인덱스 설정 및 실행 계획 분석을 통한 쿼리 성능 튜닝
- 200여 개의 테스트 코드 작성으로 100% 테스트 커버리지 달성
- @SpringBootTest를 적용한 추상 클래스를 통해 전체 테스트 시간 5초 단축
- ApiResponse<T>와 ErrorCode를 활용하여 일관된 API 응답 제공
- Spring Mail을 활용한 이메일 인증 기반 회원가입 구현
  - @Async를 이용한 비동기 이메일 전송
    - Jmeter를 이용한 API 응답 속도 개선 확인
  - 네트워크 오류 시 메일 전송을 재시도하여 사용자 경험 개선
- Custom Bean Validation을 이용하여 이메일 유효성 검사 범위 중복 제거
- 세션과 ArgumentResolver를 활용한 로그인 기능 구현
- [Rest Docs를 활용한 API 자동 문서화](http://restdocs.s3-website.ap-northeast-2.amazonaws.com/)
  - S3에 배포하여 문서 접근성 강화
  - "필드명, 타입, 선택 여부, 날짜 양식, 설명"을 포함한 상세 API 문서화
- Docker-Compose, Prometheus, Grafana를 활용해 로컬 모니터링 서버 구축
- Layer 간 참조 방향을 유지하기 위해 ServiceDto와 ControllerDto 분리
- @Async를 이용한 비동기 이메일 전송 및 API응답 속도 개선 확인하기

## 결제 프로세스 과정
![image](https://github.com/user-attachments/assets/41871857-d94a-4175-9ce5-4c09f405ba53)


## 개발환경
* JDK17 / Spring Boot 3.X / Spring Rest Docs / Spring Mail
* JPA / MySQL / Redis
* JUnit5 & Mockito
* Docker / S3 / Prometheus / Grafana






