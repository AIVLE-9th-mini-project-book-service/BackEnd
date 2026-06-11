# 📚 도서관리시스템 서버 개발



## 프로젝트 개요

## 프로젝트 개요

걷기와 서재 백엔드는 Spring Boot 기반의 도서 관리 시스템 API 서버입니다.

Spring Data JPA를 활용하여 도서 CRUD, 회원 관리, 좋아요, 댓글, AI 이미지 생성, 통계 조회 기능을 제공하는 REST API를 구현하였습니다.

## 기술 스택

### Frontend
- React 19 · Vite ·fetchBackend

### Backend
- java 17.0
- Spring Boot 4.0
- Spring MVC 
- Spring Data JPA
- Lombok
- MySQL 8.0

### AI
- OpenAI API (GPT Image 모델)

### Tools
- IntelliJ IDEA
- GitHub 
- Postman



## 팀원 구성

- 박유경 :

- 김완수 :
  
- 박선호 :
  
- 박형우 :
  
- 신가람 :
  
- 심유리 :
  
- 윤빈 :
  
- 최지흠 : 

- 한승연 :



## 기능 소개

### 회원 기능
- 회원가입
- 로그인
- 로그아웃

### 도서 관리
- 새 도서 등록
- 도서 목록 조회
- 도서 수정
- 도서 삭제 (휴지통으로 이동)

### 후기 관리
- 도서 후기 댓글 등록
- 도서 후기 댓글 수정
- 도서 후기 댓글 삭제
- 도서 좋아요 수 증가

### AI 
- AI 표지 이미지 생성
- AI 한줄평 생성

### 통계 조회
- 도서 수 통계 
- 좋아요 수 통계

### 휴지통 관리
- 삭제된 도서 관리
- 휴지통 도서 영구 삭제

### 예외처리
- 회원가입 및 로그인 관련 예외 처리
  - 존재하지 않는 이메일
  - 중복 이메일
  - 비밀번호 불일치
- 필수 입력값 검증
- 잘못된 요청 파라미터 검증
- 존재하지 않는 도서 조회/수정/삭제 예외처리
- 존재하지 않는 댓글 조회/수정 예외처리
- OpenAI API 호출 및 이미지 생성 예외처리



## 설치 방법

git clone https://github.com/AIVLE-9th-mini-project-book-service/BackEnd.git

cd BackEnd

## 실행 방법

### IntelliJ IDEA
- IntelliJ IDEA 실행
- BackEnd 프로젝트 열기
- Gradle 의존성 다운로드 완료 대기
- BookappApplication.java 실행
- 
### 터미널 실행
gradlew.bat bootRun

### API 서버
http://localhost:8080



## 프로젝트 구조

```text
src/
├── main/
│   ├── java/
│   │   └── com/aivle/bookapp/
│   │       ├── config/
│   │       │   ├── SecurityConfig.java
│   │       │   ├── SwaggerConfig.java
│   │       │   └── WebConfig.java
│   │       ├── controller/
│   │       │   ├── BookController.java
│   │       │   ├── CommentController.java
│   │       │   └── MemberController.java
│   │       ├── domain/
│   │       │   ├── Book.java
│   │       │   ├── BookTag.java
│   │       │   ├── Comment.java
│   │       │   └── Member.java
│   │       ├── dto/
│   │       │   ├── AiBookSummaryRequest.java
│   │       │   ├── AiBookSummaryResponse.java
│   │       │   ├── BookCreateRequest.java
│   │       │   ├── BookSearchRequest.java
│   │       │   ├── BookSearchResponse.java
│   │       │   ├── BookUpdateRequest.java
│   │       │   ├── CommentCreateRequest.java
│   │       │   ├── CommentUpdateRequest.java
│   │       │   ├── CoverImageUpdateRequest.java
│   │       │   ├── GenerateCoverRequest.java
│   │       │   ├── GenerateCoverResponse.java
│   │       │   ├── LoginRequest.java
│   │       │   ├── LoginResponse.java
│   │       │   ├── SignupRequest.java
│   │       │   └── SignupResponse.java
│   │       ├── exception/
│   │       │   ├── BookNotFoundException.java
│   │       │   ├── CommentNotFoundException.java
│   │       │   ├── DuplicateEmailException.java
│   │       │   ├── GlobalExceptionHandler.java
│   │       │   ├── InvalidPasswordException.java
│   │       │   ├── MemberNotFoundException.java
│   │       │   └── OpenAiException.java
│   │       ├── filter/
│   │       │   └── JwtFilter.java
│   │       ├── repository/
│   │       │   ├── BookRepository.java
│   │       │   ├── CommentRepository.java
│   │       │   └── MemberRepository.java
│   │       ├── service/
│   │       │   ├── BookService.java
│   │       │   ├── CommentService.java
│   │       │   └── MemberService.java
│   │       ├── util/
│   │       │   └── JwtUtil.java
│   │       └── BookappApplication.java
│   └── resources/
│       ├── application.yaml
│       ├── data.sql
│       └── mysql_mini5.sql
└── test/

### 주요 디렉토리 설명
- config : 보안 및 환경 설정
- controller : API 요청 처리
- domain : Entity 관리
- dto : 요청 및 응답 객체
- exception : 예외처리
- repository : 데이터베이스 접근
- service : 비즈니스 로직 처리

