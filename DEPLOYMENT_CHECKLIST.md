# Railway Deployment Checklist

## ✅ Pre-Deployment (Done)

- [x] `system.properties` created (Java 21)
- [x] `Procfile` created
- [x] `.gitignore` configured (excludes .env files)
- [x] Cookie security auto-detects production
- [x] Production config files ready

## 📋 Your Action Items

### 1. Generate JWT Secret Key

Run in PowerShell:

```powershell
-join ((65..90) + (97..122) + (48..57) | Get-Random -Count 64 | ForEach-Object {[char]$_})
```

Save the output - you'll need it for Railway.

### 2. Get Your SMTP Credentials

You need from Brevo (smtp-relay.brevo.com):

- SMTP Username
- SMTP Password
- Verified sender email

### 3. Push to GitHub

```bash
git init
git add .
git commit -m "Ready for Railway deployment"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/authify.git
git push -u origin main
```

### 4. Deploy on Railway

Follow the steps in `RAILWAY_QUICK_START.md`

## 🔍 Post-Deployment Testing

Once deployed, test these endpoints:

```bash
# Replace YOUR_URL with your Railway URL

# 1. Health check
curl https://YOUR_URL/api/v1.0/is-authenticated

# 2. Register (if you have this endpoint)
curl -X POST https://YOUR_URL/api/v1.0/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"Test123!"}'

# 3. Login
curl -X POST https://YOUR_URL/api/v1.0/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"Test123!"}'
```

## 📊 Monitoring

- **Logs**: Railway Dashboard → Your Service → Deployments
- **Metrics**: Railway Dashboard → Your Service → Metrics
- **Database**: Railway Dashboard → MySQL Service

## 🚨 Common Issues

| Issue                     | Solution                                     |
| ------------------------- | -------------------------------------------- |
| Build fails               | Check logs, verify pom.xml                   |
| App crashes on start      | Verify all environment variables are set     |
| Database connection error | Check DB_URL format and MySQL service status |
| 502 Bad Gateway           | App is still starting, wait 1-2 minutes      |
| SMTP errors               | Verify Brevo credentials                     |

## 💰 Cost Estimate

**Free Tier:**

- $5 credit/month
- Usually enough for small apps
- ~500 hours of runtime

**If you exceed free tier:**

- $5/month for Hobby plan
- Includes MySQL database

## 🎯 Next Steps After Deployment

1. Test all API endpoints
2. Connect your frontend
3. Configure CORS if needed
4. Set up custom domain (optional)
5. Monitor logs for errors
6. Set up alerts (optional)

## 📚 Documentation

- Full guide: `RAILWAY_DEPLOYMENT.md`
- Quick start: `RAILWAY_QUICK_START.md`
- Railway docs: https://docs.railway.app
