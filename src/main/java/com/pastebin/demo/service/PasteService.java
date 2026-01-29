package com.pastebin.demo.service;

import com.pastebin.demo.dto.CreatePasteRequest;
import com.pastebin.demo.dto.CreatePasteResponse;
import com.pastebin.demo.dto.PasteResponse;
import com.pastebin.demo.model.Paste;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class PasteService {

    private final RedisTemplate<String, Object> redisTemplate;

    public PasteService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean checkHealth() {
        try {
            redisTemplate.opsForValue().set("health_check", "ok");
            return "ok".equals(redisTemplate.opsForValue().get("health_check"));
        } catch (Exception e) {
            return false;
        }
    }

    public CreatePasteResponse createPaste(CreatePasteRequest request, HttpServletRequest httpRequest) {
        String id = UUID.randomUUID().toString().substring(0, 8);
        
        long currentTime = getCurrentTime(httpRequest);
        
        Paste paste = new Paste();
        paste.setId(id);
        paste.setContent(request.getContent());
        paste.setCreatedAt(currentTime);
        paste.setViews(0);
        
        if (request.getTtl_seconds() != null) {
            paste.setExpiresAt(currentTime + (request.getTtl_seconds() * 1000L));
        }
        
        if (request.getMax_views() != null) {
            paste.setMaxViews(request.getMax_views());
        }
        
        // Save to Redis
        redisTemplate.opsForValue().set("paste:" + id, paste);
        
        // Set TTL on Redis key if specified
        if (request.getTtl_seconds() != null) {
            redisTemplate.expire("paste:" + id, request.getTtl_seconds(), TimeUnit.SECONDS);
        }
        
        // Build dynamic URL from request
        String scheme = httpRequest.getScheme();
        String serverName = httpRequest.getServerName();
        int serverPort = httpRequest.getServerPort();
        String contextPath = httpRequest.getContextPath();
        
        String url;
        if ((scheme.equals("http") && serverPort == 80) || (scheme.equals("https") && serverPort == 443)) {
            url = scheme + "://" + serverName + contextPath + "/p/" + id;
        } else {
            url = scheme + "://" + serverName + ":" + serverPort + contextPath + "/p/" + id;
        }
        
        CreatePasteResponse response = new CreatePasteResponse();
        response.setId(id);
        response.setUrl(url);
        
        return response;
    }

    public PasteResponse getPaste(String id, HttpServletRequest request, boolean incrementView) {
        String key = "paste:" + id;
        Paste paste = (Paste) redisTemplate.opsForValue().get(key);
        
        if (paste == null) {
            return null;
        }
        
        long currentTime = getCurrentTime(request);
        
        // Check if expired
        if (paste.getExpiresAt() != null && currentTime >= paste.getExpiresAt()) {
            redisTemplate.delete(key);
            return null;
        }
        
        // Check if view limit exceeded BEFORE incrementing
        if (paste.getMaxViews() != null && paste.getViews() >= paste.getMaxViews()) {
            redisTemplate.delete(key);
            return null;
        }
        
        // Increment view count if requested
        if (incrementView && paste.getMaxViews() != null) {
            paste.setViews(paste.getViews() + 1);
            
            // Check if this was the last view
            if (paste.getViews() >= paste.getMaxViews()) {
                // Delete the paste after serving it
                redisTemplate.delete(key);
            } else {
                // Update the paste in Redis with new view count
                redisTemplate.opsForValue().set(key, paste);
                // Preserve TTL if it exists
                if (paste.getExpiresAt() != null) {
                    long ttlSeconds = (paste.getExpiresAt() - currentTime) / 1000;
                    if (ttlSeconds > 0) {
                        redisTemplate.expire(key, ttlSeconds, TimeUnit.SECONDS);
                    }
                }
            }
        }
        
        PasteResponse response = new PasteResponse();
        response.setContent(paste.getContent());
        
        if (paste.getMaxViews() != null) {
            int remaining = paste.getMaxViews() - paste.getViews();
            response.setRemaining_views(Math.max(0, remaining));
        }
        
        if (paste.getExpiresAt() != null) {
            response.setExpires_at(Instant.ofEpochMilli(paste.getExpiresAt()).toString());
        }
        
        return response;
    }

    private long getCurrentTime(HttpServletRequest request) {
        String testMode = System.getenv("TEST_MODE");
        
        if ("1".equals(testMode)) {
            String testNowHeader = request.getHeader("x-test-now-ms");
            if (testNowHeader != null) {
                try {
                    return Long.parseLong(testNowHeader);
                } catch (NumberFormatException e) {
                    // Fall through to system time
                }
            }
        }
        
        return System.currentTimeMillis();
    }
}
