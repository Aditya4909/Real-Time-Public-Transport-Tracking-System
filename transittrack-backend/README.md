# TransitTrack – Real-Time Public Transport Tracking System

A production-grade backend application built with **Java 21** and **Spring Boot 3** that powers public transport tracking, fleet management, dynamic ETA calculation, and live telemetry broadcasting via WebSockets.

---

## 🚀 Tech Stack

- **Language**: Java 21
- **Framework**: Spring Boot 3.3.4
- **Security**: Spring Security 6, JWT (JJWT 0.12.5), BCrypt
- **ORM & Data**: Spring Data JPA, Hibernate, MySQL 8.0 / PostgreSQL / H2
- **Real-Time Communication**: Spring WebSocket with STOMP over SockJS
- **API Documentation**: Springdoc OpenAPI 3 / Swagger UI
- **Build Tool**: Maven
- **Containerization**: Docker & Docker Compose

---

## 🏛️ Architecture

```
Controller  ──▶  Service  ──▶  Repository  ──▶  Database (MySQL)
    ▲                │
    │                ▼
Clients      SimpMessagingTemplate (WebSocket Broker)
 (HTTP)              │
                     ▼
             Subscribers (/topic/...)
```

---

## 🔑 Default Seed Accounts

Upon first launch, `DataInitializer` seeds the following demo credentials:

| Role | Username | Password | Email | Notes |
|---|---|---|---|---|
| **ADMIN** | `admin` | `admin123` | admin@transittrack.com | Full management & dashboard access |
| **DRIVER** | `driver_john` | `password123` | john.driver@transittrack.com | License: `DL-982341-X`, assigned to `BUS-101` |
| **USER** | Can register via `/api/v1/auth/register` |

### Pre-seeded Transit Entities:
- **Route**: `R-101` (Downtown - Airport Express, 16.5 km, 35 mins)
- **Stops**: `ST-01` (Central Station) ➔ `ST-02` (University Campus) ➔ `ST-03` (City Mall) ➔ `ST-04` (Tech Park) ➔ `ST-05` (Airport Terminal 1)
- **Vehicle**: `BUS-101` (Volvo 7900 Electric, 65 capacity, active on Route `R-101`)

---

## 🛠️ Getting Started

### Option 1: Running with Docker Compose (Recommended)

Make sure Docker Desktop is installed and running:

```bash
cd transittrack-backend
docker-compose up --build
```

The database container and Spring Boot application will start and initialize automatically.

### Option 2: Running Locally with Maven & MySQL

1. **Verify MySQL 8.0 is running** on `localhost:3306`.
2. Configure credentials in `src/main/resources/application.yml` or set environment variables:
   ```bash
   set DB_URL=jdbc:mysql://localhost:3306/transittrack_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true
   set DB_USERNAME=root
   set DB_PASSWORD=root
   ```
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

### Option 3: In-Memory H2 Mode (Zero DB Setup Required)

If you wish to test without MySQL:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```
H2 Console will be available at: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:transittrack_dev`, User: `sa`, Password: empty).

---

## 📖 Swagger / OpenAPI Interactive Documentation

Access the Swagger UI at:
**[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**

Click the **Authorize** button and input the Bearer token received from `/api/v1/auth/login` to execute authenticated requests directly from your browser.

---

## 📡 WebSocket Real-Time Telemetry Guide

- **Connection Handshake Endpoint**: `http://localhost:8080/ws-transit`
- **Application Inbound Prefix**: `/app`
- **Broker Outbound Prefix**: `/topic`

### Subscribing to Live Vehicle Location:
- Destination: `/topic/vehicles/{vehicleId}/location`
- Example: `/topic/vehicles/1/location`

### Subscribing to All Vehicles on a Route:
- Destination: `/topic/routes/{routeId}/locations`
- Example: `/topic/routes/1/locations`

### Driver Telemetry Format (JSON):
```json
{
  "vehicleId": 1,
  "latitude": 40.712776,
  "longitude": -74.005974,
  "speedKmh": 32.5,
  "heading": 45.0
}
```

---

## 🧪 Sample cURL Commands

### 1. Admin Login
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail": "admin", "password": "admin123"}'
```

### 2. Search Transit Routes
```bash
curl -X GET "http://localhost:8080/api/v1/routes/search?keyword=Airport"
```

### 3. Calculate Vehicle ETAs for All Stops
```bash
curl -X GET "http://localhost:8080/api/v1/vehicles/1/eta"
```

### 4. Transmit Live GPS Telemetry (Driver)
```bash
curl -X POST http://localhost:8080/api/v1/tracking/location \
  -H "Authorization: Bearer <YOUR_JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "vehicleId": 1,
    "latitude": 40.729100,
    "longitude": -73.996500,
    "speedKmh": 35.0,
    "heading": 90.0
  }'
```

### 5. Admin Dashboard Statistics
```bash
curl -X GET http://localhost:8080/api/v1/admin/dashboard/stats \
  -H "Authorization: Bearer <ADMIN_JWT_TOKEN>"
```
