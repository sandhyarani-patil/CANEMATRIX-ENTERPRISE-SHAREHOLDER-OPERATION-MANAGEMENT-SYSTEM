"CANEMATRIX-ENTERPRISE SHAREOLDER OPERATION MANAGEMENT SYSTEM"

CaneMatrix is a full-stack management system designed for sugar factories to digitize and centralize their day-to-day farmer and cane-related operations. Traditionally, factories maintain this data across paper registers or scattered spreadsheets — farmer details in one place, cane supply entries in another, share records somewhere else. CaneMatrix brings all of this into a single, structured application, with a Spring Boot backend and a React frontend.

The system covers the complete lifecycle of a farmer's engagement with the factory:

- **Farmer Records** – Registering farmers along with their bank details, farm details, and nominee information, all linked to a single farmer profile.
- **Sugarcane Supply Tracking** – Logging each cane delivery from a farmer, which becomes the base data for all downstream calculations.
- **Share Allocation** – Calculating and managing how factory shares are allocated to each farmer.
- **Tonnes-Based Sugar Allocation** – Determining sugar allocation based on the tonnage of cane a farmer has supplied, with historical tracking for audits.
- **Share Transfers** – Recording transfers of shares from one farmer to another, keeping an accurate ownership trail.
- **Reporting** – Generating Excel reports for cane supply and share allocation, so factory staff can review or submit data without manually compiling it.

The goal is simple: replace manual, error-prone register-keeping with one reliable system that gives factory staff and farmers a clear, accurate, and auditable record of every transaction.

 Tech Stack

**Backend**
- Java 21, Spring Boot 3.3.4
- Spring Data JPA + MySQL
- Spring Security with JWT authentication
- ModelMapper for DTO mapping
- springdoc-openapi (Swagger UI)
- Apache POI (Excel reports), Apache PDFBox (PDF generation)
- Maven

**Frontend**
- React 19 + Vite
- Tailwind CSS 4
- React Router
- Axios
- Recharts (dashboard charts)
- react-hot-toast

## Features

- **Authentication** – JWT-based login for admin/clerk users and a separate login for farmers, with role-based access (`ADMIN`, `CLERK`, `FARMER`).
- **Farmer Management** – Add, update, and view farmers along with their bank details, farm details, and nominee information.
- **Sugarcane Supply** – Record and track sugarcane supplied by each farmer.
- **Share Allocation** – Manage share allocation and share-based sugar allocation for farmers.
- **Share Transfer** – Handle transfer of shares between farmers.
- **Tonnes Sugar Allocation** – Allocate sugar based on tonnes supplied, with history tracking.
- **Festival Sugar** – Manage festival sugar master data and distribution.
- **Sugar Factory Rate** – Maintain factory rate records used across calculations.
- **Reports** – Export sugarcane supply and share allocation reports as Excel files.
- **API Documentation** – Auto-generated Swagger UI for all backend endpoints.

## Project Structure

```
CaneMatrix/                # Backend (Spring Boot)
  src/main/java/com/example/farmer/canematrix/
    controller/            # REST controllers
    service/                # Business logic
    repository/             # JPA repositories
    entity/                 # Database entities
    dto/                     # Request/response objects
    security/                # JWT + Spring Security config
    config/                  # App-level configuration
  src/main/resources/
    application.properties

canematrix-frontend/       # Frontend (React + Vite)
  src/
    pages/                  # Feature pages (farmers, supply, reports, etc.)
    components/             # Reusable UI components
    context/                 # React context (auth, etc.)
    routes/                  # Route guards
    lib/                     # Axios instance and API service calls
```

## Prerequisites

- Java 21
- Maven (or use the included `mvnw` wrapper)
- Node.js 18+ and npm
- MySQL 8+

## Getting Started

### 1. Backend Setup

Create a MySQL database:

```sql
CREATE DATABASE canematrix;
```

Update `src/main/resources/application.properties` with your own database credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/canematrix
spring.datasource.username=your_username
spring.datasource.password=your_password
```

> Note: Avoid committing real database credentials to GitHub. Consider moving these to environment variables or a local `application-local.properties` file that is excluded via `.gitignore`.

Run the backend:

```bash
cd CaneMatrix
./mvnw spring-boot:run
```

The backend starts on `http://localhost:8080`. Tables are auto-created/updated on startup (`spring.jpa.hibernate.ddl-auto=update`).

Swagger UI is available at:

```
http://localhost:8080/swagger-ui.html
```

### 2. Frontend Setup

```bash
cd canematrix-frontend
npm install
npm run dev
```

The frontend starts on `http://localhost:5173`.

By default, the frontend calls the backend at `http://localhost:8080/api`. To point it somewhere else, create a `.env` file in `canematrix-frontend/`:

```
VITE_API_BASE_URL=http://localhost:8080/api
```

### 3. Build for Production

Backend:
```bash
./mvnw clean package
java -jar target/CaneMatrix-0.0.1-SNAPSHOT.jar
```

Frontend:
```bash
npm run build
```

## API Overview

All backend endpoints are prefixed with `/api`. Main endpoint groups:

| Module | Base path |
|---|---|
| Auth | `/api/auth` |
| Farmers | `/api/farmers` |
| Sugarcane Supply | `/api/sugarcane-supply` |
| Share Allocation | `/api/share-allocation` |
| Share Sugar Allocation | `/api/share-sugar-allocation` |
| Share Transfer | `/api/share-transfer` |
| Tonnes Sugar Allocation | `/api/tonnes-sugar-allocation` |
| Festival Sugar | `/api/festival-sugar` |
| Sugar Factory Rate | `/api/sugar-factory-rate` |
| Reports | `/api/reports` |

Full request/response details are available through Swagger UI once the backend is running.

## Roadmap / Possible Improvements

- Add pagination and search filters to list endpoints
- Move sensitive configuration to environment variables
- Add unit and integration test coverage
- Dockerize backend and frontend for easier deployment



