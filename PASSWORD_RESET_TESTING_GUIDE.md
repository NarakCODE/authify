# Password Reset Testing Guide

## Overview

The password reset flow allows users to reset their password using an OTP (One-Time Password) sent to their email. This is a secure, public flow that doesn't require authentication.

## Password Reset Flow

```
User Forgot Password
        ↓
Request Reset OTP (POST /send-reset-otp)
        ↓
Receive 6-digit OTP via Email (expires in 15 minutes)
        ↓
Submit New Password + OTP (POST /reset-password)
        ↓
Password Updated & Confirmation Email Sent
        ↓
Login with New Password
```

---

## API Endpoints

### 1. Request Password Reset OTP

**Endpoint:** `POST /send-reset-otp`

**Authentication:** Not required (Public endpoint)

**Request:**

```http
POST /send-reset-otp?email=user@example.com
```

**cURL Example:**

```bash
curl -X POST "http://localhost:8080/send-reset-otp?email=john@example.com"
```

**Success Response:**

```
HTTP/1.1 200 OK
Content-Type: text/plain

Password reset OTP sent successfully
```

**Error Responses:**

| Status                  | Message                                        | Reason                |
| ----------------------- | ---------------------------------------------- | --------------------- |
| 404 Not Found           | "No account found with this email address"     | Email doesn't exist   |
| 503 Service Unavailable | "Failed to send email. Please try again later" | Email service failure |

**What Happens:**

1. System validates email exists in database
2. Generates random 6-digit OTP (e.g., "123456")
3. Sets expiration time to 15 minutes from now
4. Saves OTP and expiration to database
5. Sends email with OTP to user
6. Returns success message

**Email Content:**

```
Subject: Reset Password

Dear User,

Please use the following OTP to reset your password: 123456

Best regards,
Authify Team
```

---

### 2. Reset Password with OTP

**Endpoint:** `POST /reset-password`

**Authentication:** Not required (Public endpoint)

**Request:**

```http
POST /reset-password
Content-Type: application/json

{
  "email": "user@example.com",
  "otp": "123456",
  "newPassword": "NewSecurePassword123!"
}
```

**cURL Example:**

```bash
curl -X POST http://localhost:8080/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "otp": "123456",
    "newPassword": "NewSecurePassword123!"
  }'
```

**Success Response:**

```
HTTP/1.1 200 OK
Content-Type: text/plain

Password reset successfully
```

**Error Responses:**

| Status          | Message                                     | Reason                    |
| --------------- | ------------------------------------------- | ------------------------- |
| 404 Not Found   | "No account found with this email address"  | Email doesn't exist       |
| 400 Bad Request | "Invalid OTP. Please check and try again"   | Wrong OTP entered         |
| 400 Bad Request | "OTP has expired. Please request a new one" | OTP older than 15 minutes |
| 400 Bad Request | "New password is required"                  | Missing newPassword field |
| 400 Bad Request | "OTP is required"                           | Missing otp field         |
| 400 Bad Request | "Email is required"                         | Missing email field       |

**What Happens:**

1. System validates email exists
2. Checks if OTP matches the one in database
3. Verifies OTP hasn't expired (15-minute window)
4. Hashes the new password using BCrypt
5. Updates user's password in database
6. Clears/invalidates the OTP (sets to null)
7. Sends confirmation email to user
8. Returns success message

**Confirmation Email Content:**

```
Subject: Your Password Has Been Reset

Dear [User Name],

Your password for Authify has been successfully reset.

If you did not make this change, please contact our support team immediately.

Best regards,
Authify Team
```

---

## Complete Testing Scenarios

### Scenario 1: Successful Password Reset ✅

```bash
# Step 1: Request OTP
curl -X POST "http://localhost:8080/send-reset-otp?email=john@example.com"

# Expected Response: "Password reset OTP sent successfully"
# Check email inbox for OTP (e.g., "123456")

# Step 2: Reset password with OTP
curl -X POST http://localhost:8080/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "otp": "123456",
    "newPassword": "MyNewPassword123!"
  }'

# Expected Response: "Password reset successfully"
# Check email for confirmation

# Step 3: Login with new password
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "MyNewPassword123!"
  }'

# Expected Response: JWT token and success
```

---

### Scenario 2: Invalid Email ❌

```bash
curl -X POST "http://localhost:8080/send-reset-otp?email=nonexistent@example.com"

# Expected Response: 404 Not Found
# {
#   "error": true,
#   "message": "No account found with this email address"
# }
```

---

### Scenario 3: Wrong OTP ❌

```bash
# Step 1: Request OTP
curl -X POST "http://localhost:8080/send-reset-otp?email=john@example.com"

# Step 2: Try to reset with wrong OTP
curl -X POST http://localhost:8080/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "otp": "999999",
    "newPassword": "MyNewPassword123!"
  }'

# Expected Response: 400 Bad Request
# {
#   "error": true,
#   "message": "Invalid OTP. Please check and try again"
# }
```

---

### Scenario 4: Expired OTP ❌

```bash
# Step 1: Request OTP
curl -X POST "http://localhost:8080/send-reset-otp?email=john@example.com"

# Step 2: Wait more than 15 minutes...

# Step 3: Try to reset with expired OTP
curl -X POST http://localhost:8080/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "otp": "123456",
    "newPassword": "MyNewPassword123!"
  }'

# Expected Response: 400 Bad Request
# {
#   "error": true,
#   "message": "OTP has expired. Please request a new one"
# }

# Step 4: Request new OTP
curl -X POST "http://localhost:8080/send-reset-otp?email=john@example.com"

# Now use the new OTP within 15 minutes
```

---

### Scenario 5: Multiple OTP Requests (OTP Replacement)

```bash
# Step 1: Request first OTP
curl -X POST "http://localhost:8080/send-reset-otp?email=john@example.com"
# Receives OTP: 123456

# Step 2: Request second OTP (before using first one)
curl -X POST "http://localhost:8080/send-reset-otp?email=john@example.com"
# Receives new OTP: 789012

# Step 3: Try to use first OTP (should fail)
curl -X POST http://localhost:8080/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "otp": "123456",
    "newPassword": "MyNewPassword123!"
  }'

# Expected Response: 400 Bad Request - "Invalid OTP"

# Step 4: Use second OTP (should work)
curl -X POST http://localhost:8080/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "otp": "789012",
    "newPassword": "MyNewPassword123!"
  }'

# Expected Response: "Password reset successfully"
```

---

### Scenario 6: OTP Reuse Prevention ❌

```bash
# Step 1: Request OTP
curl -X POST "http://localhost:8080/send-reset-otp?email=john@example.com"

# Step 2: Reset password successfully
curl -X POST http://localhost:8080/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "otp": "123456",
    "newPassword": "MyNewPassword123!"
  }'

# Expected Response: "Password reset successfully"

# Step 3: Try to reuse the same OTP
curl -X POST http://localhost:8080/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "otp": "123456",
    "newPassword": "AnotherPassword456!"
  }'

# Expected Response: 400 Bad Request - "Invalid OTP"
# (OTP was cleared after successful use)
```

---

### Scenario 7: Missing Required Fields ❌

```bash
# Missing email
curl -X POST http://localhost:8080/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "otp": "123456",
    "newPassword": "MyNewPassword123!"
  }'

# Expected Response: 400 Bad Request - "Email is required"

# Missing OTP
curl -X POST http://localhost:8080/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "newPassword": "MyNewPassword123!"
  }'

# Expected Response: 400 Bad Request - "OTP is required"

# Missing password
curl -X POST http://localhost:8080/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "otp": "123456"
  }'

# Expected Response: 400 Bad Request - "New password is required"
```

---

## Postman Collection

### Request 1: Send Reset OTP

```
Method: POST
URL: http://localhost:8080/send-reset-otp?email={{user_email}}
Headers: (none required)
Body: (none)
```

### Request 2: Reset Password

```
Method: POST
URL: http://localhost:8080/reset-password
Headers:
  Content-Type: application/json
Body (raw JSON):
{
  "email": "{{user_email}}",
  "otp": "{{otp_from_email}}",
  "newPassword": "{{new_password}}"
}
```

### Request 3: Login with New Password

```
Method: POST
URL: http://localhost:8080/login
Headers:
  Content-Type: application/json
Body (raw JSON):
{
  "email": "{{user_email}}",
  "password": "{{new_password}}"
}
```

---

## Security Features

### 1. **OTP Expiration**

- OTPs expire after 15 minutes
- Expired OTPs are rejected with clear error message
- Users must request new OTP if expired

### 2. **OTP Invalidation**

- OTP is cleared from database after successful password reset
- Prevents OTP reuse
- Each password reset requires a fresh OTP

### 3. **OTP Replacement**

- Requesting new OTP invalidates previous one
- Only the most recent OTP is valid
- Prevents confusion with multiple OTP requests

### 4. **Password Hashing**

- New passwords are hashed using BCrypt
- Original password never stored in plain text
- Secure password storage

### 5. **Email Confirmation**

- User receives confirmation email after password reset
- Alerts user if password was changed without their knowledge
- Includes security warning to contact support

### 6. **No Authentication Required**

- Public endpoints for password reset
- Users can reset password even if locked out
- Email + OTP provides sufficient security

---

## Database Changes

When password reset is initiated:

```sql
-- After /send-reset-otp
UPDATE users
SET resetOtp = '123456',
    resetOtpExpireAt = 1699123456789  -- Current time + 15 minutes
WHERE email = 'user@example.com';
```

When password is successfully reset:

```sql
-- After /reset-password
UPDATE users
SET password = '$2a$10$hashedPasswordHere',
    resetOtp = NULL,
    resetOtpExpireAt = 0
WHERE email = 'user@example.com';
```

---

## Common Issues & Troubleshooting

### Issue 1: Not Receiving OTP Email

**Possible Causes:**

- Email service (Brevo) configuration issue
- Invalid API key
- Email in spam/junk folder
- Email address typo

**Solution:**

1. Check application logs for email sending errors
2. Verify Brevo API key in application.properties
3. Check spam folder
4. Verify email address is correct

---

### Issue 2: OTP Always Invalid

**Possible Causes:**

- Using old OTP after requesting new one
- Typo in OTP entry
- OTP expired (>15 minutes)
- Database not updated

**Solution:**

1. Check database for current OTP value
2. Verify OTP expiration timestamp
3. Request fresh OTP
4. Copy-paste OTP to avoid typos

---

### Issue 3: Password Not Updating

**Possible Causes:**

- Transaction rollback
- Database connection issue
- Validation error

**Solution:**

1. Check application logs for errors
2. Verify database connection
3. Test with simple password first
4. Check for any password validation rules

---

## Testing Checklist

- [ ] Request OTP with valid email → Success
- [ ] Request OTP with invalid email → 404 Error
- [ ] Reset password with valid OTP → Success
- [ ] Reset password with invalid OTP → 400 Error
- [ ] Reset password with expired OTP → 400 Error
- [ ] Try to reuse OTP after successful reset → 400 Error
- [ ] Request multiple OTPs → Only latest works
- [ ] Login with new password → Success
- [ ] Login with old password → Fail
- [ ] Receive confirmation email after reset
- [ ] OTP expires after 15 minutes
- [ ] Missing required fields → Validation errors

---

## Production Recommendations

### 1. Rate Limiting

Implement rate limiting to prevent abuse:

```
/send-reset-otp: Max 3 requests per email per hour
/reset-password: Max 5 attempts per email per hour
```

### 2. Account Lockout

After multiple failed attempts:

- Temporarily lock password reset for that email
- Require CAPTCHA verification
- Send security alert email

### 3. Monitoring

Log and monitor:

- Failed OTP attempts
- Multiple OTP requests from same IP
- Password reset patterns
- Suspicious activity

### 4. Enhanced Security

Consider adding:

- CAPTCHA on OTP request
- SMS as backup OTP delivery
- Security questions
- Two-factor authentication

### 5. User Experience

Improve UX with:

- OTP expiration countdown timer
- Auto-fill OTP from email (on mobile)
- Password strength indicator
- Clear error messages with next steps

---

## Quick Reference

| Action         | Endpoint                        | Method | Auth | Body                        |
| -------------- | ------------------------------- | ------ | ---- | --------------------------- |
| Request OTP    | `/send-reset-otp?email={email}` | POST   | No   | None                        |
| Reset Password | `/reset-password`               | POST   | No   | `{email, otp, newPassword}` |
| Login          | `/login`                        | POST   | No   | `{email, password}`         |

**OTP Validity:** 15 minutes
**OTP Format:** 6 digits (e.g., "123456")
**OTP Generation:** Random (100000-999999)
