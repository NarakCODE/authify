# Email Templates Preview

## Quick Preview Guide

To preview the email templates in your browser, simply open the HTML files directly:

### Windows

```cmd
start src/main/resources/templates/welcome-email.html
start src/main/resources/templates/verification-otp-email.html
start src/main/resources/templates/reset-password-otp-email.html
start src/main/resources/templates/password-reset-success-email.html
```

### Preview with Sample Data

Replace the placeholders before opening:

1. **Welcome Email**

   - Replace `{{name}}` with `John Doe`

2. **Verification OTP Email**

   - Replace `{{otp}}` with `123456`

3. **Reset Password OTP Email**

   - Replace `{{otp}}` with `789012`

4. **Password Reset Success Email**
   - Replace `{{name}}` with `John Doe`

---

## Email Samples

### 1. Welcome Email

```
Subject: Welcome to Authify! 🎉
To: john@example.com

[Purple gradient header with "Welcome to Authify! 🎉"]

Hi John Doe,

Thank you for verifying your email address! Your account is now fully
activated and ready to use.

We're excited to have you on board. You can now access all features of
your account.

✓ Secure authentication
✓ Profile management
✓ Password reset capability
✓ Email notifications

If you have any or need assistance, feel free to reach out to
our support team.

Best regards,
The Authify Team
```

---

### 2. Verification OTP Email

```
Subject: Verify Your Email Address - OTP Inside
To: john@example.com

[Purple gradient header with "Verify Your Email Address"]

Hello,

Thank you for registering with Authify! To complete your registration,
please verify your email address using the OTP below.

┌─────────────────────────┐
│ Your Verification Code  │
│                         │
│      1 2 3 4 5 6       │
└─────────────────────────┘

⚠️ Important: This code will expire in 15 minutes. If you didn't
request this code, please ignore this email.

If you're having trouble verifying your account, you can request a new
verification code from the application.

Best regards,
The Authify Team
```

---

### 3. Password Reset OTP Email

```
Subject: Reset Your Password - OTP Inside
To: john@example.com

[Pink/Red gradient header with "🔐 Password Reset Request"]

Hello,

We received a request to reset your password. Use the OTP below to
proceed with resetting your password.

┌─────────────────────────┐
│ Your Password Reset Code│
│                         │
│      7 8 9 0 1 2       │
└─────────────────────────┘

⚠️ Security Alert: This code will expire in 15 minutes. If you didn't
request a password reset, please ignore this email and your password
will remain unchanged.

💡 Tip: For your security, we recommend using a strong password that
includes uppercase letters, lowercase letters, numbers, and special
characters.

If you're experiencing issues, please contact our support team for
assistance.

Best regards,
The Authify Team
```

---

### 4. Password Reset Success Email

```
Subject: Password Reset Successful
To: john@example.com

[Green gradient header with "✅ Password Reset Successful"]

Hi John Doe,

Your password for Authify has been successfully reset.

✓ Your password has been updated
✓ You can now login with your new password
✓ Your account remains secure

🔒 Security Notice: If you did not make this change, please contact our
support team immediately. Your account security is our top priority.

Security Tips:
• Never share your password with anyone
• Use a unique password for each account
• Enable two-factor authentication when available
• Change your password regularly

If you have any questions or concerns, please don't hesitate to contact
our support team.

Best regards,
The Authify Team
```

---

## Testing in Real Email Clients

### Option 1: Use Test Endpoint (Recommended)

Add this to your controller for testing:

```java
@GetMapping("/test-emails")
public ResponseEntity<String> testEmails(@RequestParam String email) {
    // Test all email templates
    emailService.sendWelcomeEmail(email, "Test User");
    emailService.sendOtpEmail(email, "123456");
    emailService.sendResetOtpEmail(email, "789012");
    emailService.sendPasswordResetSuccessEmail(email, "Test User");

    return ResponseEntity.ok("Test emails sent to: " + email);
}
```

Then call:

```bash
curl "http://localhost:8080/test-emails?email=your-email@example.com"
```

### Option 2: Use Existing Flows

1. **Test Welcome Email:**

   - Register a new account
   - Verify with OTP
   - Check inbox for welcome email

2. **Test Verification OTP:**

   - Register a new account
   - Check inbox for OTP email

3. **Test Reset OTP:**

   - Request password reset
   - Check inbox for reset OTP email

4. **Test Reset Success:**
   - Complete password reset
   - Check inbox for success email

---

## Visual Preview

### Color Palette

**Welcome & Verification:**

- Primary: `#667eea` (Purple)
- Secondary: `#764ba2` (Dark Purple)
- Background: `#f4f4f4` (Light Gray)

**Password Reset OTP:**

- Primary: `#f093fb` (Pink)
- Secondary: `#f5576c` (Red)
- Alert: `#dc3545` (Danger Red)

**Reset Success:**

- Primary: `#11998e` (Teal)
- Secondary: `#38ef7d` (Green)
- Success: `#28a745` (Success Green)

### Typography

- **Headers:** 28-32px, Bold, White
- **Body:** 16px, Regular, #333333
- **Small Text:** 14px, Regular, #666666
- **Footer:** 12px, Regular, #999999

### Layout

```
┌─────────────────────────────────┐
│         Header (Gradient)        │
│         Title (White)            │
└─────────────────────────────────┘
│                                  │
│         Content Area             │
│         (White Background)       │
│                                  │
│  ┌────────────────────────┐     │
│  │    OTP Box (Gray BG)   │     │
│  │      Large Number      │     │
│  └────────────────────────┘     │
│                                  │
│  ┌────────────────────────┐     │
│  │  Warning Box (Yellow)  │     │
│  └────────────────────────┘     │
│                                  │
└─────────────────────────────────┘
│         Footer (Gray)            │
│      Company Info & Links        │
└─────────────────────────────────┘
```

---

## Mobile Preview

All templates are responsive and will look great on mobile devices:

- Maximum width: 600px
- Scales down automatically
- Touch-friendly spacing
- Large, readable text
- Prominent CTAs

### Test on Mobile

1. Send test email to your phone
2. Open in Gmail app
3. Open in native mail app
4. Check layout and readability

---

## Spam Score Testing

Use [Mail Tester](https://www.mail-tester.com/) to check spam score:

1. Get test email from Mail Tester
2. Send email to that address
3. Check your score (aim for 10/10)
4. Fix any issues reported

Common issues:

- Missing unsubscribe link
- No plain text version
- Suspicious links
- Poor sender reputation

---

## Customization Examples

### Add Company Logo

```html
<tr>
  <td style="padding: 20px 40px 0 40px; text-align: center;">
    <img
      src="https://your-domain.com/logo.png"
      alt="Company Logo"
      style="width: 120px; height: auto;"
    />
  </td>
</tr>
```

### Add Call-to-Action Button

```html
<table role="presentation" style="margin: 30px auto;">
  <tr>
    <td style="background-color: #667eea; border-radius: 4px;">
      <a
        href="https://your-app.com/login"
        style="display: inline-block; padding: 15px 40px;
                      color: #ffffff; text-decoration: none;
                      font-weight: 600; font-size: 16px;"
      >
        Login Now
      </a>
    </td>
  </tr>
</table>
```

### Add Social Media Icons

```html
<p style="text-align: center; margin: 20px 0;">
  <a href="https://twitter.com/yourcompany" style="margin: 0 10px;">
    <img
      src="https://cdn.example.com/twitter.png"
      alt="Twitter"
      style="width: 32px; height: 32px;"
    />
  </a>
  <a href="https://facebook.com/yourcompany" style="margin: 0 10px;">
    <img
      src="https://cdn.example.com/facebook.png"
      alt="Facebook"
      style="width: 32px; height: 32px;"
    />
  </a>
</p>
```

---

## Quick Checklist

Before sending emails to production:

- [ ] Test in Gmail
- [ ] Test in Outlook
- [ ] Test on mobile device
- [ ] Check spam score
- [ ] Verify all links work
- [ ] Check images load
- [ ] Test with real data
- [ ] Verify unsubscribe link (if applicable)
- [ ] Check sender name and email
- [ ] Test in dark mode (if supported)
