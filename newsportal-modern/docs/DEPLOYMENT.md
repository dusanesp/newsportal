# Deployment Guide

Production deployment guide for newsportal-modern application.

## Deployment Options

1. **Docker Compose** (Recommended)
2. **Docker Hub** (Pull pre-built image)
3. **Traditional JAR Deployment**
4. **Cloud Platforms** (AWS, Azure, Google Cloud)

---

## Option 1: Docker Compose Deployment

### Step 1: Clone Repository

```bash
git clone <repository-url>
cd newsportal-modern
```

### Step 2: Configure Environment

Create `.env` file:

```bash
# Database
MYSQL_ROOT_PASSWORD=<strong-root-password>
MYSQL_DATABASE=newsportalmodern
MYSQL_USER=newsportal
MYSQL_PASSWORD=<strong-password>

# Application
SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/newsportalmodern?useSSL=true&requireSSL=true
SPRING_DATASOURCE_USERNAME=newsportal
SPRING_DATASOURCE_PASSWORD=<strong-password>
SPRING_JPA_HIBERNATE_DDL_AUTO=validate
SPRING_JPA_SHOW_SQL=false
```

### Step 3: Start Services

```bash
docker-compose up -d
```

### Step 4: Verify Deployment

```bash
# Check containers
docker-compose ps

# Check logs
docker-compose logs -f

# Test health
curl http://localhost:8080/actuator/health
```

---

## Option 2: Docker Hub Deployment

### Pull Latest Image

```bash
docker pull dusanesp/portal:latest
```

### Run with Docker Compose

```yaml
version: '3.8'
services:
  newsportal-app:
    image: dusanesp/portal:latest
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/newsportalmodern
      SPRING_DATASOURCE_USERNAME: newsportal
      SPRING_DATASOURCE_PASSWORD: <password>
    depends_on:
      - mysql
  
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: <root-password>
      MYSQL_DATABASE: newsportalmodern
      MYSQL_USER: newsportal
      MYSQL_PASSWORD: <password>
    volumes:
      - mysql_data:/var/lib/mysql

volumes:
  mysql_data:
```

---

## Option 3: JAR Deployment

### Build Application

```bash
mvn clean package -DskipTests
```

### Setup Database

```sql
CREATE DATABASE newsportalmodern;
```

### Run Application

```bash
java -jar target/newsportal-modern-0.0.1-SNAPSHOT.jar \
  --spring.datasource.url=jdbc:mysql://localhost:3306/newsportalmodern \
  --spring.datasource.username=newsportal \
  --spring.datasource.password=<password>
```

### Run as Service (Linux)

Create `/etc/systemd/system/newsportal.service`:

```ini
[Unit]
Description=Newsportal Modern
After=network.target

[Service]
Type=simple
User=newsportal
ExecStart=/usr/bin/java -jar /opt/newsportal/newsportal-modern.jar
Restart=on-failure
EnvironmentFile=/opt/newsportal/.env

[Install]
WantedBy=multi-user.target
```

Enable and start:
```bash
sudo systemctl enable newsportal
sudo systemctl start newsportal
```

---

## Production Checklist

### Security
- [ ] Change all default passwords
- [ ] Use HTTPS/TLS (reverse proxy or Spring Boot HTTPS)
- [ ] Configure firewall rules
- [ ] Enable CSRF protection
- [ ] Set secure session cookies
- [ ] Implement rate limiting

### Database
- [ ] Use strong passwords
- [ ] Enable SSL for database connections
- [ ] Configure regular backups
- [ ] Set up replication (if needed)
- [ ] Optimize indexes
- [ ] Set `ddl-auto=validate` (not `update`)

### Application
- [ ] Set `spring.jpa.show-sql=false`
- [ ] Configure logging to files
- [ ] Set up log rotation
- [ ] Configure actuator security
- [ ] Set resource limits
- [ ] Enable health checks

### Monitoring
- [ ] Set up application monitoring
- [ ] Configure alerting
- [ ] Enable access logs
- [ ] Monitor database performance
- [ ] Track error rates

---

## Nginx Reverse Proxy

### Install Nginx

```bash
sudo apt install nginx
```

### Configure

`/etc/nginx/sites-available/newsportal`:

```nginx
server {
    listen 80;
    server_name newsportal.example.com;
    
    # Redirect to HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name newsportal.example.com;
    
    ssl_certificate /etc/letsencrypt/live/newsportal.example.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/newsportal.example.com/privkey.pem;
    
    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

Enable site:
```bash
sudo ln -s /etc/nginx/sites-available/newsportal /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

---

## SSL/TLS with Let's Encrypt

```bash
# Install Certbot
sudo apt install certbot python3-certbot-nginx

# Obtain certificate
sudo certbot --nginx -d newsportal.example.com

# Auto-renewal is configured automatically
```

---

## Cloud Deployment

### AWS (Elastic Beanstalk)

1. Create `Dockerrun.aws.json`
2. Deploy via EB CLI
3. Configure RDS MySQL
4. Set environment variables

### Google Cloud (Cloud Run)

```bash
# Build and push
gcloud builds submit --tag gcr.io/PROJECT_ID/newsportal

# Deploy
gcloud run deploy newsportal \
  --image gcr.io/PROJECT_ID/newsportal \
  --platform managed \
  --allow-unauthenticated
```

### Azure (App Service)

1. Create App Service
2. Configure MySQL database
3. Deploy via Azure CLI or GitHub Actions

---

## Backup Strategy

### Database Backups

```bash
# Daily backup cron job
0 2 * * * mysqldump -u newsportal -p newsportalmodern > /backups/newsportal-$(date +\%Y\%m\%d).sql
```

### Volume Backups

```bash
# Backup Docker volumes
docker run --rm \
  -v newsportal_mysql_data:/data \
  -v /backups:/backup \
  alpine tar czf /backup/mysql-data.tar.gz /data
```

---

## Scaling

### Horizontal Scaling

Use Docker Swarm or Kubernetes for multiple app instances with load balancing.

### Database Scaling

- Read replicas for heavy read loads
- Connection pooling
- Query optimization

---

## Troubleshooting

See [TROUBLESHOOTING.md](TROUBLESHOOTING.md) for common deployment issues.
