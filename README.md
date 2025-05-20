# 🌐 URL Shortener Service

## 📝 Overview

The **URL Shortener Service** is a backend microservice designed to convert long URLs—such as referral or tracking links—into short, user-friendly URLs. This is particularly beneficial for sharing links on social media platforms, where shorter URLs improve readability and aesthetics.

This service is part of a larger **microservice-based URL shortening platform**, which consists of the following components:

- **URL Shortener Service** (current microservice)
- **URL Audit Service** (for tracking and analytics)
- **Frontend Application** (for user interaction)

---

## 🔗 Source Code Repositories

- 👉 [**Frontend Application**](https://github.com/dobrevd/url-shortener-frontend)  
  *Angular-based user interface for shortening and managing URLs.*

- 👉 [**URL Shortener Service (Backend)**](https://github.com/dobrevd/url_shortener_service)  
  *Core logic for generating and resolving short links.*

- 👉 [**URL Audit Service**](https://github.com/dobrevd/url-audit-service)  
  *Tracks and logs usage of shortened URLs for analysis and reporting.*


## Features

The primary function of the service is to **shorten URLs**, but it also includes several advanced features such as:

- **Caching**: Both local (in-memory) and using Redis for improved performance.
- **Multithreading**: Handles multiple requests concurrently for efficient processing.
- **Database Integration**: Works with both Postgres and Redis databases.
- **REST API**: Exposes a clean, REST-based interface for easy interaction.
- **Resource Reuse Scheduler**: Optimizes resource management by scheduling the reuse of resources.

## Performance Optimization

To further optimize the microservice’s performance, I’ve implemented a **local cache** directly in memory using a **thread-safe data structure**, significantly improving overall efficiency and reducing latency.

## Microservice Interaction

Main service will communicate with the URL Shortener through its **REST API**, enabling real microservice-based interaction. This setup allows us to address the various challenges that typically arise in microservice architectures, such as scalability, reliability, and service discovery.

## GitHub Actions Workflow

### 📌 Overview
This GitHub Actions workflow automates the **build and deployment** process for the Url Shortener Service application.

### 🚀 Trigger Conditions
- Runs on **push** and **pull request** events to the `master` branch.

### 🛠️ Build Job (`build`)
✅ **Steps:**
- 🏗️ **Checkout Repository** – Clones the project repository.
- 🔧 **Set Permissions** – Grants execute permissions to the Gradle wrapper.
- ☕ **Set up JDK 17** – Installs Temurin JDK 17.
- ⚙️ **Configure Gradle** – Sets up Gradle for dependency management.
- 🏗️ **Build Project** – Runs `./gradlew build -x test` to compile the application.
- 🧪 **Run Tests with JaCoCo** – Executes tests and generates a code coverage report using JaCoCo (via ./gradlew test jacocoTestReport).
- 📦 **Save JaCoCo Report** – Uploads the generated JaCoCo report as an artifact to track test coverage.
- 📦 **Save Artifact** – Stores the generated JAR file for later use.

### 🐳 Docker Job (`docker`)
✅ **Steps:**
- 📥 **Download JAR Artifact** – Retrieves the built application from the previous job.
- 🧐 **Verify JAR File** – Ensures the artifact is available.
- 🔐 **Log in to Docker Hub** – Uses GitHub Secrets for authentication.
- 🏗️ **Build Docker Image** – Creates a Docker image for the application.
- 📤 **Push to Docker Hub** – Publishes the Docker image for deployment.

### 🔄 CI/CD Process
This workflow ensures **continuous integration and deployment**, making the application **automatically available as a Docker image** on every update to the `master` branch.

## The **Url Shortener Service** can be run locally using **Kubernetes**.

## 🧪 Code Coverage with JaCoCo

**Url Shortener Service** uses **JaCoCo** to generate code coverage reports and enforce a minimum coverage threshold during testing.

- A **minimum line coverage threshold of 68%** is enforced.
- The **build will fail** if the actual coverage is below this threshold.
- Test coverage reports are generated in multiple formats: **HTML** and **XML**.

### 📁 View the Report


![img.png](img.png)