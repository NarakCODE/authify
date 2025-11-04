# Railway Deployment Guide

## Prerequisites
- GitHub account
- Railway account (sign up at railway.app)
- Your code pushed to GitHub

## Step-by-Step Deployment

### 1. Push Your Code to GitHub

```bash
git init
git add .
git commit -m "Initial commit"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/authify.git
git push -u origin main
```

### 2. Create Railway Project

1. Go to https://railway.app
2. Click "Start a New Project"
3. Select "Deploy from GitHub repo"
4. Authorize Railway to access your GitHub
5. Select your `authify` repository

### 3. Add MySQL Database

1. In your Railway project, click "New"
2. Select "Database" → "Add MySQL"
3. Railway will automatically create a MySQL database

### 4. Link Database to Your App

Railway automatically creates these environment variables:
- `MYSQL_URL`
- `MYSQL_USER`
- `MYSQL_PASSWORD`
- `MYSQL_DATABASE`
- `MYSQL_HOST`
- `MYSQL_PORT`

### 5. Configure Environment Variables

Click on your Spring Boot service → "Variables" tab → Add these:

```
SPRING_PROFILES_ACTIVE=prod
DB_URL=jdbc:mysql://${{MYSQL_HOST}}:${{MYSQL_PORT}}/${{MYSQL_DATABASE}}
DB_USERNAME=${{MYSQL_USER}}
DB_PASSWORD=${{MYSQL_PASSWORD}}
JWT_SECRET_KEY=YOUR_SECURE_256_BIT_SECRET_KEY_HERE
JWT_EXPIRATION_MS=3600000
SMTP_HOST=smtp-relay.brevo.com
SMTP_PORT=587
SMTP_USERNAME=your_smtp_username
SMTP_PASSWORD=your_smtp_password
SMTP_FROM=noreply@yourdomain.com
```

**Note:** Railway uses `${{VARIABLE}}` syntax to reference other variables.

### 6. Generate JWT Secret Key

Run this in PowerShell to generate a secure key:

```powershell
-join ((65..90) + (97..122) + (48..57) | Get-Random -Count 64 | ForEach-Object {[char]$_})
```

Copy the output and use it as `JWT_SECRET_KEY`.

### 7. Deploy

Railway will automatically:
1. Detect it's a Maven project
2. Run `mvn clean package`
3. Start your application
4. Provide a public URL

### 8. Access Your Application

Railway will give you a URL like:
```
https://authify-production-xxxx.up.railway.app
```

Your API will be at:
```
https://authify-production-xxxx.up.railway.app/api/v1.0/
```

## Important Configuration Changes

### Update Cookie Security

Since Railway provides HTTPS, update `AuthController.java`:

Change `.secure(false)` to `.secure(true)` on lines 50 and 116.

### Update Database Schema

Railway MySQL starts empty. You have two options:

**Option 1: Let Hibernate Create Tables (First Deploy)**
In Railway variables, temporarily set:
```
spring.jpa.hibernate.ddl-auto=update
```

After first successful run, change it back to:
```
spring.jpa.hibernate.ddl-auto=validate
```

**Option 2: Manual Schema Creation**
1. Connect to Railway MySQL using the provided credentials
2. Run your SQL schema manually

## Monitoring

- **Logs**: Click on your service → "Deployments" → View logs
- **Metrics**: Railway shows CPU, Memory, Network usage
- **Database**: Click MySQL service to see connection info

## Custom Domain (Optional)

1. Go to your service settings
2. Click "Settings" → "Domains"
3. Add your custom domain
4. Update DNS records as instructed

## Costs

- **Free Tier**: $5 credit/month (usually enough for small apps)
- **Paid**: $5/month for more resources
- **Database**: Included in your plan

## Troubleshooting

### Build Fails
- Check logs in Railway dashboard
- Ensure `pom.xml` is correct
- Verify Java version in `system.properties`

### App Crashes on Start
- Check environment variables are set correctly
- Verify database connection
- Check logs for errors

### Database Connection Issues
- Ensure `DB_URL` format is correct
- Verify MySQL service is running
- Check that variables reference MySQL correctly

## Useful Commands

### View Logs
```bash
# Install Railway CLI
npm i -g @railway/cli

# Login
railway login

# Link to project
railway link

# View logs
railway logs
```

## Security Checklist

- ✅ Use strong JWT secret key
- ✅ Set `secure=true` for cookies (HTTPS)
- ✅ Use environment variables for all secrets
- ✅ Set `spring.jpa.hibernate.ddl-auto=validate` in production
- ✅ Enable CORS only for your frontend domain
- ✅ Keep Railway variables private

## Next Steps

1. Test all endpoints
2. Set up your frontend to use the Railway URL
3. Configure CORS if needed
4. Set up monitoring/alerts
5. Consider adding a custom domain
