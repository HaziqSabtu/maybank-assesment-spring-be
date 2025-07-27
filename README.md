# Maybank Assessment - Spring BE

A simple social media backend example built with Spring Boot. The app allows users to create, view, and save places, as well as follow each other.

Test now at: https://spring-app-1060231715178.asia-southeast1.run.app/swagger-ui/index.html

Performance might be a bit slow, running on a free tier of Cloud Run.

## ✨ Features

- 👤 User account creation and authentication
- 📍 Ability to create, view, and save favorite places
- 🔗 Functionality to follow and unfollow other users
- ⚡ Improved performance through Caffeine caching
- ✅ Unit tests implemented for core services and business logic

## 🛠️ Tech Stack

- ☕ Java 21
- 🌱 Spring Boot 3
- 🔐 Spring Security (JWT-based authentication)
- 🗃️ Spring Data JPA with Hibernate
- 🔌 JDBC for database access
- 🛢️ Microsoft SQL Server (MSSQL)
- 🧠 Caffeine for in-memory caching
- 🧪 JUnit & Mockito for unit testing
- 🛠️ Gradle for build and dependency management
- 🧼 Jakarta Bean Validation (for DTO sanitization and validation)
- 🔎 Spring Null Annotations (e.g., `@NonNull` for static analysis)

## 🔗 API Endpoints

Below is a list of available API endpoints with brief descriptions of their functionality:

### 🔐 Authentication
- `POST /auth/login`  
  Authenticates a user and returns a JWT token.
- `POST /auth/register`  
  Registers a new user with basic credentials.

---

### 👤 User Management
- `GET /users/:userId`  
  Retrieves detailed profile information for the specified user.
- `GET /users/:userId/summary`  
  Fetch data from 3rd party APIs based on the user's country.
- `GET /users/:userId/followers`  
  Lists users who follow the specified user.
- `GET /users/:userId/followees`  
  Lists users the specified user is following.
- `GET /users/:userId/followees/:followeeId`  
  Checks if the user is following a specific target user.
- `POST /users/follow`  
  Follows another user.
- `DELETE /users/follow`  
  Unfollows another user.

---

### 📍 Places
- `POST /places`  
  Adds a new place created by the authenticated user.
- `GET /places`  
  Retrieves a paginated list of saved places.
- `GET /places/:placeId`  
  Fetches detailed information about a specific place.
- `DELETE /places/:placeId`  
  Deletes a place by ID (if owned by the user).


## ⚙️ Setup Instructions

### 1. 🖥️ Running Locally

Ensure you have the following prerequisites installed:
- Java 21
- An accessible MSSQL database running locally (default: `localhost:1433`)

> ℹ️ You may modify the database configuration in `src/main/resources/application.properties` if your setup differs.

```bash
# Build the project
./gradlew build

# Run the application
./gradlew bootRun
```

### 2. 🐳 Running with Docker Compose
To spin up the entire environment (app + database) using Docker Compose:
```bash
docker compose up --build
```

### ✅ Accessing the Application
Once the app is running, you can access it at:
```bash
http://localhost:8080
```

### ❓ Having trouble setting up locally?

You can test the deployed version at: https://spring-app-1060231715178.asia-southeast1.run.app

## Other Resources

### Scripts folder

This folder contains scripts that can be used to automate various tasks.

- Dockerfile - Used to build the Docker image for the application.
- init.sql - Used to initialize the database tables.
- seed.sql - Used to seed the database with sample data.

### Postman Collection

The Postman collection for testing the API is made available in the `postman/` folder.

You can import the collection into your Postman environment to interact with the API directly.