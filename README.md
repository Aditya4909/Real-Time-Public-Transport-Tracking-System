# TransitTrack – Real-Time Public Transport Tracking System

[![TransitTrack Backend CI & Build](https://github.com/Aditya4909/Real-Time-Public-Transport-Tracking-System/actions/workflows/backend-ci.yml/badge.svg)](https://github.com/Aditya4909/Real-Time-Public-Transport-Tracking-System/actions/workflows/backend-ci.yml)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-6.x-green.svg)](https://spring.io/projects/spring-security)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

A high-performance, enterprise-grade public transit tracking and fleet management system built with **Java 21**, **Spring Boot 3**, **Spring Security 6**, **WebSocket (STOMP)**, and **React**.

TransitTrack enables transit authorities and administrators to manage vehicles, routes, and sequenced stops, empowers drivers to transmit live GPS telemetry, and allows commuters to track buses in real-time with dynamic Estimated Time of Arrival (ETA) predictions.

---

## 🏛️ High-Level Architecture

```
                                 [ Web & Mobile Clients ]
                                             │
                       ┌─────────────────────┴─────────────────────┐
                       ▼ (HTTP REST)                               ▼ (WebSocket STOMP)
             ┌─────────────────────────────────────────────────────────────┐
             │                Spring Security 6 Filter Chain               │
             │           - JWT Authentication (HMAC-SHA256)                │
             │           - Role-Based Access (USER, DRIVER, ADMIN)         │
             └──────────────────────────────┬──────────────────────────────┘
                                            ▼
             ┌─────────────────────────────────────────────────────────────┐
             │                 API Layer (Controllers)                     │
             │   AuthController         VehicleController  RouteController │
             │   StopController         TrackingController AdminController │
             │   WebSocketTrackingController (/ws-transit)                 │
             └──────────────────────────────┬──────────────────────────────┘
                                            ▼
             ┌─────────────────────────────────────────────────────────────┐
             │                 Service Layer & ETA Engine                  │
             │   - Haversine Geospatial Math for dynamic arrival ETA       │
             │   - SimpMessagingTemplate (Telemetry Topic Broadcasting)    │
             └──────────────────────────────┬──────────────────────────────┘
                                            ▼
             ┌─────────────────────────────────────────────────────────────┐
             │            Data Access Layer (Spring Data JPA)              │
             └──────────────────────────────┬──────────────────────────────┘
                                            ▼
             ┌─────────────────────────────────────────────────────────────┐
             │               Relational Database (MySQL 8.0)               │
             │   users, roles, vehicles, routes, stops, route_stops, logs  │
             └─────────────────────────────────────────────────────────────┘
```

---

## 🚀 Tech Stack

### Backend (`transittrack-backend/`)
- **Language**: Java 21
- **Framework**: Spring Boot 3.3.4
- **Security**: Spring Security 6, JJWT (0.12.5), BCrypt Password Hashing
- **Data & Persistence**: Spring Data JPA, Hibernate ORM, MySQL 8.0 / H2 In-Memory
- **Real-Time Communication**: Spring WebSocket with STOMP over SockJS
- **API Documentation**: Springdoc OpenAPI 3 / Swagger UI
- **Containerization**: Docker, Docker Compose (Multi-stage Temurin 21 build)
- **CI/CD**: GitHub Actions automated build & test pipeline

### Frontend (`client/`)
- **Framework**: React.js
- **Mapping**: Leaflet / React-Leaflet
- **Real-Time Feed**: WebSocket / STOMP client
- **Analytics**: Recharts
- **Styling**: Modern CSS3 responsive layouts

---

## 👥 User Roles & Core Features

### 👤 USER (Commuter)
- **Route Search**: Find routes by keyword, origin, or destination.
- **Ordered Stop Sequence**: View all intermediate stops with sequence ordering and distances.
- **Live Vehicle Tracking**: Real-time vehicle pin updates via WebSockets.
- **Arrival ETA Prediction**: Calculate arrival times for all incoming vehicles approaching any stop.
- **Nearby Stops**: Discover transit stops within a given radius using GPS coordinates.

### 🚌 DRIVER
- **Telemetry Transmission**: Transmit live GPS coordinates (latitude, longitude, speed, heading) via REST or WebSocket STOMP.
- **Assigned Vehicle & Route**: Automatic status activation upon sending telemetry pings.

### 👨‍💼 ADMIN
- **Fleet Management**: Register, update, inspect, and decommission vehicles.
- **Driver Allocation**: Assign licensed drivers to fleet vehicles.
- **Route Management**: Define routes, assign vehicles, and sequence intermediate stops with incremental travel times.
- **Telemetry Audit Log**: Retrieve historical GPS breadcrumb logs for any vehicle.
- **Dashboard Analytics**: Real-time overview of fleet operational status (Active, Inactive, Maintenance, total routes, active drivers).

---

## 🔑 Pre-Seeded Default Accounts

The system automatically initializes default seed data on startup:

| Role | Username | Password | Email | Details |
|---|---|---|---|---|
| **ADMIN** | `admin` | `admin123` | admin@transittrack.com | Full fleet and dashboard control |
| **DRIVER** | `driver_john` | `password123` | john.driver@transittrack.com | License: `DL-982341-X`, assigned to `BUS-101` |
| **USER** | Register new account via `/api/v1/auth/register` |

### Pre-Configured Transit Route & Stops:
- **Route**: `R-101` (Downtown - Airport Express, 16.5 km, 35 mins)
- **Stops**: `ST-01 Central Station` ➔ `ST-02 University Campus` ➔ `ST-03 City Mall` ➔ `ST-04 Tech Park` ➔ `ST-05 Airport Terminal 1`
- **Vehicle**: `BUS-101` (Volvo 7900 Electric, allocated to Route `R-101`)

---

## ⚡ Quick Start & Execution

### Option 1: Docker Compose (Recommended)

From the project root:
```bash
cd transittrack-backend
docker-compose up --build
```
This automatically boots:
1. **MySQL 8.0** on `localhost:3306` with persistent storage.
2. **TransitTrack Spring Boot** backend on `http://localhost:8080`.

### Option 2: Local Run with Maven & MySQL

1. Verify MySQL 8.0 is running locally on port 3306.
2. Configure credentials in `transittrack-backend/src/main/resources/application.yml` or export environment variables:
   ```bash
   set DB_URL=jdbc:mysql://localhost:3306/transittrack_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true
   set DB_USERNAME=root
   set DB_PASSWORD=root
   ```
3. Run with Maven:
   ```bash
   cd transittrack-backend
   mvn spring-boot:run
   ```

### Option 3: In-Memory H2 Mode (Zero Database Setup Required)

To test the backend immediately without configuring MySQL:
```bash
cd transittrack-backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```
- H2 Console: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:transittrack_dev`, User: `sa`, Password: empty)

---

## 📖 Interactive Swagger / OpenAPI Documentation

Explore and test all REST endpoints interactively via Swagger UI:
👉 **[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**

1. Call `POST /api/v1/auth/login` with `{"usernameOrEmail": "admin", "password": "admin123"}`.
2. Copy the `accessToken`.
3. Click the **Authorize 🔓** button at the top right, paste the token, and click **Authorize**.
4. Test all protected Admin, Driver, and User endpoints directly from the browser!

---

## 📡 WebSocket STOMP Real-Time Guide

- **Connection URL**: `http://localhost:8080/ws-transit` (with SockJS fallback)
- **Application Inbound Prefix**: `/app`
- **Broker Outbound Prefix**: `/topic`

### Subscriptions:
- **Track a Specific Vehicle**: `/topic/vehicles/{vehicleId}/location`
- **Track All Vehicles on a Route**: `/topic/routes/{routeId}/locations`

### Driver Telemetry Format:
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

## 📋 REST API Summary

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/v1/auth/login` | Public | Authenticate user & issue JWT |
| `POST` | `/api/v1/auth/register` | Public | Register commuter or driver account |
| `GET` | `/api/v1/auth/me` | Authenticated | Get current user profile |
| `POST` | `/api/v1/vehicles` | `ADMIN` | Register new fleet vehicle |
| `GET` | `/api/v1/vehicles` | Public | List fleet vehicles (paginated) |
| `GET` | `/api/v1/vehicles/{id}` | Public | Get vehicle details |
| `POST` | `/api/v1/vehicles/{id}/assign-driver` | `ADMIN` | Assign driver to vehicle |
| `POST` | `/api/v1/vehicles/{id}/assign-route` | `ADMIN` | Allocate vehicle to route |
| `GET` | `/api/v1/vehicles/{id}/location` | Public | Get latest GPS telemetry |
| `GET` | `/api/v1/vehicles/{id}/eta` | Public | Calculate vehicle arrival ETAs for all stops |
| `POST` | `/api/v1/routes` | `ADMIN` | Create transit route |
| `GET` | `/api/v1/routes` | Public | List routes (paginated) |
| `GET` | `/api/v1/routes/{id}` | Public | Get route with ordered stops |
| `POST` | `/api/v1/routes/{id}/stops` | `ADMIN` | Add sequenced stop to route |
| `GET` | `/api/v1/routes/search` | Public | Search routes by keyword |
| `GET` | `/api/v1/routes/{id}/eta` | Public | Get incoming vehicle ETAs for a stop |
| `POST` | `/api/v1/stops` | `ADMIN` | Create physical transit stop |
| `GET` | `/api/v1/stops/nearby` | Public | Find stops within coordinate radius |
| `POST` | `/api/v1/tracking/location` | `DRIVER`, `ADMIN` | Transmit live GPS coordinates |
| `GET` | `/api/v1/tracking/vehicles/{id}/history` | `ADMIN` | Get telemetry audit trail |
| `GET` | `/api/v1/admin/dashboard/stats` | `ADMIN` | Summary metrics of fleet & routes |
| `GET` | `/api/v1/admin/dashboard/active-vehicles`| `ADMIN` | Live snapshot of all active vehicles |

---

## 🚢 Cloud Deployment (Render)

A multi-service configuration is provided in [`render.yaml`](render.yaml) for one-click deployment:
- Connect the repository to [Render](https://dashboard.render.com) using the Blueprint option.
- Render will automatically build the `transittrack-spring-backend` container using the multi-stage Dockerfile and launch the service.

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
