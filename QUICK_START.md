# ⚡ QUICK START - DEPLOY YOUR JAVA PASTEBIN

## ✅ WHAT WAS FIXED:

1. **View Counting Bug** - Now works correctly (deletes after max views)
2. **Hardcoded localhost URL** - Now generates dynamic URLs
3. **Paste Not Deleted** - Now deletes expired/exhausted pastes

---

## 🚀 DEPLOY IN 20 MINUTES:

### STEP 1: Update GitHub (5 min)
```bash
cd pastebin-java-fixed
git init
git add .
git commit -m "Fix bugs and add deployment config"
git remote add origin https://github.com/vasanth-sakthivel-hub/Pastebin.git
git push -u origin main --force
```

### STEP 2: Sign Up Render (2 min)
- Go to: https://render.com
- Sign up with GitHub
- Authorize Render

### STEP 3: Create Redis (3 min)
- New + → Redis
- Name: `pastebin-redis`
- Plan: Free
- Create

### STEP 4: Deploy App (10 min)
- New + → Web Service
- Select: vasanth-sakthivel-hub/Pastebin
- Name: pastebin-lite
- Runtime: Java
- Build: `mvn clean package -DskipTests`
- Start: `java -jar target/demo-0.0.1-SNAPSHOT.jar`
- Add Environment Variables:
  ```
  PORT = 8080
  REDIS_HOST = [From Service → pastebin-redis → hostname]
  REDIS_PORT = [From Service → pastebin-redis → port]
  REDIS_PASSWORD = [From Service → pastebin-redis → password]
  REDIS_SSL = true
  TEST_MODE = 1
  ```
- Plan: Free
- Create Web Service

**Wait 5-8 minutes for deployment...**

### STEP 5: Test (2 min)
```bash
# Your URL
URL="https://pastebin-lite-xxxx.onrender.com"

# Test health
curl $URL/api/healthz

# Should return: {"ok":true}
```

### STEP 6: Submit
```
Deployed URL: https://pastebin-lite-xxxx.onrender.com
GitHub: https://github.com/vasanth-sakthivel-hub/Pastebin
Notes: See DEPLOYMENT_GUIDE.md
```

---

## 📁 FILES IN THIS PACKAGE:

- `src/` - Fixed Java source code
- `DEPLOYMENT_GUIDE.md` - Complete deployment instructions
- `README.md` - Updated project documentation
- `render.yaml` - Render deployment config
- `build.sh` - Build script

---

## 🎯 READ DEPLOYMENT_GUIDE.md FOR DETAILS!

It has screenshots and detailed explanations.

**Time: 20 minutes | Platform: Render.com (Free)**
