# 🚀 DEPLOYMENT GUIDE - JAVA SPRING BOOT PASTEBIN

## ⚠️ IMPORTANT: Your Java App CANNOT Deploy to Vercel!

Vercel only supports Node.js/Next.js. For Java Spring Boot, we'll use **Render.com** (free tier available with Redis support).

---

## 🔧 CRITICAL BUGS FIXED:

### ✅ Bug #1: View Counting - FIXED
**Problem:** Used separate Redis counter (`paste:id:views`) which didn't sync with paste object
**Fix:** Now stores view count inside paste object and updates atomically

### ✅ Bug #2: Hardcoded localhost URL - FIXED  
**Problem:** Returned `http://localhost:8080/p/xxx`
**Fix:** Now builds URL dynamically from request headers

### ✅ Bug #3: Pastes Not Deleted - FIXED
**Problem:** Expired/exhausted pastes stayed in Redis forever
**Fix:** Now deletes paste when it becomes unavailable

---

## 📋 DEPLOYMENT OPTIONS FOR JAVA

Since Vercel doesn't support Java, here are your options:

### Option 1: Render.com ⭐ RECOMMENDED (Free)
- ✅ Free tier available
- ✅ Supports Java + Redis
- ✅ Easy deployment
- ✅ Similar to Vercel

### Option 2: Railway.app (Free trial)
- ✅ $5 free credit
- ✅ Supports Java + Redis
- ✅ Easy setup

### Option 3: Heroku (Paid)
- ❌ No longer has free tier
- ✅ Reliable
- ✅ Good documentation

**We'll use Render.com in this guide!**

---

## 🚀 DEPLOYMENT TO RENDER.COM (20 minutes)

### STEP 1: Push Fixed Code to GitHub (5 minutes)

1. **Go to your existing repository:**
   ```
   https://github.com/vasanth-sakthivel-hub/Pastebin
   ```

2. **Update your repository with fixed code:**

   **Option A: Using Git (Recommended)**
   ```bash
   # Navigate to your fixed project folder
   cd path/to/pastebin-java-fixed
   
   # Initialize git (if not already)
   git init
   
   # Add all files
   git add .
   
   # Commit
   git commit -m "Fix view counting and URL generation bugs"
   
   # Connect to your existing repo
   git remote add origin https://github.com/vasanth-sakthivel-hub/Pastebin.git
   
   # Force push to update
   git branch -M main
   git push -u origin main --force
   ```

   **Option B: Manual Upload via GitHub**
   1. Go to: https://github.com/vasanth-sakthivel-hub/Pastebin
   2. Delete all files
   3. Upload all files from `pastebin-java-fixed` folder
   4. Commit changes

### STEP 2: Sign Up for Render (2 minutes)

1. Go to: **https://render.com**
2. Click **"Get Started for Free"**
3. Sign up with **GitHub** (recommended)
4. Authorize Render to access your repositories

### STEP 3: Create Redis Database (3 minutes)

1. On Render dashboard, click **"New +"** (top right)
2. Select **"Redis"**
3. Fill in:
   - **Name:** `pastebin-redis`
   - **Plan:** **Free** (select the $0/month plan)
   - **Region:** Choose closest to you
4. Click **"Create Redis"**
5. ⏳ Wait ~1 minute for creation
6. ✅ Redis is ready!

### STEP 4: Deploy Web Service (10 minutes)

1. Click **"New +"** → **"Web Service"**

2. **Connect Repository:**
   - Find and select: `vasanth-sakthivel-hub/Pastebin`
   - Click **"Connect"**

3. **Configure Service:**
   ```
   Name: pastebin-lite
   Region: Same as your Redis
   Branch: main
   Runtime: Java
   Build Command: mvn clean package -DskipTests
   Start Command: java -jar target/demo-0.0.1-SNAPSHOT.jar
   ```

4. **Set Instance Type:**
   - **Plan:** Free (select the $0/month plan)

5. **Add Environment Variables:**
   Click "Advanced" → "Add Environment Variable"
   
   Add these EXACTLY:
   
   ```
   PORT = 8080
   
   REDIS_HOST = [Click "From Service" → Select "pastebin-redis" → "hostname"]
   
   REDIS_PORT = [Click "From Service" → Select "pastebin-redis" → "port"]
   
   REDIS_PASSWORD = [Click "From Service" → Select "pastebin-redis" → "password"]
   
   REDIS_SSL = true
   
   TEST_MODE = 1
   ```

6. Click **"Create Web Service"**

7. ⏳ **Wait 5-8 minutes** for deployment
   - You'll see build logs
   - First build takes longer
   - Don't close the page!

8. ✅ **Deployment Success!**
   - You'll see: "Live" with a green dot
   - Your URL: `https://pastebin-lite-xxxx.onrender.com`

---

## 🧪 TESTING YOUR DEPLOYMENT (5 minutes)

### Test 1: Health Check
```bash
curl https://pastebin-lite-xxxx.onrender.com/api/healthz
```
**Expected:** `{"ok":true}`

### Test 2: Create Paste
```bash
curl -X POST https://pastebin-lite-xxxx.onrender.com/api/pastes \
  -H "Content-Type: application/json" \
  -d '{"content":"Test from deployment","max_views":2}'
```
**Expected:** Returns ID and URL

### Test 3: View Counting
```bash
# Use the ID from above
curl https://pastebin-lite-xxxx.onrender.com/api/pastes/YOUR_ID

# First view: remaining_views = 1
curl https://pastebin-lite-xxxx.onrender.com/api/pastes/YOUR_ID

# Second view: remaining_views = 0  
curl https://pastebin-lite-xxxx.onrender.com/api/pastes/YOUR_ID

# Third view: Should return 404
```

### Test 4: UI Test
1. Open: `https://pastebin-lite-xxxx.onrender.com`
2. Create a paste
3. Click the generated URL
4. Verify content displays

---

## 📝 SUBMISSION

### What to Submit:

**1. Deployed URL:**
```
https://pastebin-lite-xxxx.onrender.com
```

**2. GitHub Repository:**
```
https://github.com/vasanth-sakthivel-hub/Pastebin
```

**3. Notes:**
```
Project: Pastebin Lite
Technology: Java Spring Boot 3.2.0 + Redis

Persistence Layer: Redis (hosted on Render)
- Pastes stored as serialized objects in Redis
- Automatic TTL expiration via Redis
- View count tracking with atomic operations
- Data persists across application restarts

Deployment Platform: Render.com
- Free tier web service for Spring Boot application
- Managed Redis instance for data persistence  
- Supports TEST_MODE for deterministic time testing

Design Decisions:
1. Fixed view counting to use single Redis key with atomic updates
2. Dynamic URL generation from request headers (no hardcoded values)
3. Automatic paste deletion when expired or view limit reached
4. Combined TTL and max_views constraints with proper precedence
5. XSS protection through Thymeleaf template engine
6. Support for deterministic time testing via x-test-now-ms header

All required API endpoints implemented:
- GET /api/healthz - Health check with Redis connection test
- POST /api/pastes - Create paste with validation
- GET /api/pastes/:id - Fetch paste (counts as view)
- GET /p/:id - HTML view of paste

The application handles all edge cases including view limits, TTL expiration,
combined constraints, and proper error responses (400/404).
```

---

## 🆘 TROUBLESHOOTING

### Problem: Build fails on Render
**Solution:**
- Check build logs for errors
- Ensure `pom.xml` is in repository root
- Verify Java version is 17 in `pom.xml`

### Problem: App crashes on startup
**Solution:**
- Check logs: Render Dashboard → Your Service → Logs
- Usually Redis connection issue
- Verify all Redis environment variables are set correctly
- Make sure Redis service is running

### Problem: Health check returns {"ok":false}
**Solution:**
- Redis not connected
- Check environment variables:
  - REDIS_HOST
  - REDIS_PORT  
  - REDIS_PASSWORD
  - REDIS_SSL=true
- Redeploy service

### Problem: Can't create pastes (500 error)
**Solution:**
- Check application logs in Render
- Usually serialization or Redis issue
- Verify Paste model is Serializable

### Problem: App is slow on Render free tier
**Solution:**
- Render free tier spins down after 15 min of inactivity
- First request takes 30-60 seconds to wake up
- This is normal for free tier
- Subsequent requests are fast

---

## ⚙️ FILE CHANGES MADE

### PasteService.java
**Changes:**
1. ✅ Fixed view counting - now stores in paste object
2. ✅ Deletes paste after last view
3. ✅ Deletes expired pastes
4. ✅ Dynamic URL generation (no hardcoded localhost)
5. ✅ Preserves TTL when updating views

### application.properties
**Already had:**
- Environment variable support for Redis
- SSL support
- Configurable port

### New Files Added:
- `render.yaml` - Render deployment configuration
- `build.sh` - Build script

---

## ⚡ ALTERNATIVE: If Render Doesn't Work

### Use Railway.app:

1. Go to: **https://railway.app**
2. Sign up with GitHub
3. Click "New Project"
4. Select "Deploy from GitHub repo"
5. Choose your repository
6. Add Redis: "+ New" → "Database" → "Redis"
7. Configure environment variables (same as Render)
8. Deploy!

Your URL: `https://pastebin-lite.up.railway.app`

---

## ✅ VERIFICATION CHECKLIST

Before submitting:

- [ ] Code pushed to GitHub ✓
- [ ] Deployed on Render (or Railway) ✓
- [ ] Redis connected ✓
- [ ] `/api/healthz` returns `{"ok":true}` ✓
- [ ] Can create paste via API ✓
- [ ] Can view paste in browser ✓
- [ ] View counting works (paste disappears after max views) ✓
- [ ] TTL expiration works ✓
- [ ] No hardcoded localhost in code ✓
- [ ] README updated ✓

---

## 🎯 IMPORTANT NOTES

1. **Render Free Tier Limitations:**
   - Spins down after 15 min inactivity
   - First request after spin-down takes 30-60 seconds
   - This is normal - mention it if asked

2. **Why Not Vercel:**
   - Vercel ONLY supports Node.js/Python/Go
   - Java Spring Boot needs a traditional server
   - Render/Railway are the best free alternatives

3. **README Update:**
   Make sure your README mentions:
   - Deployed platform (Render)
   - Redis persistence
   - Fixed bugs from original code

---

## 📞 NEED HELP?

If deployment fails:
1. Check Render logs (very detailed)
2. Verify all environment variables
3. Make sure Redis service is running
4. Check that code is pushed to GitHub

**Time Estimate:** 20-30 minutes total

**Good luck with deployment!** 🚀
