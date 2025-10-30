# 쿠폰 관리 시스템 ADMIN

## 프로젝트 개요

- 특정 사용자 그룹에게 대량으로 쿠폰을 발급하기 위한 관리자 시스템
- CSV 또는 Excel 형식의 사용자 목록 파일을 업로드하여 쿠폰 발급 대상을 지정하고 업로드된 파일의 유효성을 검증하며 디버깅 목적으로 원본 파일을 다시 다운로드하는 기능을 제공함

<br>

## 기술 스택
- Backend
    - Java 17
    - Spring Boot 3.5.7
- Database
    - Spring Data JPA, H2
- API Documentation
    - `springdoc-openapi-starter-webmvc-ui:2.5.0`

<br>

## 주요 기능

- **쿠폰 대량 발급**
    - CSV, Excel 파일 업로드를 통해 다수의 사용자에게 쿠폰을 대량 발급함
    - JPA/JDBC 배치 기능을 활용한 대량 삽입을 적용함
- **파일 유효성 검증**
    - 파일 헤더가 `customer_id`인지, 사용자 목록이 비어있지 않은지 검증함
- **파일 다운로드**
    - 업로드된 파일을 원본 파일명으로 다운로드하는 기능을 제공함
- **환경별 설정 분리**
    - Spring Profiles (`dev`, `prod`)를 사용하여 개발 및 운영 환경의 설정을 분리함
- **API 문서**
    - Swagger UI를 통해 API 명세를 제공함
- **공통 예외 처리**
    - `@RestControllerAdvice`를 통해 전역 예외 핸들러를 구현하고 일관된 형식의 에러를 응답함

<br>

## API 명세

- 애플리케이션 실행 후 아래 Swagger UI 주소로 접속하여 API 명세를 확인하고 직접 테스트할 수 있음
- **Swagger UI**
    - `http://localhost:8080/swagger-ui.html`

### 파일 업로드 API
- **Endpoint**
    - `POST /api/coupon-issuances/upload`
- **Description**
    - CSV 또는 Excel 형식의 사용자 목록 파일을 업로드하여 쿠폰을 대량 발급함
- **Request**
    - `Content-Type`
        - `multipart/form-data`
    - `Request Parameters`
        - `file` (MultipartFile)
            - 쿠폰을 발급할 사용자 ID 목록이 포함된 파일
        - `couponName` (String)
            - 발급할 쿠폰의 이름
        - `expiresAt` (String, `yyyy-MM-dd'T'HH:mm:ss`)
            - 쿠폰 만료일시
- **Success Response**
  - `201 CREATED`
      ```json
      {
          "fileId": "generated_uuid_string"
      }
      ```
- **Error Response**
    - `400 Bad Request`
        - 파일 비어있음, 헤더 유효하지 않음, 지원하지 않는 파일 형식 등
    - `500 Internal Server Error`
        - 파일 저장 실패 등 서버 내부 오류

### 파일 다운로드 API
- **Endpoint**
    - `GET /api/coupon-issuances/{fileId}/download`
- **Description**
    - 업로드 시 반환된 파일 ID를 사용하여 원본 파일을 다운로드함
- **Request**
    - `Path Variable`
        - `fileId` (String)
            - 업로드 시 발급된 파일의 고유 ID
- **Success Response**
  - `200 OK`
      - `Content-Type`
          - `application/octet-stream`
      - `Body`
          - 업로드된 원본 파일의 바이너리 데이터
- **Error Response**
    - `404 Not Found`
        - 해당 `fileId`의 파일을 찾을 수 없는 경우

<br>

## 실행 및 테스트 방법

### 애플리케이션 실행
- **개발 환경**
    - 별도 설정 없이 실행 시 `application-dev.properties`가 적용됨
- **운영 환경**
    - JAR 파일 실행 시 `-Dspring.profiles.active=prod` 옵션을 추가하여 `application-prod.properties`를 적용함
    - `java -jar -Dspring.profiles.active=prod build/libs/coupon-admin-0.0.1-SNAPSHOT.jar`

### 단위 테스트 실행
- 아래 명령어를 통해 단위 테스트를 실행함
   ```sh
   ./gradlew test
   ```

### API 및 데이터베이스 확인
- **Swagger UI**
    - `http://localhost:8080/swagger-ui.html`
- **H2 Console**
    - `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:testdb`)
