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

- **`Device`**: Represents a product.
- **`PrivacyProperty`**: Represents a specific privacy-related attribute.
- **`DevicePrivacyValue`**: A join entity that connects a `Device` to a `PrivacyProperty` and stores the specific value for that property.

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

The security mechanism is implemented via a custom servlet filter, `ApiKeyFilter.java`, configured in the `security` folder.

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

This section outlines all the components and steps required to replicate the project's environment and functionality.

* **Source Code**: The complete source code is available in this repository. The main application logic is contained within the `src/main/java/com/kbyb/know_before_you-buy` directory.

* **Git Repository**: The project's development history and code are managed using Git. You can access the repository at https://github.com/robinLoe/know-before-you-buy/commits/Try-without-cascade/ to clone the project and view the complete commit history.

* **Database Schema**: The application uses Spring Data JPA to automatically create the database schema on startup. No separate SQL schema file is required. The entities (`Device.java`, `PrivacyProperty.java`, `DevicePrivacyValue.java`) define the table structure.

* **Data Import**: A CSV file named `PrivacyPropertiesCSV.csv` is included in `src/main/resources/`. The `DataLoaderConfig.java` class is configured to automatically load this data into the database on application startup if the database is empty.

* **Configuration**: All configuration, including database credentials and the API key, is managed in `src/main/resources/application.properties`.

* **Ingestion of Analyzed Data via Bulk Import**: A CSV file named `AllDevicesCSV.csv` is included in `src/main/resources/`. You can use Postman with the endpoint `https://localhost:8443/devices/csv` and under the tab “Body” you select “form-data,” enter “file” as Key and upload the CSV file as the corresponding Value (make sure the type is set to “File”). Then the data is ingested into the database.

* **Build Tool**: The project uses Maven, with the `pom.xml` file detailing all required dependencies and build plugins for a reproducible build process.

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
   - Open Postman → Click **Import** → Select **File** → Choose the `KnowBeforeYouBuy.postman_collection.json` file.  
   - After import, select the previously created environment (`{{baseUrl}}`) to ensure all requests point to your local API instance.  

For write operations (`POST`, `PUT`, `DELETE`), make sure to include the `X-API-KEY` header in your requests.