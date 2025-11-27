# Newsportal Modern

A modern, full-featured news portal application built with Spring Boot 3.3.5, Thymeleaf, and Tailwind CSS.

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen)
![Java](https://img.shields.io/badge/Java-17-orange)
![Docker](https://img.shields.io/badge/Docker-Ready-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

## Features

- 📰 **Article Management** - Create, edit, and publish news articles with rich HTML content
- 🎨 **Modern UI** - Responsive design with Tailwind CSS
- 👥 **User Management** - Role-based access control (USER, AUTHOR, ADMIN)
- 🏷️ **Categories & Tags** - Organize articles with categories and tags
- 💬 **Comments** - Community engagement with article comments
- 🖼️ **Image Upload** - Article images with placeholder support
- 🔒 **Security** - Spring Security with BCrypt password encryption
- 🐳 **Docker Ready** - Containerized deployment with Docker Compose
- 📊 **Actuator** - Health checks and monitoring endpoints

## Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+ OR H2 (embedded)
- Docker & Docker Compose (for containerized deployment)

### Run Locally

```bash
# Clone the repository
git clone <repository-url>
cd newsportal-modern

# Run with H2 (in-memory database)
mvn spring-boot:run

# OR with MySQL
mvn spring-boot:run -Dspring.datasource.url=jdbc:mysql://localhost:3306/newsportalmodern

# Access the application
open http://localhost:8080
```

### Run with Docker

```bash
# Start full stack (app + MySQL)
docker-compose up -d

# Access the application
open http://localhost:8080

# Stop services
docker-compose down
```

### Pull from Docker Hub

```bash
docker pull dusanesp/portal:latest
```

## Default Credentials

| Username | Password  | Roles                    |
|----------|-----------|--------------------------|
| admin    | password  | USER, ADMIN, AUTHOR      |
| author   | password  | USER, AUTHOR             |
| user     | password  | USER                     |

> ⚠️ **Change these passwords in production!**

## Project Structure

```
newsportal-modern/
├── src/
│   ├── main/
│   │   ├── java/net/filippov/newsportal/
│   │   │   ├── domain/          # Entity models
│   │   │   ├── repository/      # Data access layer
│   │   │   ├── service/         # Business logic
│   │   │   ├── web/             # Controllers & DTOs
│   │   │   └── config/          # Spring configuration
│   │   └── resources/
│   │       ├── templates/       # Thymeleaf templates
│   │       ├── static/          # CSS, JS, images
│   │       ├── data.sql         # Sample data
│   │       └── application.properties
│   └── test/                    # Unit & integration tests
├── docs/                        # Documentation
├── Dockerfile                   # Docker image definition
├── docker-compose.yml          # Docker Compose stack
├── pom.xml                     # Maven dependencies
└── README.md
```

## Technology Stack

### Backend
- **Spring Boot 3.3.5** - Application framework
- **Spring Security** - Authentication & authorization
- **Spring Data JPA** - Data persistence
- **Hibernate** - ORM
- **MySQL / H2** - Database
- **Lombok** - Boilerplate reduction

### Frontend
- **Thymeleaf** - Server-side templating
- **Tailwind CSS** - Utility-first CSS
- **Thymeleaf Security** - Template-level security

### DevOps
- **Docker** - Containerization
- **Docker Compose** - Multi-container orchestration
- **Spring Boot Actuator** - Health checks & monitoring
- **Maven** - Build tool

## Documentation

- [📚 Setup Guide](docs/SETUP.md) - Development environment setup
- [🔌 API Documentation](docs/API.md) - REST endpoints & usage
- [💾 Database Schema](docs/DATABASE.md) - Data model & migrations
- [🚀 Deployment Guide](docs/DEPLOYMENT.md) - Production deployment
- [🔐 Security Guidelines](docs/SECURITY.md) - Security best practices
- [🐛 Troubleshooting](docs/TROUBLESHOOTING.md) - Common issues & solutions
- [🤝 Contributing](docs/CONTRIBUTING.md) - How to contribute

## Configuration

### Environment Variables

```bash
# Database
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/newsportalmodern
SPRING_DATASOURCE_USERNAME=newsportal
SPRING_DATASOURCE_PASSWORD=<your-password>

# Application
SERVER_PORT=8080
SPRING_JPA_HIBERNATE_DDL_AUTO=update

# File Upload
SPRING_SERVLET_MULTIPART_MAX_FILE_SIZE=5MB
SPRING_SERVLET_MULTIPART_MAX_REQUEST_SIZE=5MB
```

See [.env.example](.env.example) for complete configuration.

## Key Endpoints

| Endpoint | Description | Auth Required |
|----------|-------------|---------------|
| `/` | Homepage with latest articles | No |
| `/login` | User login | No |
| `/register` | User registration | No |
| `/articles` | Article list | No |
| `/articles/{id}` | Article details | No |
| `/admin` | Admin dashboard | ADMIN |
| `/author/articles/new` | Create article | AUTHOR |
| `/actuator/health` | Health check | No |

## Docker Hub

The latest image is available on Docker Hub:

```bash
docker pull dusanesp/portal:latest
```

**Image Details:**
- Base: `eclipse-temurin:17-jre-alpine`
- Size: ~385MB (130MB compressed)
- Includes: Sample data, placeholder images

## Development

```bash
# Install dependencies
mvn clean install

# Run tests
mvn test

# Run with dev profile (H2 database)
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Build for production
mvn clean package -DskipTests
```

## Security

- ✅ Spring Security with BCrypt password encryption
- ✅ CSRF protection enabled
- ✅ SQL injection protection via JPA
- ✅ Input validation with Jakarta Validation
- ✅ Secure headers configuration
- ⚠️ Use HTTPS in production

See [Security Guidelines](docs/SECURITY.md) for details.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

- 📧 Email: support@example.com
- 🐛 Issues: [GitHub Issues](https://github.com/username/newsportal-modern/issues)
- 📖 Wiki: [Project Wiki](https://github.com/username/newsportal-modern/wiki)

## Acknowledgments

- Original project by Oleg Filippov
- Modernized version with Spring Boot 3 & Thymeleaf
- Community contributors

---

Made with ❤️ by the Newsportal Team
