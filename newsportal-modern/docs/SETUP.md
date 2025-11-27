# Development Setup Guide

Complete guide to setting up the newsportal-modern development environment.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Local Development Setup](#local-development-setup)
- [Database Setup](#database-setup)
- [IDE Configuration](#ide-configuration)
- [Running the Application](#running-the-application)
- [Development Workflow](#development-workflow)

## Prerequisites

### Required Software

| Software | Version | Purpose |
|----------|---------|---------|
| Java JDK | 17+ | Runtime environment |
| Maven | 3.6+ | Build tool |
| Git | Latest | Version control |
| MySQL | 8.0+ | Production database (optional) |
| Docker | Latest | Containerization (optional) |

### Optional Tools

- **IDE**: IntelliJ IDEA, Eclipse, or VS Code
- **Database Client**: MySQL Workbench, DBeaver, or DataGrip
- **API Testing**: Postman or curl
- **Docker Desktop**: For containerized development

## Local Development Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd newsportal-modern
```

### 2. Install Java 17

**Windows:**
```powershell
# Using Chocolatey
choco install openjdk17

# Verify installation
java -version
```

**macOS:**
```bash
# Using Homebrew
brew install openjdk@17

# Verify installation
java -version
```

**Linux:**
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-17-jdk

# Verify installation
java -version
```

### 3. Install Maven

**Windows:**
```powershell
# Using Chocolatey
choco install maven

# Verify installation
mvn -version
```

**macOS:**
```bash
# Using Homebrew
brew install maven

# Verify installation
mvn -version
```

**Linux:**
```bash
# Ubuntu/Debian
sudo apt install maven

# Verify installation
mvn -version
```

## Database Setup

### Option 1: H2 (Embedded - Recommended for Development)

No setup required! H2 runs in-memory.

**Configuration:**
```properties
# src/main/resources/application.properties
spring.datasource.url=jdbc:h2:mem:newsportal
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true
```

**Access H2 Console:**
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:newsportal`
- Username: `sa`
- Password: (leave empty)

### Option 2: MySQL (Production-like)

#### Install MySQL

**Windows:**
```powershell
choco install mysql
```

**macOS:**
```bash
brew install mysql
brew services start mysql
```

**Linux:**
```bash
sudo apt install mysql-server
sudo systemctl start mysql
```

#### Create Database

```sql
-- Login to MySQL
mysql -u root -p

-- Create database
CREATE DATABASE newsportalmodern CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create user
CREATE USER 'newsportal'@'localhost' IDENTIFIED BY 'newsportal123';

-- Grant permissions
GRANT ALL PRIVILEGES ON newsportalmodern.* TO 'newsportal'@'localhost';
FLUSH PRIVILEGES;

-- Exit
EXIT;
```

#### Configure Application

Create `.env` file:
```bash
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/newsportalmodern?createDatabaseIfNotExist=true&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME=newsportal
SPRING_DATASOURCE_PASSWORD=newsportal123
```

### Option 3: Docker MySQL

```bash
# Start MySQL container
docker run --name newsportal-mysql \
  -e MYSQL_ROOT_PASSWORD=root_password \
  -e MYSQL_DATABASE=newsportalmodern \
  -e MYSQL_USER=newsportal \
  -e MYSQL_PASSWORD=newsportal123 \
  -p 3306:3306 \
  -d mysql:8.0

# Verify it's running
docker ps
```

## IDE Configuration

### IntelliJ IDEA

1. **Import Project:**
   - File → Open → Select `pom.xml`
   - Wait for Maven to download dependencies

2. **Enable Lombok:**
   - Settings → Plugins → Install "Lombok"
   - Settings → Build → Compiler → Annotation Processors → Enable

3. **Configure Run Configuration:**
   - Run → Edit Configurations → Add New → Spring Boot
   - Main class: `net.filippov.newsportal.NewsportalModernApplication`
   - Environment variables: Load from `.env`

### VS Code

1. **Install Extensions:**
   - Extension Pack for Java
   - Spring Boot Extension Pack
   - Lombok Annotations Support

2. **Open Project:**
   ```bash
   code .
   ```

3. **Configure `launch.json`:**
   ```json
   {
     "type": "java",
     "name": "Spring Boot - NewsportalModernApplication",
     "request": "launch",
     "mainClass": "net.filippov.newsportal.NewsportalModernApplication",
     "envFile": "${workspaceFolder}/.env"
   }
   ```

### Eclipse

1. **Import Project:**
   - File → Import → Maven → Existing Maven Projects
   - Browse to project folder

2. **Install Lombok:**
   - Download lombok.jar
   - Run: `java -jar lombok.jar`
   - Select Eclipse installation

## Running the Application

### Method 1: Maven (Command Line)

```bash
# Build the application
mvn clean package -DskipTests

# Run with H2
mvn spring-boot:run

# Run with MySQL
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.datasource.url=jdbc:mysql://localhost:3306/newsportalmodern"
```

### Method 2: IDE (IntelliJ/Eclipse/VS Code)

- Click the "Run" button
- Or right-click `NewsportalModernApplication.java` → Run

### Method 3: JAR File

```bash
# Build JAR
mvn clean package -DskipTests

# Run JAR
java -jar target/newsportal-modern-0.0.1-SNAPSHOT.jar
```

### Method 4: Docker Compose

```bash
# Start full stack
docker-compose up -d

# View logs
docker-compose logs -f

# Stop
docker-compose down
```

### Verify Application is Running

1. **Open Browser:**
   - http://localhost:8080

2. **Check Health:**
   ```bash
   curl http://localhost:8080/actuator/health
   ```

3. **Login:**
   - Username: `admin`
   - Password: `password`

## Development Workflow

### 1. Create a New Feature Branch

```bash
git checkout -b feature/your-feature-name
```

### 2. Make Changes

- Edit code in `src/main/java`
- Update templates in `src/main/resources/templates`
- Modify CSS in `src/main/resources/static/css`

### 3. Hot Reload (Development Mode)

**Enable DevTools:**
Already included in `pom.xml`:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
```

**In IntelliJ:**
- Settings → Build → Compiler → Build project automatically
- Settings → Advanced → Allow auto-make to start even if developed application is currently running

**Changes are hot-reloaded for:**
- ✅ Thymeleaf templates
- ✅ Static resources (CSS, JS)
- ⚠️ Java code (requires rebuild)

### 4. Run Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=ArticleServiceTest

# Run tests with coverage
mvn clean test jacoco:report
```

### 5. Build for Production

```bash
# Create production JAR
mvn clean package -DskipTests

# JAR location
ls target/newsportal-modern-0.0.1-SNAPSHOT.jar
```

## Sample Data

The application includes sample data in `src/main/resources/data.sql`:

- **3 Users:** admin, author, user
- **4 Categories:** Technology, Sports, Politics, Business
- **3 Articles:** With images and tags
- **3 Comments:** Sample comments

To reset data:
1. Stop application
2. Delete database (H2 auto-resets, MySQL: `DROP DATABASE newsportalmodern; CREATE DATABASE newsportalmodern;`)
3. Restart application

## Environment Variables

Create `.env` file in project root:

```bash
# Database
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/newsportalmodern
SPRING_DATASOURCE_USERNAME=newsportal
SPRING_DATASOURCE_PASSWORD=newsportal123

# Server
SERVER_PORT=8080

# JPA
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=true

# File Upload
SPRING_SERVLET_MULTIPART_MAX_FILE_SIZE=5MB
SPRING_SERVLET_MULTIPART_MAX_REQUEST_SIZE=5MB

# Actuator
MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health

# H2 Console (if using H2)
SPRING_H2_CONSOLE_ENABLED=true
```

## Troubleshooting

### Port 8080 Already in Use

```bash
# Find process using port 8080
netstat -ano | findstr :8080  # Windows
lsof -i :8080                 # Mac/Linux

# Kill the process or change port
export SERVER_PORT=8081
mvn spring-boot:run
```

### Maven Build Fails

```bash
# Clear Maven cache
mvn clean install -U

# Skip tests if failing
mvn clean package -DskipTests
```

### Database Connection Issues

```bash
# Test MySQL connection
mysql -u newsportal -p newsportalmodern

# Check MySQL is running
systemctl status mysql  # Linux
brew services list       # macOS
```

### Lombok Not Working

```bash
# Rebuild project
mvn clean install

# Verify Lombok is enabled in IDE
# IntelliJ: Settings → Plugins → Lombok installed
# Eclipse: Run lombok.jar installer
```

## Next Steps

- [📚 API Documentation](API.md)
- [💾 Database Schema](DATABASE.md)
- [🚀 Deployment Guide](DEPLOYMENT.md)
- [🐛 Troubleshooting](TROUBLESHOOTING.md)

---

Need help? Check the [Troubleshooting Guide](TROUBLESHOOTING.md) or open an issue.
