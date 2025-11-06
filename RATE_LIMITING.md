# Rate Limiting Implementation

## Overview

This application implements IP-based rate limiting using Bucket4j to protect against API abuse, brute force attacks, and DDoS attempts.

## Protected Endpoints

### Login Endpoint (`/login`)

- **Limit**: 5 attempts per IP
- **Refill**: 5 tokens every 15 minutes
- **Purpose**: Prevents brute force password attacks

### OTP Endpoints

Protected endpoints:

- `/send-reset-otp`
- `/resend-verification-otp`
- `/reset-password`
- `/verify-account`

**Configuration**:

- **Limit**: 3 attempts per IP
- **Refill**: 3 tokens every 10 minutes
- **Purpose**: Prevents OTP spam and email flooding

### Registration Endpoint (`/register`)

- **Limit**: 100 attempts per IP
- **Refill**: 100 tokens every 1 minute
- **Purpose**: Prevents mass account creation

## Configuration

Rate limits are configured in `application.properties`:

```properties
# Login rate limiting
rate.limit.login.capacity=5
rate.limit.login.refill.tokens=5
rate.limit.login.refill.minutes=15

# OTP rate limiting
rate.limit.otp.capacity=3
rate.limit.otp.refill.tokens=3
rate.limit.otp.refill.minutes=10

# General rate limiting
rate.limit.general.capacity=100
rate.limit.general.refill.tokens=100
rate.limit.general.refill.minutes=1
```

## Response Headers

### Successful Request

- `X-Rate-Limit-Remaining`: Number of remaining tokens

### Rate Limited Request (429 Too Many Requests)

- `X-Rate-Limit-Retry-After-Seconds`: Seconds until tokens refill

**Response Body**:

```json
{
  "error": true,
  "message": "Too many requests. Please try again later.",
  "retryAfterSeconds": 900
}
```

## IP Detection

The filter detects client IP from:

1. `X-Forwarded-For` header (for proxies/load balancers)
2. `X-Real-IP` header (for reverse proxies)
3. `RemoteAddr` (direct connection)

This ensures accurate rate limiting behind proxies like Nginx or Railway.

## Customization

To adjust rate limits for production:

1. Edit `application.properties` or use environment variables
2. Restart the application
3. Buckets are created per IP address dynamically

## Security Benefits

✅ Prevents brute force login attacks
✅ Stops OTP/email spam
✅ Protects against account enumeration
✅ Mitigates DDoS attempts
✅ Reduces server load from malicious traffic
