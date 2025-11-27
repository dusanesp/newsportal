# Troubleshooting Guide

Common issues and solutions for newsportal-modern.

## Table of Contents

- [Application Won't Start](#application-wont-start)
- [Database Issues](#database-issues)
- [Docker Issues](#docker-issues)
- [Login Problems](#login-problems)
- [File Upload Errors](#file-upload-errors)
- [Performance Issues](#performance-issues)

---

## Application Won't Start

### Port Already in Use

**Error:**
```
Port 8080 is already in use
```

**Solutions:**

1. Find and kill process:
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac
lsof -i :8080
kill -9 <PID>
```

2. Or change port:
```properties
server.port=8081
```

---

### Java Version Mismatch

**Error:**
```
Unsupported class file major version
```

**Solution:** Install Java 17+
```bash
java -version  # Should show 17 or higher
```

---

### Maven Build Fails

**Error:**
```
Failed to execute goal org.apache.maven.plugins
```

**Solutions:**

1. Clear Maven cache:
```bash
mvn clean install -U
```

2. Delete `.m2/repository` and rebuild

3. Check internet connection (Maven downloads dependencies)

---

## Database Issues

### Connection Refused

**Error:**
```
Communications link failure
```

**Solutions:**

1. Check MySQL is running:
```bash
# Linux
sudo systemctl status mysql

# Mac
brew services list

# Windows
services.msc (look for MySQL)
```

2. Verify connection details:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/newsportalmodern
spring.datasource.username=newsportal
spring.datasource.password=newsportal123
```

3. Test connection:
```bash
mysql -u newsportal -p newsportalmodern
```

---

### Access Denied

**Error:**
```
Access denied for user 'newsportal'@'localhost'
```

**Solution:** Grant permissions:
```sql
mysql -u root -p
GRANT ALL PRIVILEGES ON newsportalmodern.* TO 'newsportal'@'localhost';
FLUSH PRIVILEGES;
```

---

### Database Doesn't Exist

**Error:**
```
Unknown database 'newsportalmodern'
```

**Solution:** Create database:
```sql
CREATE DATABASE newsportalmodern CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

---

### Schema Issues

**Error:**
```
Table 'user' doesn't exist
```

**Solutions:**

1. Check `ddl-auto` setting:
```properties
spring.jpa.hibernate.ddl-auto=update
```

2. Manually create tables (see [DATABASE.md](DATABASE.md))

3. Drop and recreate database:
```sql
DROP DATABASE newsportalmodern;
CREATE DATABASE newsportalmodern;
```

---

## Docker Issues

### Container Won't Start

**Error:**
```
Container newsportal-app exited with code 1
```

**Solutions:**

1. Check logs:
```bash
docker-compose logs newsportal-app
```

2. Verify environment variables in `.env`

3. Ensure MySQL is healthy:
```bash
docker-compose ps
```

---

### Image Build Fails

**Error:**
```
ERROR [build 6/6] RUN mvn clean package
```

**Solutions:**

1. Check internet connection

2. Increase Docker memory (Docker Desktop → Settings → Resources)

3. Build without cache:
```bash
docker-compose build --no-cache
```

---

### Port Conflicts

**Error:**
```
Port 8080 is already allocated
```

**Solutions:**

1. Stop conflicting container:
```bash
docker ps
docker stop <container-id>
```

2. Change port in `docker-compose.yml`:
```yaml
ports:
  - "8081:8080"
```

---

### MySQL Won't Start

**Error:**
```
Container newsportal-mysql unhealthy
```

**Solutions:**

1. Check logs:
```bash
docker-compose logs mysql
```

2. Remove volume and recreate:
```bash
docker-compose down -v
docker-compose up -d
```

---

## Login Problems

### Can't Log In with Default Credentials

**Solutions:**

1. Verify `data.sql` was loaded:
```sql
SELECT * FROM user WHERE login = 'admin';
```

2. Check password hash matches:
```java
// Password should be: password
// Hash: $2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG
```

3. Reset password:
```sql
UPDATE user 
SET password = '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG' 
WHERE login = 'admin';
```

---

### Session Expires Too Quickly

**Solution:** Increase timeout:
```properties
server.servlet.session.timeout=30m
```

---

### CSRF Token Errors

**Error:**
```
Invalid CSRF token
```

**Solutions:**

1. Ensure CSRF token in forms:
```html
<input type="hidden" name="_csrf" value="${_csrf.token}"/>
```

2. For AJAX, include header:
```javascript
headers: {
    'X-CSRF-TOKEN': document.querySelector('[name="_csrf"]').value
}
```

---

## File Upload Errors

### File Too Large

**Error:**
```
Maximum upload size exceeded
```

**Solution:** Increase limits:
```properties
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

---

### Upload Directory Not Found

**Error:**
```
FileNotFoundException: /app/uploads
```

**Solution:** Create directory:
```bash
mkdir -p /app/uploads/images/articles
```

Or in Docker, volume mount:
```yaml
volumes:
  - ./uploads:/app/uploads
```

---

## Performance Issues

### Slow Queries

**Solutions:**

1. Enable query logging:
```properties
spring.jpa.show-sql=true
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

2. Add indexes (see [DATABASE.md](DATABASE.md))

3. Use pagination:
```java
Pageable pageable = PageRequest.of(page, size);
```

---

### High Memory Usage

**Solutions:**

1. Set JVM memory limits:
```bash
java -Xmx512m -jar app.jar
```

2. For Docker:
```yaml
deploy:
  resources:
    limits:
      memory: 1G
```

3. Profile with:
```bash
jvisualvm
```

---

### Slow Startup

**Solutions:**

1. Use lazy initialization:
```properties
spring.main.lazy-initialization=true
```

2. Disable DevTools in production

3. Use native compilation (GraalVM)

---

## Common Error Messages

### "Failed to configure a DataSource"

**Cause:** Database configuration missing

**Fix:** Add to `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/newsportalmodern
spring.datasource.username=newsportal
spring.datasource.password=newsportal123
```

---

### "Whitelabel Error Page"

**Cause:** Request mapping not found

**Fix:**

1. Check URL is correct
2. Verify controller mapping exists
3. Check security configuration allows access

---

### "LazyInitializationException"

**Cause:** Accessing lazy-loaded data outside transaction

**Fix:**

1. Use `@Transactional`
2. Or fetch eagerly: `@ManyToOne(fetch = FetchType.EAGER)`
3. Or use JOIN FETCH in query

---

## Debugging Tips

### Enable Debug Logging

```properties
logging.level.root=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate=DEBUG
```

### Check Actuator Health

```bash
curl http://localhost:8080/actuator/health
```

### Inspect Database

```bash
# H2 Console
http://localhost:8080/h2-console

# MySQL
mysql -u newsportal -p newsportalmodern
```

### Docker Exec

```bash
# Access running container
docker exec -it newsportal-app sh

# Check logs
docker logs newsportal-app
```

---

## Getting Help

Still stuck? Try these resources:

1. **Check logs** - Most issues are logged
2. **Search GitHub Issues** - Someone may have had the same problem
3. **Stack Overflow** - Tag: spring-boot, thymeleaf
4. **Documentation** - [README.md](../README.md), [SETUP.md](SETUP.md)

### Report a Bug

Include:
- Error message
- Stack trace
- Steps to reproduce
- Environment (OS, Java version, etc.)

---

## FAQ

**Q: How do I reset the database?**  
A: For H2, just restart. For MySQL, `DROP` and `CREATE` database.

**Q: Can I use PostgreSQL instead of MySQL?**  
A: Yes, change driver and dialect in `pom.xml` and `application.properties`.

**Q: How do I change the admin password?**  
A: Update the `user` table with a new BCrypt hash.

**Q Port 8080 in use?**  
A: Change `server.port` in `application.properties`.

---

Need more help? Contact: support@example.com
