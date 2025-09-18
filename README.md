# Know-Before-You-Buy Backend

Database setup and CRUD API for the "Know-Before-You-Buy" project.

This document serves as a comprehensive guide to the `know-before-you-buy` backend application. It covers everything from the project's architecture and setup to its API endpoints, security, and instructions for reproducibility.

## Table of Contents

1. [Project Overview](#1-project-overview)  
2. [Technology Stack](#2-technology-stack)  
3. [Setup and Execution](#3-setup-and-execution)  
4. [API Endpoints](#4-api-endpoints)  
5. [Authentication and Security](#5-authentication-and-security)  
6. [Logging for Traceability](#6-logging-for-traceability)  
7. [Reproducibility](#7-reproducibility)  
8. [Postman Setup](#8-postman-setup)  

---

### 1. Project Overview

The core purpose of this project is to provide a robust backend for a service that helps users make informed purchasing decisions. It uses a three-part data model:

- **`Device`**: Represents a product (e.g., "Smart TV", "Smart Speaker").  
- **`PrivacyProperty`**: Represents a specific privacy-related attribute (e.g., "Data Encryption", "Microphone Use").  
- **`DevicePrivacyValue`**: A join entity that connects a `Device` to a `PrivacyProperty` and stores the specific value for that property (e.g., a "Smart TV" having a "Data Encryption" value of "AES-256").  

The application is built as a RESTful API using the Spring Boot framework, allowing a frontend application to perform full CRUD operations on this data.

---

### 2. Technology Stack

- **Backend Framework**: Spring Boot  
- **Language**: Java 21  
- **Build Tool**: Maven  
- **Database**: PostgreSQL  
- **ORM**: Spring Data JPA  

**Dependencies:**

- `spring-boot-starter-web`: For building the REST API  
- `spring-boot-starter-data-jpa`: For simplified data access with JPA  
- `org.postgresql`: PostgreSQL database driver  
- `lombok`: Reduces boilerplate code for models and DTOs  
- `springdoc-openapi-starter-webmvc-ui`: For generating interactive API documentation (Swagger UI)  

**Security:** A custom filter for API key-based authentication.

---

### 3. Setup and Execution

To run the application locally, you will need a PostgreSQL database instance.

1. **Configure the Database**  

   Open `src/main/resources/application.properties` and adjust:

   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/your_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

2. **Build the Project**  

   Open a terminal in the project's root directory and run:

   ```bash
   ./mvnw clean package
   ```

3. **Run the Application**  

   Execute the generated JAR file:

   ```bash
   java -jar target/know-before-you-buy-0.0.1-SNAPSHOT.jar
   ```

   The application will start on `https://localhost:8443` by default.

---

### 4. API Endpoints

The API documentation, including all available endpoints and their required parameters, is automatically generated and can be accessed at:  

```
https://localhost:8443/swagger-ui.html
```

The endpoints are categorized by the entity they manage: `devices`, `privacy-properties`, and `device-privacy-values`.

**Note on GET Endpoints:** All `GET` endpoints are publicly accessible and do not require authentication.

---

### 5. Authentication and Security

All write-related endpoints (`POST`, `PUT`, `DELETE`) are protected by a single secret API key. Read-only `GET` endpoints remain open.

#### Implementation Details

The security mechanism is implemented via a custom servlet filter, `ApiKeyFilter.java`, configured in `WebConfig.java`.

1. **Request Interception**: `ApiKeyFilter` checks for a custom HTTP header named `X-API-KEY`.  
2. **Key Validation**: Compares the header value against a secret key from the application's configuration.  
3. **Authentication**: If valid, the request proceeds; otherwise, a `401 Unauthorized` response is returned.

#### Using the API Key

Include the `X-API-KEY` header in your request:

```bash
curl -X POST "https://localhost:8443" \
-H "Content-Type: application/json" \
-H "X-API-KEY: YOUR_SECRET_API_KEY" \
-d '{ "name": "New Smart Watch" }'
```

#### Production Configuration

1. **Secure the API Key**  

```properties
application.api.key=YOUR_SECRET_API_KEY_HERE
```

2. **Enable HTTPS**: Ensure the API runs exclusively over HTTPS to prevent key leakage.

---

### 6. Logging for Traceability

All API requests are logged for traceability using `RequestLoggingFilter.java`, recording the request method and URI for a clear audit trail.

---

### 7. Reproducibility

Steps to replicate the project's environment:

- **Source Code**: Located in `src/main/java/com/kbyb/know_before_you-buy`.  
- **Database Schema**: Automatically created via Spring Data JPA based on entities.  
- **Data Import**: `PrivacyPropertiesCSV.csv` is loaded at startup via `DataLoaderConfig.java`.  
- **Configuration**: Managed in `src/main/resources/application.properties`.  
- **CSV Data Ingestion**: Use `AllDevicesCSV.csv` with the endpoint `/devices/csv` in Postman (`form-data` key `file`).  
- **Build Tool**: Maven (`pom.xml` contains all dependencies and plugins).

---

### 8. Postman Setup

To test the API with Postman:

1. **Create an Environment Variable**  
   - **Variable Name:** `{{baseUrl}}`  
   - **Value:** `https://localhost:8443`  

   Use `{{baseUrl}}` as the base URL for all requests in your Postman collection. For example:

   ```
   GET {{baseUrl}}/devices
   POST {{baseUrl}}/devices
   ```

2. **Postman Collection**  
   A ready-to-use Postman collection is included in the project folder:

   ```
   /postman/KnowBeforeYouBuy.postman_collection.json
   ```

   **To import it into Postman:**  
   - Open Postman → Click **Import** → Select **File** → Choose the `KBYB-API.postman_collection.json` file.  
   - After import, select the previously created environment (`{{baseUrl}}`) to ensure all requests point to your local API instance.  

For write operations (`POST`, `PUT`, `DELETE`), make sure to include the `X-API-KEY` header in your requests.
