# KG 이니시스 간편인증 Mock Server

Keycloak 연동 테스트를 위한 KG 이니시스 간편인증 시뮬레이터입니다.

## 📋 기능

- ✅ **인증 성공/실패 선택**: UI에서 성공 또는 실패를 선택하여 테스트
- 👥 **테스트 사용자 제공**: 5명의 미리 정의된 테스트 사용자
- ✏️ **커스텀 입력**: 사용자 정보를 직접 입력하여 테스트
- 🔍 **상세 로깅**: 모든 요청과 응답을 콘솔에 로깅
- 🎨 **실제와 유사한 UI**: 실제 이니시스와 유사한 인증 화면

## 🚀 실행 방법

### 1. 로컬 실행

```bash
# Maven 빌드
mvn clean package

# 실행
java -jar target/inicis-mock-server-1.0.0.jar

# 또는 Maven으로 직접 실행
mvn spring-boot:run
```

### 2. Docker 실행

```bash
# Docker 이미지 빌드
docker build -t inicis-mock-server .

# 컨테이너 실행
docker run -p 9090:9090 inicis-mock-server
```

### 3. 서버 확인

```bash
# 브라우저에서 접속
http://localhost:9090
```

## ⚙️ 설정

### application.yml

```yaml
server:
  port: 9090  # Mock Server 포트

inicis:
  mock:
    # 유효한 MID 목록 (설정하지 않으면 모든 MID 허용)
    valid-mids:
      - CIC12345678
      - CIC_TEST_001
    
    # 자동 성공 모드 (true: 항상 성공, false: 수동 선택)
    auto-success: false
    
    # 인증 지연 시간 (밀리초, 실제 환경 시뮬레이션)
    auth-delay-ms: 2000
```

## 🔗 Keycloak 연동 설정

### 1. Keycloak Admin Console 설정

```
Identity Providers → Add provider 
→ KG 이니시스 간편인증 선택
```

### 2. Provider 설정

| 항목 | 값 |
|-----|-----|
| Alias | `inicis` |
| 인증 페이지 URL | `http://localhost:9090/auth` |
| 상점 ID (MID) | `CIC12345678` |
| API Key | `any-value` (Mock Server는 검증하지 않음) |

### 3. Redirect URI 확인

Keycloak이 자동으로 생성하는 Redirect URI:
```
http://localhost:8080/realms/{realm-name}/broker/inicis/endpoint
```

 ## 📡 API 엔드포인트

### GET /auth

Keycloak에서 리다이렉트되는 인증 요청 엔드포인트

**요청 파라미터:**
- `mid` (필수): 상점 ID
- `returnUrl` (필수): 콜백 URL
- `reqSvcCd` (필수): 서비스 코드 (01: 간편인증)
- `moid` (선택): 거래 고유번호
- `state` (선택): CSRF 방지 토큰
- `phoneNo` (선택): 휴대폰 번호
- `birthDate` (선택): 생년월일

**예시:**
```
http://localhost:9090/auth?mid=CIC12345678&returnUrl=http://localhost:8080/realms/master/broker/inicis/endpoint&reqSvcCd=01&moid=240209123045AB12CD&state=xyz789
```

### POST /auth/process

인증 처리 엔드포인트 (UI에서 성공/실패 선택 시 호출)

## 👥 테스트 사용자 목록

| 이름 | 생년월일 | 휴대폰 번호 |
|-----|---------|-----------|
| 홍길동 | 19900101 | 01012345678 |
| 김철수 | 19850515 | 01087654321 |
| 이영희 | 19950303 | 01055556666 |
| 박민수 | 19881212 | 01099998888 |
| 정수진 | 19920707 | 01011112222 |

## 🔄 인증 플로우

```
1. Keycloak 로그인 페이지 → "KG 이니시스 간편인증" 클릭

2. Mock Server로 리다이렉트 (GET /auth)
   - MID, returnUrl, moid 등 파라미터 전달

3. Mock Server 인증 화면 표시
   - 테스트 사용자 선택 또는 직접 입력
   - 성공/실패 버튼 선택

4. POST /auth/process
   - 선택한 결과에 따라 응답 생성

5. Keycloak Callback URL로 리다이렉트
   - resultCode, CI, name, birthDate 등 파라미터 포함

6. Keycloak에서 사용자 매핑 및 세션 생성

7. 로그인 완료
```

## 📊 응답 형식

### 성공 응답

```
returnUrl?resultCode=0000
  &resultMsg=인증 성공
  &CI={생성된 CI}
  &name=홍길동
  &birthDate=19900101
  &gender=1
  &mobileCo=SKT
  &mobileNo=01012345678
  &moid=240209123045AB12CD
  &state=xyz789
```

### 실패 응답

```
returnUrl?resultCode=9999
  &resultMsg=사용자 인증 취소
  &moid=240209123045AB12CD
  &state=xyz789
```

## 🧪 테스트 시나리오

### 1. 정상 인증 테스트

1. Keycloak 로그인 페이지에서 "KG 이니시스 간편인증" 클릭
2. Mock Server에서 테스트 사용자 선택 (예: 홍길동)
3. "인증 성공" 버튼 클릭
4. Keycloak으로 돌아가서 로그인 완료 확인

### 2. 인증 실패 테스트

1. Keycloak 로그인 페이지에서 "KG 이니시스 간편인증" 클릭
2. Mock Server에서 실패 사유 선택
3. "인증 실패" 버튼 클릭
4. Keycloak에서 에러 메시지 확인

### 3. 재인증 테스트

1. 이미 로그인한 상태에서 재인증 시도
2. 기존 사용자 정보가 자동으로 전달되는지 확인
3. 동일한 CI로 매핑되는지 확인

### 4. 커스텀 사용자 테스트

1. "직접 입력" 섹션에서 임의의 정보 입력
2. "인증 성공" 클릭
3. 입력한 정보가 Keycloak에 올바르게 매핑되는지 확인

## 🔍 로그 확인

Mock Server는 모든 요청과 응답을 상세하게 로깅합니다:

```
========================================
Inicis Mock Auth Request Received
MID: CIC12345678
returnUrl: http://localhost:8080/realms/master/broker/inicis/endpoint
reqSvcCd: 01
moid: 240209123045AB12CD
state: xyz789
phoneNo: null
birthDate: null
========================================

Processing auth - action: success, name: 홍길동

Generating SUCCESS response - CI: a1b2c3d4..., name: 홍길동

Redirecting to callback URL: http://localhost:8080/realms/master/broker/inicis/endpoint?resultCode=0000&...
```

## 🐛 트러블슈팅

### Mock Server에 접속되지 않음

```bash
# 포트 사용 확인
netstat -an | grep 9090

# 방화벽 확인 (필요시)
sudo ufw allow 9090
```

### Keycloak에서 콜백 오류

1. Keycloak의 Redirect URI가 정확한지 확인
2. Mock Server 로그에서 콜백 URL 확인
3. 네트워크 연결 확인 (localhost vs 실제 IP)

### CI 값이 매번 다름

- 정상 동작입니다
- 실제 환경에서도 동일한 사용자는 항상 같은 CI를 받지만, Mock Server는 매번 새로운 CI를 생성합니다
- 테스트 시 같은 CI를 원한다면 코드 수정 필요

## 📦 프로젝트 구조

```
inicis-mock-server/
├── pom.xml
├── README.md
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── example/
        │           └── inicis/
        │               └── mock/
        │                   ├── InicisMockServerApplication.java
        │                   ├── config/
        │                   │   └── MockProperties.java
        │                   ├── controller/
        │                   │   └── InicisMockController.java
        │                   ├── dto/
        │                   │   ├── AuthRequest.java
        │                   │   └── AuthResponse.java
        │                   └── service/
        │                       └── MockAuthService.java
        └── resources/
            ├── application.yml
            └── templates/
                ├── index.html
                ├── auth.html
                └── error.html
```

## 🛠️ 개발 환경

- Java 17
- Spring Boot 3.2.0
- Thymeleaf
- Lombok
- Maven

## 📝 주의사항

- **테스트 전용**: 이 서버는 개발/테스트 환경에서만 사용하세요
- **실제 인증 없음**: 실제 본인확인은 수행되지 않습니다
- **보안**: 실제 환경에서는 절대 사용하지 마세요

## 🤝 기여

이슈 및 Pull Request 환영합니다!

## 📄 라이선스

MIT License

## 🔗 관련 프로젝트

- [keycloak-inicis-provider](../keycloak-inicis-provider) - Keycloak Identity Provider 구현
