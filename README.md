# NBE1_1_DDang
# 반려인과 반려견의 행복한 공생을 돕는 강아지 산책 서비스

![DDang](https://github.com/user-attachments/assets/90862317-5658-462d-9b4f-f22cac7870d0)

## 프로젝트 소개

- 현대인의 생활 속에서 반려견은 중요한 가족 구성원으로 자리 잡고 있습니다. 이에 따라 반려견을 위한 다양한 서비스와 기능이 필요해지고 있습니다. 특히, **산책**과 같은 일상적인 활동을 기록하고, 다른 반려견과의 교류를 통해 더욱 풍요로운 반려 생활을 지원하는 것은 필수적입니다.
- **DDang**은 반려견과 함께 산책하고, 산책 기록을 관리하며, 이웃 반려견과 소셜 네트워크를 형성하는 서비스를 제공합니다. 이를 통해 사용자들은 반려견과의 활동을 손쉽게 추적하고, 더욱 즐거운 산책 경험을 만들어 나갈 수 있습니다.


### 개발 기간

- 전체 개발 기간 : 2024-11-08 ~ 2024-12-11
- 프로젝트 기획 : 2024-11-08 ~ 2024-11-15
- 개발환경 세팅 : 2024-11-15 ~ 2024-11-19
- 기능 구현 : 2024-11-19 ~ 2024-12-08
- 문서화 : 2024-12-08 ~ 2024-12-11


### 작업 관리

- 매일 아침 스크럼 및 데일리 스탠드업 회의를 진행하며 작업한 내용, 작업 할 내용, 이슈 및 협업이 필요한 사항에 대해 공유하고 노션에 기록하며 작업을 진행했습니다.
- GitHub Projects와 Issues를 사용하여 진행 상황을 공유했습니다.
- 작업을 마친 Issue에 대해 Pull Request를 올리면 팀원 모두가 코드 리뷰를 진행하고 피드백 및 간단한 소감 작성을 마친 뒤 병합을 진행했습니다.


### 2차 팀 Notion
[최종 9팀](https://www.notion.so/prgrms/Team09-DDang-a42fec0ab88643e98eb5c47162c42bd3?pvs=4)

### 요구사항 명세서
[요구사항 명세서](https://www.notion.so/prgrms/13d3e47046bf81f0ad56fc30cc9198e0?pvs=4)

### 기획서
[최종 팀 프로젝트 기획서](https://www.notion.so/prgrms/DDang-13d3e47046bf8176baf7dd62047d56b7?pvs=4)

---

## 팀원 구성
<div align="center">

|                                                                **노관태**                                                                 |                                                                     **문재경**                                                                     |                                                                     **송경훈**                                                                     |                                                               **장준우**                                                               |
|:--------------------------------------------------------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------------------------------------------------:|
| [<img src="https://avatars.githubusercontent.com/u/65394501?v=4" height=100 width=100> <br/> @Repaion24](https://github.com/Repaion24) | [<img src="https://avatars.githubusercontent.com/u/108010440?v=4" height=100 width=100> <br/> @MoonJaeGyeong](https://github.com/MoonJaeGyeong) | [<img src="https://avatars.githubusercontent.com/u/128586833?v=4" height=100 width=100> <br/> @rudgns328](https://github.com/rudgns328) | [<img src="https://avatars.githubusercontent.com/u/176549799?v=4" height=100 width=100> <br/> @highjjjw](https://github.com/highjjjw) | 

</div>
<br>


---

## 1. 개발 환경

- Back-end : Java, Spring-boot, JPA, MYSQL, Redis
- 버전 및 이슈관리 : Github, Github Issues, Github Project
- 협업 툴 : Discord, Notion, Slack
- 문서화 : Swagger, Notion, README.md
- 테스트 : JUnit5, Postman
- 디자인 : [Figma](https://www.figma.com/design/8csD5BIS9LBoaPaS2WMdI2/%EC%9B%B9%EB%8D%B0%EB%B8%8C%EC%BD%94%EC%8A%A4-%EC%B5%9C%EC%A2%85%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8?node-id=557-46&t=MxVB5vMMXRWaoCub-0)

| **Software**          | **Version / Spec**                              |
|-----------------------|------------------------------------------------|
| **Java**              | Java SE 17.0.11                                |
| **Spring Boot**       | 3.3.5                                          |
| **Spring Boot Modules** | Data JPA, Web, Validation, Security, Batch, Redis Reactive |
| **Lombok**            | 1.18.20                                        |
| **QueryDSL**          | JPA 5.0.0 (Jakarta)                            |
| **JWT**               | Java JWT 4.2.1                                 |
| **Database**          | MySQL 8.0.39, H2 2.1.214 (Test)                |
| **Hibernate Spatial** | 6.5.3.Final                                    |
| **Redis**             | Embedded Redis 0.7.2 (ARM 지원)                |
| **WebSocket**         | Spring Boot Starter WebSocket                  |
| **Security**          | Spring Security (OAuth2 Client 6.3.1)          |
| **Jackson**           | Jackson Datatype JSR310, Jackson Databind      |
| **Testing**           | JUnit 5 (Platform Launcher 1.9.2)              |
| **Documentation**     | Swagger (SpringDoc OpenAPI UI 2.2.0), REST Docs |
| **Build Tools**       | Gradle                                         |

---

## 2. 개발 가이드라인
### 브랜치 전략
- [깃 컨벤션](docs/Branch%20strategy%20and%20pull-request.md)
### 코드 컨벤션
- [코드 컨벤션](docs/Code%20Convention.md)

---

## 3. Entity Relationship Diagram

![erd_image](https://github.com/user-attachments/assets/5fccffc6-4a18-4575-a34c-a4c1f806dae5)
[ERDCloud](https://www.erdcloud.com/d/MPKkcgJnPpLvkgH7b)

---

## 4. 프로젝트 패키지 구조

```
📦src
 ┣ 📂main
 ┃ ┣ 📂java
 ┃ ┃ ┗ 📂team9
 ┃ ┃ ┃ ┗ 📂ddang
 ┃ ┃ ┃ ┃ ┣ 📂chat
 ┃ ┃ ┃ ┃ ┃ ┣ 📂consumer
 ┃ ┃ ┃ ┃ ┃ ┣ 📂controller
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂request
 ┃ ┃ ┃ ┃ ┃ ┣ 📂entity
 ┃ ┃ ┃ ┃ ┃ ┣ 📂event
 ┃ ┃ ┃ ┃ ┃ ┣ 📂exception
 ┃ ┃ ┃ ┃ ┃ ┣ 📂producer
 ┃ ┃ ┃ ┃ ┃ ┣ 📂repository
 ┃ ┃ ┃ ┃ ┃ ┗ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂request
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂response
 ┃ ┃ ┃ ┃ ┣ 📂dog
 ┃ ┃ ┃ ┃ ┃ ┣ 📂controller
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂request
 ┃ ┃ ┃ ┃ ┃ ┣ 📂entity
 ┃ ┃ ┃ ┃ ┃ ┣ 📂exception
 ┃ ┃ ┃ ┃ ┃ ┣ 📂repository
 ┃ ┃ ┃ ┃ ┃ ┗ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂request
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂response
 ┃ ┃ ┃ ┃ ┣ 📂family
 ┃ ┃ ┃ ┃ ┃ ┣ 📂controller
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂request
 ┃ ┃ ┃ ┃ ┃ ┣ 📂entity
 ┃ ┃ ┃ ┃ ┃ ┣ 📂exception
 ┃ ┃ ┃ ┃ ┃ ┣ 📂repository
 ┃ ┃ ┃ ┃ ┃ ┗ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂request
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂response
 ┃ ┃ ┃ ┃ ┣ 📂global
 ┃ ┃ ┃ ┃ ┃ ┣ 📂aop
 ┃ ┃ ┃ ┃ ┃ ┣ 📂api
 ┃ ┃ ┃ ┃ ┃ ┣ 📂config
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂batch
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂kafka
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂security
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂websocket
 ┃ ┃ ┃ ┃ ┃ ┣ 📂controller
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂response
 ┃ ┃ ┃ ┃ ┃ ┣ 📂entity
 ┃ ┃ ┃ ┃ ┃ ┣ 📂event
 ┃ ┃ ┃ ┃ ┃ ┣ 📂exception
 ┃ ┃ ┃ ┃ ┃ ┗ 📂service
 ┃ ┃ ┃ ┃ ┣ 📂member
 ┃ ┃ ┃ ┃ ┃ ┣ 📂controller
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂request
 ┃ ┃ ┃ ┃ ┃ ┣ 📂entity
 ┃ ┃ ┃ ┃ ┃ ┣ 📂exception
 ┃ ┃ ┃ ┃ ┃ ┣ 📂jwt
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂filter
 ┃ ┃ ┃ ┃ ┃ ┣ 📂oauth2
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂handler
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂userinfo
 ┃ ┃ ┃ ┃ ┃ ┣ 📂repository
 ┃ ┃ ┃ ┃ ┃ ┗ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂request
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂response
 ┃ ┃ ┃ ┃ ┣ 📂notification
 ┃ ┃ ┃ ┃ ┃ ┣ 📂controller
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂request
 ┃ ┃ ┃ ┃ ┃ ┣ 📂entity
 ┃ ┃ ┃ ┃ ┃ ┣ 📂exception
 ┃ ┃ ┃ ┃ ┃ ┣ 📂repository
 ┃ ┃ ┃ ┃ ┃ ┣ 📂scheduler
 ┃ ┃ ┃ ┃ ┃ ┗ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂request
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂response
 ┃ ┃ ┃ ┃ ┣ 📂walk
 ┃ ┃ ┃ ┃ ┃ ┣ 📂controller
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂request
 ┃ ┃ ┃ ┃ ┃ ┣ 📂entity
 ┃ ┃ ┃ ┃ ┃ ┣ 📂exception
 ┃ ┃ ┃ ┃ ┃ ┣ 📂repository
 ┃ ┃ ┃ ┃ ┃ ┗ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂request
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂response
 ┃ ┃ ┃ ┃ ┃ ┗ 📂util
 ┃ ┃ ┃ ┃ ┗ 📂resources
 ┃ ┗ 📂resources
 ┗ 📂test
 ┃ ┣ 📂java
 ┃ ┃ ┗ 📂team9
 ┃ ┃ ┃ ┗ 📂ddang
 ┃ ┃ ┃ ┃ ┣ 📂chat
 ┃ ┃ ┃ ┃ ┣ 📂family
 ┃ ┃ ┃ ┃ ┣ 📂member
 ┃ ┃ ┃ ┃ ┣ 📂security
 ┃ ┃ ┃ ┃ ┗ 📂resources
 ┃ ┗ 📂resources
```

---

## 5. 역할 분담

### 👻 노관태

- **패밀리댕**
  - 패밀리댕 관련 API 구현
- **산책 일정**
  - 산책일정 관련 API 구현
- **채팅**
  - 채팅 관련 API 구현
  - WebSocket과 Kafka를 이용한 채팅 시스템 구현

<br>

### 😎 문재경

- **산책**
  - 산책 관련 API 구현
  - WebSocket과 Redis를 사용한 이용한 산책 시스템 구현
- **강번따**
  - 강번따 관련 API 구현
  - Redis의 공간 데이터 함수를 활용하여 가까운 유저 간 실시간 위치 공유
- **댕댕로그**
  - 댕댕로그 관련 API 구현
  - Security 를 통한 유저 권한 체크 및 인증 구현

<br>

### 🙃 송경훈

- **회원**
  - 멤버 관련 API 구현
  - AccessToken 과 RefreshToken 을 이용한 JWT 회원 로그인 구현
  - OAuth 2.0 을 사용한 구글 로그인 구현
- **시큐리티**
  - CORS 설정 구현
  - Security 를 통한 유저 권한 체크 및 인증 구현
- **알림**
  - 멤버 관련 API 구현
  - FCM을 활용한 알림 기능 구현

<br>

### 🤔 장준우

- **강아지**
  - 강아지 관련 API 구현

<br>


## 6. 신경 쓴 부분



---

## 7. API 명세서

###  [REST Docs](https://ddang.shop/swagger-ui/index.html)

---

## 8. 개선 목표

- 성능 체크 및 코드 리팩토링을 통한 성능 향샹
- 경계값 테스트 진행
- SQL 튜닝 하여 성능 향상
- QueryDsl 적극적으로 사용하기
- N+1 문제 발생지점 파악 후 해결
- SOLID 원칙에 따라서 객체지향 설계로 리팩토링 도전
- 반복문으로 단일쿼리가 나가는 곳 개선
- 컨테이너화를 통한 환경 통합
- 
  <br>