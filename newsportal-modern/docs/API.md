# API Documentation

Complete reference for all endpoints and APIs in the newsportal-modern application.

## Table of Contents

- [Authentication](#authentication)
- [Public Endpoints](#public-endpoints)
- [User Endpoints](#user-endpoints)
- [Author Endpoints](#author-endpoints)
- [Admin Endpoints](#admin-endpoints)
- [API Responses](#api-responses)

## Base URL

```
http://localhost:8080
```

## Authentication

The application uses **Spring Security** with session-based authentication.

### Login

**Endpoint:** `POST /login`

**Request:**
```http
POST /login HTTP/1.1
Content-Type: application/x-www-form-urlencoded

username=admin&password=password
```

**Response:** Redirects to dashboard or homepage

### Logout

**Endpoint:** `POST /logout`

**Request:**
```http
POST /logout HTTP/1.1
```

**Response:** Red to login page

---

## Public Endpoints

### 1. Homepage

**GET /**

Returns the homepage with latest articles.

```http
GET / HTTP/1.1
```

**Response:** HTML page

---

### 2. Article List

**GET /articles**

Get paginated list of published articles.

**Query Parameters:**
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| page      | int  | 0       | Page number |
| size      | int  | 10      | Items per page |
| category  | String | null  | Filter by category |
| tag       | String | null  | Filter by tag |

**Example:**
```http
GET /articles?page=0&size=20&category=Technology HTTP/1.1
```

---

### 3. Article Details

**GET /articles/{id}**

Get full article details including comments.

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| id        | Long | Article ID  |

**Example:**
```http
GET /articles/1 HTTP/1.1
```

**Response:** HTML page with article content

---

### 4. Search Articles

**GET /search**

Search articles by keyword.

**Query Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| q         | String | Yes    | Search query |
| page      | int  | No       | Page number |

**Example:**
```http
GET /search?q=spring+boot HTTP/1.1
```

---

### 5. Category Articles

**GET /category/{name}**

Get articles in a specific category.

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| name      | String | Category name |

**Example:**
```http
GET /category/Technology HTTP/1.1
```

---

### 6. Tag Articles

**GET /tag/{name}**

Get articles with a specific tag.

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| name      | String | Tag name |

**Example:**
```http
GET /tag/Java HTTP/1.1
```

---

## User Endpoints

**Required Role:** `ROLE_USER`

### 1. User Profile

**GET /profile**

View user profile.

```http
GET /profile HTTP/1.1
Cookie: JSESSIONID=<session-id>
```

---

### 2. Update Profile

**POST /profile/update**

Update user profile information.

**Form Data:**
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| name  | String | Yes    | Full name |
| email | String | Yes    | Email address |

**Example:**
```http
POST /profile/update HTTP/1.1
Content-Type: application/x-www-form-urlencoded

name=John+Doe&email=john@example.com
```

---

### 3. Add Comment

**POST /articles/{id}/comments**

Add a comment to an article.

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| id        | Long | Article ID  |

**Form Data:**
| Field   | Type | Required | Description |
|---------|------|----------|-------------|
| content | String | Yes    | Comment text |

**Example:**
```http
POST /articles/1/comments HTTP/1.1
Content-Type: application/x-www-form-urlencoded

content=Great+article!
```

---

## Author Endpoints

**Required Role:** `ROLE_AUTHOR`

### 1. Author Dashboard

**GET /author/dashboard**

View author's articles and statistics.

```http
GET /author/dashboard HTTP/1.1
Cookie: JSESSIONID=<session-id>
```

---

### 2. Create Article

**GET /author/articles/new**

Show create article form.

```http
GET /author/articles/new HTTP/1.1
```

**POST /author/articles**

Submit new article.

**Form Data:**
| Field       | Type   | Required | Description |
|-------------|--------|----------|-------------|
| title       | String | Yes      | Article title |
| preview     | String | Yes      | Short preview |
| content     | String | Yes      | Full content (HTML) |
| categoryId  | Long   | Yes      | Category ID |
| tags        | String | No       | Comma-separated tags |
| image       | File   | No       | Cover image |

**Example:**
```http
POST /author/articles HTTP/1.1
Content-Type: multipart/form-data; boundary=----WebKitFormBoundary

------WebKitFormBoundary
Content-Disposition: form-data; name="title"

Getting Started with Spring Boot
------WebKitFormBoundary
Content-Disposition: form-data; name="preview"

Learn Spring Boot basics
------WebKitFormBoundary
Content-Disposition: form-data; name="content"

<p>Spring Boot is amazing...</p>
------WebKitFormBoundary
Content-Disposition: form-data; name="categoryId"

1
------WebKitFormBoundary
Content-Disposition: form-data; name="tags"

Java,Spring
------WebKitFormBoundary
Content-Disposition: form-data; name="image"; filename="cover.jpg"
Content-Type: image/jpeg

<binary data>
------WebKitFormBoundary--
```

---

### 3. Edit Article

**GET /author/articles/{id}/edit**

Show edit article form.

**POST /author/articles/{id}**

Update existing article.

**Form Data:** Same as create article

---

### 4. Delete Article

**POST /author/articles/{id}/delete**

Delete an article.

```http
POST /author/articles/1/delete HTTP/1.1
```

---

### 5. My Articles

**GET /author/articles**

List all articles by the current author.

```http
GET /author/articles?page=0&size=10 HTTP/1.1
```

---

## Admin Endpoints

**Required Role:** `ROLE_ADMIN`

### 1. Admin Dashboard

**GET /admin**

View admin dashboard with statistics.

```http
GET /admin HTTP/1.1
Cookie: JSESSIONID=<session-id>
```

---

### 2. Manage Users

**GET /admin/users**

List all users.

**POST /admin/users/{id}/enable**

Enable a user account.

**POST /admin/users/{id}/disable**

Disable a user account.

**POST /admin/users/{id}/delete**

Delete a user account.

---

### 3. Manage Categories

**GET /admin/categories**

List all categories.

**POST /admin/categories**

Create a new category.

**Form Data:**
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| name  | String | Yes    | Category name |

**POST /admin/categories/{id}/delete**

Delete a category.

---

### 4. Manage Tags

**GET /admin/tags**

List all tags.

**POST /admin/tags/{id}/delete**

Delete a tag.

---

### 5. Moderate Comments

**GET /admin/comments**

List all comments.

**POST /admin/comments/{id}/delete**

Delete a comment.

---

## Health & Monitoring

### Health Check

**GET /actuator/health**

Check application health status.

```http
GET /actuator/health HTTP/1.1
```

**Response:**
```json
{
  "status": "UP"
}
```

---

## Error Responses

### 400 Bad Request

Invalid request parameters or validation errors.

```html
<!-- Error page with validation messages -->
```

### 401 Unauthorized

User not authenticated.

**Response:** Redirect to `/login`

### 403 Forbidden

User doesn't have required permissions.

```html
<!-- Access denied page -->
```

### 404 Not Found

Resource not found.

```html
<!-- 404 error page -->
```

### 500 Internal Server Error

Server error occurred.

```html
<!-- 500 error page -->
```

---

## Rate Limiting

Currently, there is no rate limiting implemented. See [Security Guidelines](SECURITY.md) for recommendations.

---

## CSRF Protection

All POST/PUT/DELETE requests require a CSRF token.

**Token Location:** Hidden input field `_csrf` in forms

**Example:**
```html
<form method="POST" action="/author/articles">
    <input type="hidden" name="_csrf" value="<token>"/>
    <!-- other fields -->
</form>
```

---

## Content Encoding

All text content should be UTF-8 encoded.

---

## File Upload Limits

- **Max File Size:** 5MB
- **Max Request Size:** 5MB
- **Allowed Types:** JPG, PNG, GIF

---

## API Best Practices

1. **Always include CSRF token** in POST/PUT/DELETE requests
2. **Use HTTPS in production**
3. **Validate all inputs** on client and server side
4. **Handle errors gracefully**
5. **Sanitize HTML content** to prevent XSS

---

## Testing Endpoints

### Using curl

```bash
# Login
curl -X POST http://localhost:8080/login \
  -d "username=admin&password=password" \
  -c cookies.txt

# Create article (with session cookie)
curl -X POST http://localhost:8080/author/articles \
  -b cookies.txt \
  -F "title=Test Article" \
  -F "preview=Test preview" \
  -F "content=<p>Test content</p>" \
  -F "categoryId=1"
```

### Using Postman

1. Import the [Postman Collection](../postman/newsportal.postman_collection.json)
2. Set base URL environment variable
3. Run login request to get session cookie
4. Other requests will use the session automatically

---

## Next Steps

- [💾 Database Schema](DATABASE.md)
- [🚀 Deployment Guide](DEPLOYMENT.md)
- [🔐 Security Guidelines](SECURITY.md)

---

For issues or questions, see [Troubleshooting](TROUBLESHOOTING.md).
