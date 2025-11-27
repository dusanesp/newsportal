# Security Guidelines

Security best practices and guidelines for the newsportal-modern application.

## Current Security Measures

✅ **Implemented:**
- Spring Security with form-based authentication
- BCrypt password hashing (strength: 10)
- CSRF protection enabled
- SQL injection protection via JPA/Hibernate
- Input validation with Jakarta Validation
- Role-based access control (RBAC)
- Secure session management
- Spring Boot Actuator with health endpoint only

⚠️ **Recommended for Production:**
- HTTPS/TLS encryption
- Rate limiting on login
- Account lockout after failed attempts
- Security headers (CSP, HSTS, etc.)
- File upload validation
- Regular dependency updates

---

## Authentication & Authorization

### Password Security

**Requirements:**
- Minimum 7 characters (configured)
- BCrypt encryption (cost factor: 10)

**Change Default Passwords:**
```sql
-- Generate new BCrypt hash
-- Use online tool or Spring Security BCryptPasswordEncoder

UPDATE user SET password = '$2a$10$NEW_HASH_HERE' WHERE login = 'admin';
```

### Session Management

- Sessions stored server-side
- Session timeout: Default 30 minutes
- Secure cookie flag recommended for HTTPS

### Role-Based Access

| Role | Permissions |
|------|-------------|
| ROLE_USER | View articles, post comments |
| ROLE_AUTHOR | Create/edit own articles |
| ROLE_ADMIN | Full access, user management |

---

## Input Validation

### Server-Side Validation

Always validate on server:

```java
@NotBlank(message = "Title is required")
@Size(min = 5, max = 100)
private String title;

@Email(message = "Invalid email")
private String email;
```

### HTML Sanitization

For user-generated HTML content:

```java
// Sanitize HTML to prevent XSS
String sanitized = Jsoup.clean(userInput, Whitelist.relaxed());
```

---

## CSRF Protection

**Status:** ✅ Enabled by default

All POST/PUT/DELETE requests require CSRF token:

```html
<form method="POST">
    <input type="hidden" name="_csrf" value="${_csrf.token}"/>
</form>
```

Thymeleaf auto-includes CSRF tokens.

---

## SQL Injection Prevention

**Status:** ✅ Protected

Using JPA with parameterized queries:

```java
// SAFE
@Query("SELECT a FROM Article a WHERE a.title = :title")
Article findByTitle(@Param("title") String title);

// UNSAFE - Never do this
entityManager.createQuery("SELECT a FROM Article a WHERE title = '" + userInput + "'");
```

---

## File Upload Security

### Current Limits

- Max file size: 5MB
- Max request size: 5MB

### Recommendations

1. **Validate file types:**
```java
private static final List<String> ALLOWED_TYPES = Arrays.asList(
    "image/jpeg", "image/png", "image/gif"
);

if (!ALLOWED_TYPES.contains(file.getContentType())) {
    throw new InvalidFileTypeException();
}
```

2. **Scan for malware** (use ClamAV or similar)

3. **Store outside web root**

4. **Generate unique filenames** (prevent overwrite attacks)

---

## HTTPS Configuration

### Spring Boot HTTPS

**application.properties:**
```properties
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=<password>
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=newsportal
```

### Generate Keystore

```bash
keytool -genkeypair \
  -alias newsportal \
  -keyalg RSA \
  -keysize 2048 \
  -storetype PKCS12 \
  -keystore keystore.p12 \
  -validity 3650
```

---

## Security Headers

Add to configuration:

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .headers(headers -> headers
            .contentSecurityPolicy("default-src 'self'")
            .frameOptions().deny()
            .httpStrictTransportSecurity()
                .maxAgeInSeconds(31536000)
                .includeSubDomains(true)
        );
    return http.build();
}
```

**Headers to implement:**
- `Content-Security-Policy`
- `X-Frame-Options: DENY`
- `X-Content-Type-Options: nosniff`
- `Strict-Transport-Security`
- `X-XSS-Protection: 1; mode=block`

---

## Rate Limiting

### Implement with Bucket4j

**Add dependency:**
```xml
<dependency>
    <groupId>com.github.vladimir-bukhtoyarov</groupId>
    <artifactId>bucket4j-core</artifactId>
    <version>8.1.0</version>
</dependency>
```

**Example:**
```java
Bucket bucket = Bucket.builder()
    .addLimit(Bandwidth.simple(10, Duration.ofMinutes(1)))
    .build();

if (bucket.tryConsume(1)) {
    // Process request
} else {
    throw new RateLimitExceededException();
}
```

---

## Dependency Security

### Check for Vulnerabilities

```bash
# Maven dependency check
mvn org.owasp:dependency-check-maven:check

# Docker Scout
docker scout cves newsportal-modern:latest
```

### Keep Dependencies Updated

- Regularly update Spring Boot version
- Monitor security advisories
- Use Dependabot or Snyk

---

## Logging & Monitoring

### Security Logging

Log security events:
- Failed login attempts
- Access denied errors
- Suspicious activity

```java
log.warn("Failed login attempt for user: {}", username);
log.error("Unauthorized access attempt to: {}", path);
```

### Sensitive Data

**Never log:**
- Passwords
- Session tokens
- API keys
- Personal information

---

## Environment Variables

### Secure Storage

- Use environment variables for secrets
- Never commit `.env` to version control
- Use secret management (AWS Secrets Manager, HashiCorp Vault)

**Add to `.gitignore`:**
```
.env
*.env
!.env.example
```

---

## Database Security

### Connection Security

```properties
# Enable SSL
spring.datasource.url=jdbc:mysql://localhost:3306/newsportalmodern?useSSL=true&requireSSL=true
```

### Principle of Least Privilege

Grant only required permissions:

```sql
-- Don't use root
REVOKE ALL ON *.* FROM 'newsportal'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON newsportalmodern.* TO 'newsportal'@'localhost';
```

---

## Security Checklist

### Production Deployment

- [ ] All default passwords changed
- [ ] HTTPS enabled
- [ ] Security headers configured
- [ ] Rate limiting implemented
- [ ] Account lockout enabled
- [ ] File upload validation
- [ ] Dependencies updated
- [ ] Security logging enabled
- [ ] Secrets in environment variables
- [ ] Database SSL enabled
- [ ] Backup strategy implemented
- [ ] Monitoring and alerting configured

### Regular Maintenance

- [ ] Weekly dependency checks
- [ ] Monthly security audits
- [ ] Quarterly penetration testing
- [ ] Annual security review

---

## Security Incident Response

### If Compromised

1. **Isolate** - Take system offline
2. **Assess** - Determine scope of breach
3. **Contain** - Prevent further damage
4. **Eradicate** - Remove threat
5. **Recover** - Restore from clean backup
6. **Notify** - Inform affected users

### Contacts

- Security Team: security@example.com
- Emergency: +1-XXX-XXX-XXXX

---

## Additional Resources

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [CWE Top 25](https://cwe.mitre.org/top25/)

---

For questions, see [Security Audit Report](../security-audit.md) or contact the security team.
