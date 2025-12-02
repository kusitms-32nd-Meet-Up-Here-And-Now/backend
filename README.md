# 📍 Here & Now

> **Kusitms_32nd_Meet_Up_Team3_HereAndNow_Backend**

<br/>

## 📖 프로젝트 소개

<img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/f7b04a9f-f46a-4f84-bbe0-c05c389b3718" /></br>
> **지금 이 순간을 기록하고 싶은 연인들을 위한 데이트 장소 추천과 아카이브 서비스**<br/>
히어 앤 나우는 광고와 저품질 정보로 인한 피로감을 없애고, 신뢰할 수 있는 UGC 기반의 데이트 코스를 제안합니다. <br/>
커플이 함께 계획을 세우고 다녀온 장소를 지도 위에 기록하는, 데이트 코스 추천 및 아카이빙 서비스입니다.


## 🛠️ 주요 사용 기술

### Backend
- Java 21
- Spring Boot 3.3.1
- Spring Security
- Spring Data JPA

### Database
- PostgreSQL
- PostGIS (for spatial data)
- Redis
- Flyway (for database migration)

### Authentication
- JSON Web Token (JWT)
- OAuth2

### API Documentation
- Swagger (SpringDoc)

### Cloud Services
- Naver Cloud Platform (NCP) Object Storage
- NCP NAT GateWay
- NCP Cloud Functions

### Testing
- JUnit 5
- Testcontainers
- Fixture-Monkey
- H2 Database

### Etc
- Lombok
- Gradle

<br/>

## 🚀 배포 주소
**[Here & Now](https://here-and-now-fe.vercel.app/)**

<br/>

## 📁 프로젝트 구조

```
.
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── meetup
│   │   │           └── hereandnow
│   │   │               ├── connect     # 커플 연결 관련
│   │   │               ├── core        # 공통 모듈 (예외 처리, 보안 등)
│   │   │               ├── course      # 데이트 코스 관련
│   │   │               ├── member      # 사용자 관련
│   │   │               └── place       # 장소 관련
│   │   └── resources
│   └── test
│       └── java
├── build.gradle
└── README.md
```

<br/>

## 📝 API 명세서
**[Swagger API Documentation](https://hereandnow.p-e.kr/swagger-ui/index.html)**

<br/>

## ERD
<img width="600" height="800" alt="image" src="https://github.com/user-attachments/assets/0888981e-16ec-4e79-8307-4c742493063c" />

## System Architecture
<img width="960" height="689" alt="image" src="https://github.com/user-attachments/assets/11b0b811-e3d6-494a-baca-ed7cf498b372" />
