# Docker Setup for Newsportal Application

## Quick Start

### Build and Run with Docker Compose
```bash
# Build and start all services
docker-compose up --build

# Run in detached mode
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

### Manual Docker Commands

#### Build the Docker Image
```bash
# Build the image
docker build -t newsportal:latest .

# Build with custom tag
docker build -t newsportal:1.0.0 .
```

#### Run the Container
```bash
# Run with Docker Compose (recommended)
docker-compose up

# Run standalone (requires MySQL to be running separately)
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/newsportalmodern \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=Sanjay@123 \
  newsportal:latest
```

## Architecture

### Services

1. **newsportal-app** - Spring Boot application
   - Port: 8080
   - Depends on MySQL
   - Includes health checks

2. **mysql** - MySQL 8.0 database
   - Port: 3306
   - Auto-creates database on first run
   - Persists data in Docker volume

### Volumes

- `mysql_data` - Persists MySQL database files
- `app_uploads` - Persists uploaded article images

### Network

- `newsportal-network` - Bridge network for service communication

## Environment Variables

### Database Configuration
- `SPRING_DATASOURCE_URL` - MySQL connection string
- `SPRING_DATASOURCE_USERNAME` - Database username
- `SPRING_DATASOURCE_PASSWORD` - Database password

### Application Configuration
- `SPRING_JPA_HIBERNATE_DDL_AUTO` - Schema generation strategy (update/create/none)
- `SPRING_JPA_SHOW_SQL` - Show SQL queries in logs
- `SPRING_SERVLET_MULTIPART_MAX_FILE_SIZE` - Max file upload size
- `SPRING_SERVLET_MULTIPART_MAX_REQUEST_SIZE` - Max request size

## Accessing the Application

Once running:
- **Application**: http://localhost:8080
- **MySQL**: localhost:3306

## Useful Commands

```bash
# View running containers
docker-compose ps

# View logs for specific service
docker-compose logs -f newsportal-app
docker-compose logs -f mysql

# Restart a service
docker-compose restart newsportal-app

# Stop and remove containers, networks, volumes
docker-compose down -v

# Execute commands in running container
docker-compose exec newsportal-app sh
docker-compose exec mysql mysql -u newsportal -p

# Rebuild without cache
docker-compose build --no-cache

# Scale application (if needed)
docker-compose up --scale newsportal-app=2
```

## Database Migration

The database schema will be auto-created by Hibernate on first run (`ddl-auto=update`).

To run manual migrations:
```bash
# Copy SQL file to running container
docker cp src/main/resources/db/migration/add_image_url_to_article.sql newsportal-mysql:/tmp/

# Execute migration
docker-compose exec mysql mysql -u newsportal -pnewsportal123 newsportalmodern < /tmp/add_image_url_to_article.sql
```

## Production Considerations

For production deployment:

1. **Use environment-specific configs**
   ```bash
   docker-compose -f docker-compose.yml -f docker-compose.prod.yml up
   ```

2. **Use secrets for sensitive data**
   - Don't hardcode passwords
   - Use Docker secrets or external secret management

3. **Configure proper logging**
   - Set up log aggregation
   - Configure log rotation

4. **Use health checks**
   - Already configured in docker-compose.yml
   - Monitor with orchestration tools

5. **Set resource limits**
   ```yaml
   deploy:
     resources:
       limits:
         cpus: '1.0'
         memory: 1G
   ```

## Troubleshooting

### Container won't start
```bash
# Check logs
docker-compose logs newsportal-app

# Check if MySQL is ready
docker-compose logs mysql
```

### Database connection issues
```bash
# Verify MySQL is healthy
docker-compose ps

# Test connection
docker-compose exec mysql mysql -u newsportal -pnewsportal123 -e "SHOW DATABASES;"
```

### Image upload issues
```bash
# Check volume mount
docker volume inspect newsportal-modern_app_uploads

# Access container filesystem
docker-compose exec newsportal-app ls -la /app/uploads
```

## Clean Up

```bash
# Stop and remove everything
docker-compose down -v

# Remove images
docker rmi newsportal:latest

# Remove unused volumes
docker volume prune
```
