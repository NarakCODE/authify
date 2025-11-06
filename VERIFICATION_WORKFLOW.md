# Email Verification Workflow

## Overview

This document describes the improved email verification workflow that ensures users must verify their email address before they can access protected resources.

## Security Improvements

### 1. **Account Verification Required for Login**

- Users cannot login until their email is verified
- `AppUserDetailsService` checks `isAccountVerified` status
- Unverified accounts receive a 403 Forbidden error with message: "Account is not verified. Please verify your email address"

### 2. **Automatic OTP Sending on Registration**

- OTP is automatically sent when a user registers
- No need for users to manually request the first OTP
- OTP expires in 15 minutes (configurable)

### 3. **Public Resend OTP Endpoint**

- Unverified users can request a new OTP without authentication
- Endpoint: `POST /resend-verification-otp`
- Prevents the issue where users couldn't verify because they needed to be logged in

### 4. **Public Verification Endpoint**

- Users can verify their account without being logged in
- Endpoint: `POST /verify-account`
- After verification, users can login normally

## API Endpoints

### 1. Register (Public)

```http
POST /register
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "SecurePassword123!"
}
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

**Behavior:**

- Creates user account with `isAccountVerified = false`
- Automatically sends 6-digit OTP to user's email
- OTP expires in 15 minutes

---

### 2. Verify Account (Public)

```http
POST /verify-account
Content-Type: application/json

{
  "email": "john@example.com",
  "otp": "123456"
}
```

**Response:**

```
Account verified successfully. You can now login
```

**Behavior:**

- Validates OTP and expiration time
- Sets `isAccountVerified = true`
- Clears OTP from database
- Sends welcome email
- User can now login

**Error Cases:**

- Invalid OTP: `400 Bad Request - "Invalid OTP. Please check and try again"`
- Expired OTP: `400 Bad Request - "OTP has expired. Please request a new one"`
- Already verified: `400 Bad Request - "Your account is already verified"`
- Email not found: `404 Not Found - "No account found with this email address"`

---

### 3. Resend Verification OTP (Public)

```http
POST /resend-verification-otp
Content-Type: application/json

{
  "email": "john@example.com"
}
```

**Response:**

```
Verification OTP sent successfully
```

**Behavior:**

- Generates new 6-digit OTP
- Updates expiration time (15 minutes from now)
- Sends OTP via email
- Can be called multiple times (consider rate limiting in production)

**Error Cases:**

- Already verified: `400 Bad Request - "Your account is already verified"`
- Email not found: `404 Not Found - "No account found with this email address"`
- Email service failure: `503 Service Unavailable - "Failed to send verification email. Please try again later"`

---

### 4. Login (Public)

```http
POST /login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "SecurePassword123!"
}
```

**Success Response:**

```json
{
  "email": "john@example.com",
  "token": "jwt-token-here"
}
```

**Behavior:**

- Checks if account is verified
- If not verified, returns 403 Forbidden
- If verified, generates JWT token and sets HTTP-only cookie

**Error Cases:**

- Unverified account: `403 Forbidden - "Account is not verified. Please verify your email address"`
- Invalid credentials: `401 Unauthorized - "Email or password is incorrect"`

---

### 5. Verify OTP (Authenticated - Legacy)

```http
POST /verify-otp?otp=123456
Authorization: Bearer <jwt-token>
```

**Note:** This endpoint is kept for backward compatibility but is not needed in the new flow since users verify before login.

---

## User Flow Diagram

```
┌─────────────────┐
│  User Registers │
└────────┬────────┘
         │
         ▼
┌─────────────────────────┐
│ Account Created         │
│ isAccountVerified=false │
│ OTP Sent Automatically  │
└────────┬────────────────┘
         │
         ▼
┌─────────────────────┐
│ User Receives Email │
│ with 6-digit OTP    │
└────────┬────────────┘
         │
         ▼
    ┌────────┐
    │ Valid? │
    └───┬────┘
        │
    ┌───┴───┐
    │       │
   Yes     No
    │       │
    │       ▼
    │   ┌──────────────────┐
    │   │ OTP Expired or   │
    │   │ Invalid?         │
    │   └────────┬─────────┘
    │            │
    │            ▼
    │   ┌──────────────────┐
    │   │ Resend OTP       │
    │   │ /resend-         │
    │   │ verification-otp │
    │   └────────┬─────────┘
    │            │
    │            └──────┐
    │                   │
    ▼                   ▼
┌─────────────────────────┐
│ POST /verify-account    │
│ with email & OTP        │
└────────┬────────────────┘
         │
         ▼
┌─────────────────────────┐
│ Account Verified        │
│ isAccountVerified=true  │
│ Welcome Email Sent      │
└────────┬────────────────┘
         │
         ▼
┌─────────────────┐
│ User Can Login  │
│ POST /login     │
└────────┬────────┘
         │
         ▼
┌─────────────────────┐
│ Access Protected    │
│ Resources           │
└─────────────────────┘
```

## Security Best Practices Implemented

### 1. **OTP Expiration**

- OTPs expire after 15 minutes
- Reduces window for brute force attacks
- Forces users to request new OTP if delayed

### 2. **OTP Invalidation**

- OTP is cleared from database after successful verification
- OTP is cleared after expiration check fails
- Prevents OTP reuse

### 3. **Account Status Enforcement**

- Login is blocked for unverified accounts
- Returns clear error message to guide users
- Uses HTTP 403 Forbidden (not 401) to indicate account exists but access is forbidden

### 4. **Public Verification Endpoints**

- Users don't need to be authenticated to verify
- Prevents chicken-and-egg problem
- Email + OTP combination provides sufficient security

### 5. **Error Messages**

- Clear, actionable error messages
- Doesn't leak sensitive information
- Guides users to next steps

### 6. **Input Validation**

- Email format validation
- OTP format validation (6 digits)
- Required field validation

## Database Schema

```sql
-- UserEntity fields related to verification
isAccountVerified BOOLEAN DEFAULT false
verifyOtp VARCHAR(6)
verifyOtpExpireAt BIGINT
```

## Configuration

### OTP Settings

- **Length:** 6 digits
- **Expiration:** 15 minutes (900,000 milliseconds)
- **Generation:** ThreadLocalRandom for cryptographic randomness

### Email Service

- Uses Brevo API for email delivery
- Async email sending to avoid blocking
- Proper error handling and logging

## Recommendations for Production

### 1. **Rate Limiting**

Implement rate limiting on public endpoints to prevent abuse:

- `/resend-verification-otp`: Max 3 requests per email per hour
- `/verify-account`: Max 5 attempts per email per hour

### 2. **Account Lockout**

After multiple failed OTP attempts:

- Temporarily lock verification attempts
- Require manual intervention or extended cooldown

### 3. **Monitoring**

- Log failed verification attempts
- Monitor OTP request patterns
- Alert on suspicious activity

### 4. **OTP Delivery**

- Consider SMS as backup delivery method
- Implement retry logic for email failures
- Provide clear instructions in emails

### 5. **User Experience**

- Show OTP expiration countdown in UI
- Auto-focus OTP input field
- Provide "Resend OTP" button with cooldown timer
- Show clear success/error messages

## Migration Guide

If you have existing users in the database:

1. **Option A: Force Verification**

   ```sql
   -- All existing users need to verify
   UPDATE users SET isAccountVerified = false WHERE isAccountVerified IS NULL;
   ```

2. **Option B: Grandfather Existing Users**

   ```sql
   -- Existing users are auto-verified
   UPDATE users SET isAccountVerified = true WHERE isAccountVerified IS NULL;
   ```

3. **Option C: Gradual Migration**
   - Set existing users as verified
   - New registrations require verification
   - Optionally prompt existing users to verify for added security

## Testing Checklist

- [ ] Register new user → OTP sent automatically
- [ ] Verify with correct OTP → Account verified
- [ ] Verify with incorrect OTP → Error message
- [ ] Verify with expired OTP → Error message
- [ ] Try to login before verification → 403 Forbidden
- [ ] Login after verification → Success
- [ ] Resend OTP → New OTP sent
- [ ] Resend OTP for verified account → Error message
- [ ] Verify already verified account → Error message
- [ ] OTP expires after 15 minutes
- [ ] Welcome email sent after verification

## Troubleshooting

### User can't receive OTP

1. Check email service configuration
2. Verify Brevo API key is valid
3. Check spam/junk folder
4. Review application logs for email sending errors

### User says OTP is invalid

1. Verify OTP hasn't expired (15 minutes)
2. Check for typos in OTP entry
3. Ensure user is using the most recent OTP
4. Check database for OTP value and expiration

### User locked out

1. Verify account status in database
2. Check OTP expiration timestamp
3. Manually resend OTP if needed
4. Consider manual verification for support cases
