# Railway Quick Start (5 Minutes)

## 1. Push to GitHub

```bash
git init
git add .
git commit -m "Ready for Railway deployment"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/authify.git
git push -u origin main
```

## 2. Deploy on Railway

1. Go to **https://railway.app** and sign in with GitHub
2. Click **"New Project"**
3. Select **"Deploy from GitHub repo"**
4. Choose your **authify** repository
5. Railway will start building automatically

## 3. Add MySQL Database

1. In your project, click **"New"** → **"Database"** → **"Add MySQL"**
2. Wait for it to provision (30 seconds)

## 4. Configure Environment Variables

Click your Spring Boot service → **"Variables"** tab → Add these:

```
SPRING_PROFILES_ACTIVE=prod
DB_URL=jdbc:mysql://${{MySQL.MYSQL_HOST}}:${{MySQL.MYSQL_PORT}}/${{MySQL.MYSQL_DATABASE}}
DB_USERNAME=${{MySQL.MYSQL_USER}}
DB_PASSWORD=${{MySQL.MYSQL_PASSWORD}}
JWT_SECRET_KEY=PASTE_YOUR_GENERATED_SECRET_HERE
JWT_EXPIRATION_MS=3600000
SMTP_HOST=smtp-relay.brevo.com
SMTP_PORT=587
SMTP_USERNAME=your_brevo_username
SMTP_PASSWORD=your_brevo_password
SMTP_FROM=noreply@yourdomain.com
```

**Generate JWT Secret in PowerShell:**
```powershell
-join ((65..90) + (97..122) + (48..57) | Get-Random -Count 64 | ForEach-Object {[char]$_})
```

## 5. First Deploy - Allow Schema Creation

Add one more variable temporarily:
```
spring.jpa.hibernate.ddl-auto=update
```

After first successful deployment, change it to:
```
spring.jpa.hibernate.ddl-auto=validate
```

## 6. Get Your URL

Railway will provide a URL like:
```
https://authify-production-xxxx.up.railway.app
```

Test it:
```
https://authify-production-xxxx.up.railway.app/api/v1.0/is-authenticated
```

## Done! 🎉

Your app is now live on the internet with:
- ✅ Automatic HTTPS
- ✅ MySQL database
- ✅ Auto-deploy on git push
- ✅ Free tier (500 hours/month)

## Troubleshooting

**Build fails?**
- Check logs in Railway dashboard
- Verify `pom.xml` is correct

**App crashes?**
- Check environment variables are set
- View logs in Railway dashboard

**Database connection error?**
- Verify MySQL service is running
- Check DB_URL format is correct
