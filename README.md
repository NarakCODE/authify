# Authify - Spring Boot Authentication API

A secure, production-ready authentication API built with Spring Boot, featuring email verification, password reset, and JWT-based authentication.

## Features

- ✅ **User Registration** with automatic email verification
- ✅ **Email Verification** using OTP (One-Time Password)
- ✅ **Secure Login** with JWT tokens
- ✅ **Password Reset** via email OTP
- ✅ **HTTP-only Cookies** for token storage
- ✅ **BCrypt Password Hashing**
- ✅ **Email Service Integration** (Brevo/SendGrid)
- ✅ **MySQL Database** with JPA/Hibernate
- ✅ **Production-Ready** with Railway deployment support

## Tech Stack

- **Java 17+**
- **Spring Boot 3.x**
- **Spring Security 6**
- **MySQL 8**
- **JWT (JSON Web Tokens)**
- **Brevo Email API**
- **Maven**

## Quick Start

### Prerequisites

- Java 17 or higher
- MySQL 8.0+
- Maven 3.6+
- Brevo account (for email service)

### 1. Clone the Repository

```bash
git clone https://github.com/YOUR_USERNAME/authify.git
cd authify
```

### 2. Configure Environment Variables

Copy the example environment file:

```bash
cp .env.example .env
```

Edit `.env` with your configuration:

```env
SPRING_PROFILES_ACTIVE=dev

DB_URL=jdbc:mysql://localhost:3306/authify_app
DB_USERNAME=root
DB_PASSWORD=your_password

JWT_SECRET_KEY=your_base64_secret_key_here
JWT_EXPIRATION_MS=3600000

SMTP_HOST=smtp-relay.brevo.com
SMTP_PORT=587
SMTP_USERNAME=your_brevo_username
SMTP_PASSWORD=your_brevo_password
SMTP_FROM=noreply@yourdomain.com
```

### 3. Create Database

```sql
CREATE DATABASE authify_app;
```

### 4. Run the Application

```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`

## API Endpoints

### Public Endpoints (No Authentication Required)

| Method | Endpoint                   | Description                   |
| ------ | -------------------------- | ----------------------------- |
| POST   | `/register`                | Register new user account     |
| POST   | `/verify-account`          | Verify email with OTP         |
| POST   | `/resend-verification-otp` | Resend verification OTP       |
| POST   | `/login`                   | Login with email and password |
| POST   | `/send-reset-otp`          | Request password reset OTP    |
| POST   | `/reset-password`          | Reset password with OTP       |
| POST   | `/logout`                  | Logout (clears cookie)        |

### Protected Endpoints (Authentication Required)

| Method | Endpoint            | Description                 |
| ------ | ------------------- | --------------------------- |
| GET    | `/profile`          | Get user profile            |
| GET    | `/is-authenticated` | Check authentication status |

## Usage Examples

### 1. Register a New User

```bash
curl -X POST http://localhost:8080/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "password": "SecurePassword123!"
  }'
```

**Response:**

```json
{
  "userId": "uuid-here",
  "name": "John Doe",
  "email": "john@example.com",
  "isAccountVerified": false
}
```

An OTP will be automatically sent to the email address.

### 2. Verify Email

```bash
curl -X POST http://localhost:8080/verify-account \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "otp": "123456"
  }'
```

### 3. Login

```bash
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "SecurePassword123!"
  }'
```

**Response:**

```json
{
  "email": "john@example.com",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### 4. Password Reset

**Step 1: Request OTP**

```bash
curl -X POST "http://localhost:8080/send-reset-otp?email=john@example.com"
```

**Step 2: Reset Password**

```bash
curl -X POST http://localhost:8080/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "otp": "123456",
    "newPassword": "NewSecurePassword123!"
  }'
```

## Security Features

### Email Verification Flow

- Users must verify their email before logging in
- OTP expires after 15 minutes
- OTP is invalidated after successful verification
- Unverified users cannot access protected endpoints

### Password Security

- Passwords hashed with BCrypt (strength 10)
- Password reset requires email verification
- OTP-based password reset (no password reset links)

### JWT Token Security

- HTTP-only cookies (prevents XSS attacks)
- Secure flag enabled in production
- SameSite=Strict (prevents CSRF attacks)
- 24-hour token expiration

### Rate Limiting Recommendations

For production, implement rate limiting on:

- `/resend-verification-otp`: 3 requests/hour per email
- `/send-reset-otp`: 3 requests/hour per email
- `/login`: 5 failed attempts/hour per IP

## Project Structure

```
authify/
├── src/
│   ├── main/
│   │   ├── java/in/narakcode/authify/
│   │   │   ├── config/          # Security & CORS configuration
│   │   │   ├── controller/      # REST API endpoints
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── entity/          # JPA entities
│   │   │   ├── exception/       # Exception handlers
│   │   │   ├── filter/          # JWT authentication filter
│   │   │   ├── repository/      # Database repositories
│   │   │   ├── service/         # Business logic
│   │   │   └── util/            # Utility classes (JWT)
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       ├── application-prod.properties
│   │       └── templates/       # Email templates
│   └── test/
├── .env.example                 # Environment variables template
├── .gitignore
├── pom.xml                      # Maven dependencies
├── Procfile                     # Railway deployment
├── system.properties            # Java version for deployment
└── README.md
```

## Documentation

- **[AUTHENTICATION_WORKFLOW.md](AUTHENTICATION_WORKFLOW.md)** - Complete authentication flow
- **[VERIFICATION_WORKFLOW.md](VERIFICATION_WORKFLOW.md)** - Email verification process
- **[PASSWORD_RESET_TESTING_GUIDE.md](PASSWORD_RESET_TESTING_GUIDE.md)** - Password reset testing
- **[OTP_IMPROVEMENTS_SUMMARY.md](OTP_IMPROVEMENTS_SUMMARY.md)** - Security improvements
- **[EMAIL_TEMPLATES_GUIDE.md](EMAIL_TEMPLATES_GUIDE.md)** - Email template customization
- **[RAILWAY_DEPLOYMENT.md](RAILWAY_DEPLOYMENT.md)** - Railway deployment guide
- **[DEPLOYMENT_CHECKLIST.md](DEPLOYMENT_CHECKLIST.md)** - Production deployment checklist

## Deployment

### Railway (Recommended)

1. Push your code to GitHub
2. Connect Railway to your GitHub repository
3. Add MySQL database in Railway
4. Configure environment variables
5. Deploy automatically

See [RAILWAY_QUICK_START.md](RAILWAY_QUICK_START.md) for detailed instructions.

### Docker

```bash
# Build
docker build -t authify:latest .

# Run
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_URL=jdbc:mysql://host:3306/authify_app \
  -e DB_USERNAME=user \
  -e DB_PASSWORD=password \
  -e JWT_SECRET_KEY=your_secret \
  authify:latest
```

### Traditional Server

```bash
# Build
./mvnw clean package -DskipTests

# Run
java -jar target/authify-*.jar \
  --spring.profiles.active=prod \
  --spring.datasource.url=jdbc:mysql://host:3306/authify_app
```

## Configuration

### Database Configuration

**Development (application-dev.properties):**

```properties
spring.datasource.url=${DB_URL}
spring.jpa.hibernate.ddl-auto=update
```

**Production (application-prod.properties):**

```properties
spring.datasource.url=${DB_URL}
spring.jpa.hibernate.ddl-auto=validate
```

### Email Configuration

Configure Brevo (or any SMTP service) in your `.env`:

```env
SMTP_HOST=smtp-relay.brevo.com
SMTP_PORT=587
SMTP_USERNAME=your_username
SMTP_PASSWORD=your_password
SMTP_FROM=noreply@yourdomain.com
```

### JWT Configuration

Generate a secure secret key:

```bash
openssl rand -base64 64
```

Add to `.env`:

```env
JWT_SECRET_KEY=your_generated_key_here
JWT_EXPIRATION_MS=3600000  # 1 hour in milliseconds
```

## Testing

Run tests:

```bash
./mvnw test
```

Run with coverage:

```bash
./mvnw test jacoco:report
```

## Common Issues

### Issue: Email not sending

**Solution:** Check Brevo API credentials and ensure SMTP settings are correct in `.env`

### Issue: Database connection failed

**Solution:** Verify MySQL is running and credentials in `.env` are correct

### Issue: JWT token invalid

**Solution:** Ensure JWT_SECRET_KEY is properly set and matches between requests

### Issue: CORS errors

**Solution:** Update allowed origins in `SecurityConfig.java` to include your frontend URL

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For issues and questions:

- Open an issue on GitHub
- Check the documentation files in the repository
- Review the code comments for implementation details

## Acknowledgments

- Spring Boot team for the excellent framework
- Brevo for email service
- Railway for easy deployment platform

---

**Built with ❤️ using Spring Boot**
