# Pastebin Lite

A simple pastebin application built with Spring Boot and Redis. Supports TTL expiration and view count limits.

## ✅ Bug Fixes Applied

This version includes critical bug fixes:
1. **View counting** - Fixed to use single Redis key with atomic updates
2. **URL generation** - Dynamic URLs from request headers (no hardcoded localhost)
3. **Paste deletion** - Expired/exhausted pastes are now properly deleted

## Features

- Create text pastes with optional TTL (time-to-live) and view limits
- Share pastes via unique URLs
- View pastes in HTML format
- RESTful API for programmatic access
- Redis persistence for data storage
- TEST_MODE support for deterministic time testing

## Deployment

**Platform:** Render.com (or Railway.app)
**Database:** Redis (managed instance)

See `DEPLOYMENT_GUIDE.md` for complete deployment instructions.

## Prerequisites (Local Development)

- Java 17 or higher
- Maven 3.6+
- Redis server (running on localhost:6379)

## Running Locally

1. **Start Redis server**
   ```bash
   redis-server
   ```

2. **Clone/Download the project**

3. **Build and run the application**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

4. **Access the application**
   - Web UI: http://localhost:8081
   - API: http://localhost:8081/api/

## API Endpoints

### Health Check
```
GET /api/healthz
```

### Create Paste
```
POST /api/pastes
Content-Type: application/json

{
  "content": "Your text here",
  "ttl_seconds": 3600,      // optional
  "max_views": 10           // optional
}
```

### Get Paste (API)
```
GET /api/pastes/{id}
```
Each API fetch counts as a view.

### View Paste (HTML)
```
GET /p/{id}
```

## Persistence Layer

This application uses **Redis** as the persistence layer:
- Pastes are stored as serialized objects in Redis
- TTL-based expiration is handled by Redis automatically
- View count tracking with atomic operations
- Pastes are deleted when they become unavailable

**Production Deployment:**
- Uses managed Redis instance on Render.com
- SSL/TLS enabled for secure connections
- Environment variables for configuration

## Environment Variables

Required for deployment:
- `PORT` - Server port (default: 8081)
- `REDIS_HOST` - Redis server hostname
- `REDIS_PORT` - Redis server port
- `REDIS_PASSWORD` - Redis password
- `REDIS_SSL` - Enable SSL (true/false)
- `TEST_MODE=1` - Enables deterministic time testing with `x-test-now-ms` header

## Testing

The application supports deterministic time testing for TTL validation:

1. Set environment variable: `TEST_MODE=1`
2. Include header in requests: `x-test-now-ms: <timestamp-in-milliseconds>`

## Project Structure

```
src/
├── main/
│   ├── java/com/pastebin/demo/
│   │   ├── controller/     # REST and Web controllers
│   │   ├── service/        # Business logic (FIXED view counting)
│   │   ├── model/          # Data models
│   │   ├── dto/            # Data transfer objects
│   │   └── config/         # Configuration classes
│   └── resources/
│       ├── templates/      # Thymeleaf HTML templates
│       └── application.properties
```

## Technologies Used

- Spring Boot 3.2.0
- Spring Data Redis
- Thymeleaf
- Lettuce (Redis client)
- Java 17

## Design Decisions

1. **View Counting:** Uses single Redis key with paste object for atomic updates
2. **URL Generation:** Dynamic based on request headers (no hardcoded URLs)
3. **Paste Lifecycle:** Automatic deletion when expired or view limit reached
4. **Combined Constraints:** TTL and max_views both respected - first to trigger wins
5. **XSS Protection:** Thymeleaf template engine prevents script injection
6. **TEST_MODE:** Supports deterministic time for automated testing

## Deployment Platforms

- **Recommended:** Render.com (free tier with Redis)
- **Alternative:** Railway.app (free $5 credit)
- **Not Compatible:** Vercel (only supports Node.js/Python/Go)

See `DEPLOYMENT_GUIDE.md` for step-by-step deployment instructions.
