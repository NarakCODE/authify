# Email Templates Guide

## Overview

This project uses professional HTML email templates that are compatible with all major email clients including Gmail, Outlook, Yahoo, and mobile devices.

## Templates Available

### 1. Welcome Email (`welcome-email.html`)

**Sent when:** User successfully verifies their email address
**Variables:** `{{name}}`
**Subject:** "Welcome to Authify! 🎉"

**Features:**

- Purple gradient header
- Welcome message with user's name
- List of available features
- Professional footer

---

### 2. Verification OTP Email (`verification-otp-email.html`)

**Sent when:** User registers or requests new verification OTP
**Variables:** `{{otp}}`
**Subject:** "Verify Your Email Address - OTP Inside"

**Features:**

- Purple gradient header
- Large, prominent OTP display
- 15-minute expiration warning
- Security tips

---

### 3. Password Reset OTP Email (`reset-password-otp-email.html`)

**Sent when:** User requests password reset
**Variables:** `{{otp}}`
**Subject:** "Reset Your Passw Inside"

**Features:**

- Pink/red gradient header
- Large, prominent OTP display
- Security alert styling
- 15-minute expiration warning
- Password strength tips

---

### 4. Password Reset Success Email (`password-reset-success-email.html`)

**Sent when:** User successfully resets password
**Variables:** `{{name}}`
**Subject:** "Password Reset Successful"

**Features:**

- Green gradient header
- Success confirmation
- Security warning if not authorized
- Security tips
- Professional footer

---

## Template Structure

All templates follow a consistent structure:

```html
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Email Title</title>
  </head>
  <body style="inline styles">
    <table role="presentation">
      <!-- Header with gradient -->
      <tr>
        <td>Header Content</td>
      </tr>

      <!-- Main Content -->
      <tr>
        <td>Body Content</td>
      </tr>

      <!-- Footer -->
      <tr>
        <td>Footer Content</td>
      </tr>
    </table>
  </body>
</html>
```

## Design Features

### 1. **Responsive Design**

- 600px width for desktop
- Scales down for mobile devices
- Uses table-based layout for maximum compatibility

### 2. **Inline Styles**

- All CSS is inline for email client compatibility
- No external stylesheets
- No `<style>` tags in head

### 3. **Color Schemes**

| Template         | Primary Color | Gradient               |
| ---------------- | ------------- | ---------------------- |
| Welcome          | Purple        | `#667eea` to `#764ba2` |
| Verification OTP | Purple        | `#667eea` to `#764ba2` |
| Reset OTP        | Pink/Red      | `#f093fb` to `#f5576c` |
| Reset Success    | Green         | `#11998e` to `#38ef7d` |

### 4. **Typography**

- Font Family: `'Segoe UI', Tahoma, Geneva, Verdana, sans-serif`
- Heading Size: 28-32px
- Body Text: 16px
- Small Text: 14px
- Footer Text: 12px

### 5. **Spacing**

- Outer padding: 40px
- Inner padding: 40px
- Section spacing: 20-30px

---

## How to Use

### In EmailService.java

The `EmailService` class automatically loads and processes templates:

```java
@Async
public void sendWelcomeEmail(String toEmail, String name) {
    String subject = "Welcome to Authify! 🎉";
    String htmlContent = loadTemplate("templates/welcome-email.html")
        .replace("{{name}}", name);
    sendHtmlEmail(toEmail, name, subject, htmlContent);
}
```

### Template Variables

Templates use simple `{{variable}}` placeholders:

```html
<p>Hi <strong>{{name}}</strong>,</p>
<p>Your OTP is: {{otp}}</p>
```

Replace in code:

```java
String htmlContent = loadTemplate("templates/email.html")
    .replace("{{name}}", userName)
    .replace("{{otp}}", otpCode);
```

---

## Customization Guide

### 1. Change Colors

Find the gradient in the header:

```html
<td style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);"></td>
```

Replace with your brand colors:

```html
<td
  style="background: linear-gradient(135deg, #YOUR_COLOR_1 0%, #YOUR_COLOR_2 100%);"
></td>
```

### 2. Change Logo/Branding

Add a logo in the header:

```html
<tr>
  <td style="padding: 40px; background: linear-gradient(...);">
    <img
      src="https://your-domain.com/logo.png"
      alt="Your Logo"
      style="width: 150px; height: auto; margin-bottom: 20px;"
    />
    <h1 style="...">Email Title</h1>
  </td>
</tr>
```

### 3. Add Social Media Links

In the footer section:

```html
<tr>
  <td style="padding: 30px 40px; background-color: #f8f9fa;">
    <p style="text-align: center; margin: 0 0 15px 0;">
      <a href="https://twitter.com/yourcompany" style="margin: 0 10px;">
        <img
          src="https://your-cdn.com/twitter-icon.png"
          alt="Twitter"
          style="width: 24px; height: 24px;"
        />
      </a>
      <a href="https://facebook.com/yourcompany" style="margin: 0 10px;">
        <img
          src="https://your-cdn.com/facebook-icon.png"
          alt="Facebook"
          style="width: 24px; height: 24px;"
        />
      </a>
    </p>
    <p style="...">Best regards,<br /><strong>The Authify Team</strong></p>
  </td>
</tr>
```

### 4. Add Company Address

In the footer:

```html
<p
  style="margin: 20px 0 0 0; font-size: 12px; color: #999999; text-align: center;"
>
  Your Company Name<br />
  123 Main Street, Suite 100<br />
  City, State 12345<br />
  <a href="mailto:support@yourcompany.com" style="color: #667eea;"
    >support@yourcompany.com</a
  >
</p>
```

### 5. Add Unsubscribe Link

At the bottom of footer:

```html
<p
  style="margin: 10px 0 0 0; font-size: 11px; color: #999999; text-align: center;"
>
  <a
    href="{{unsubscribe_url}}"
    style="color: #999999; text-decoration: underline;"
  >
    Unsubscribe from these emails
  </a>
</p>
```

---

## Testing Email Templates

### 1. Test in Different Email Clients

Use services like:

- [Litmus](https://litmus.com/) - Paid
- [Email on Acid](https://www.emailonacid.com/) - Paid
- [Mail Tester](https://www.mail-tester.com/) - Free
- [Putsmail](https://putsmail.com/) - Free

### 2. Test Locally

Create a test endpoint:

```java
@GetMapping("/test-email")
public String testEmail() {
    emailService.sendWelcomeEmail("your-email@example.com", "Test User");
    return "Email sent!";
}
```

### 3. Preview in Browser

Open the HTML files directly in a browser to see the design.

---

## Email Client Compatibility

### ✅ Fully Supported

- Gmail (Desktop & Mobile)
- Outlook 2016+
- Apple Mail
- Yahoo Mail
- Thunderbird
- iOS Mail
- Android Mail

### ⚠️ Partial Support

- Outlook 2007-2013 (Limited CSS support)
- Windows Mail (Basic rendering)

### 🔧 Known Issues & Fixes

**Issue:** Gradients not showing in Outlook 2007-2013
**Fix:** Add fallback solid color:

```html
<td style="background-color: #667eea; background: linear-gradient(...);"></td>
```

**Issue:** Large spacing in Outlook
**Fix:** Use `mso-line-height-rule:exactly`:

```html
<p style="margin: 0; line-height: 1.6; mso-line-height-rule: exactly;"></p>
```

---

## Best Practices

### 1. **Keep It Simple**

- Use tables for layout (not divs)
- Inline all CSS
- Avoid JavaScript
- Avoid external resources when possible

### 2. **Optimize Images**

- Host images on CDN
- Use absolute URLs
- Provide alt text
- Compress images

### 3. **Test Thoroughly**

- Test on multiple devices
- Test in different email clients
- Check spam score
- Verify links work

### 4. **Accessibility**

- Use semantic HTML
- Provide alt text for images
- Use sufficient color contrast
- Use readable font sizes

### 5. **Mobile-First**

- Keep width at 600px max
- Use large, tappable buttons
- Use readable font sizes (14px+)
- Test on mobile devices

---

## Adding New Templates

### Step 1: Create HTML File

Create new file in `src/main/resources/templates/`:

```
your-new-email.html
```

### Step 2: Use Existing Template as Base

Copy one of the existing templates and modify:

```html
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Your Email Title</title>
  </head>
  <body style="...">
    <!-- Your content here -->
  </body>
</html>
```

### Step 3: Add Variables

Use `{{variable}}` syntax:

```html
<p>Hello {{name}},</p>
<p>Your code is: {{code}}</p>
```

### Step 4: Add Method to EmailService

```java
@Async
public void sendYourNewEmail(String toEmail, String name, String code) {
    String subject = "Your Email Subject";
    String htmlContent = loadTemplate("templates/your-new-email.html")
        .replace("{{name}}", name)
        .replace("{{code}}", code);
    sendHtmlEmail(toEmail, name, subject, htmlContent);
}
```

### Step 5: Test

```java
emailService.sendYourNewEmail("test@example.com", "John", "ABC123");
```

---

## Troubleshooting

### Template Not Loading

**Error:** `Failed to load email template`

**Solutions:**

1. Check file path: `src/main/resources/templates/filename.html`
2. Verify file is included in build
3. Check file permissions
4. Rebuild project

### Variables Not Replaced

**Issue:** Email shows `{{name}}` instead of actual name

**Solutions:**

1. Check variable name matches exactly
2. Ensure `.replace()` is called
3. Verify variable is not null

### Email Looks Broken

**Issue:** Layout is broken in email client

**Solutions:**

1. Validate HTML structure
2. Check all tables are properly closed
3. Verify inline styles are correct
4. Test in different email clients

### Images Not Showing

**Issue:** Images don't display in email

**Solutions:**

1. Use absolute URLs (not relative)
2. Host images on public server
3. Check image URLs are accessible
4. Provide alt text for accessibility

---

## Resources

### Email Design Tools

- [MJML](https://mjml.io/) - Responsive email framework
- [Foundation for Emails](https://get.foundation/emails.html) - Email framework
- [Cerberus](https://tedgoas.github.io/Cerberus/) - Email patterns

### Testing Tools

- [Litmus](https://litmus.com/)
- [Email on Acid](https://www.emailonacid.com/)
- [Mail Tester](https://www.mail-tester.com/)

### Design Inspiration

- [Really Good Emails](https://reallygoodemails.com/)
- [Milled](https://milled.com/)
- [Email Love](https://emaillove.com/)

### Documentation

- [Campaign Monitor CSS Support](https://www.campaignmonitor.com/css/)
- [Can I Email](https://www.caniemail.com/)
- [Email Client Market Share](https://emailclientmarketshare.com/)
