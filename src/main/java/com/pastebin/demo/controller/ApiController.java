package com.pastebin.demo.controller;

import com.pastebin.demo.dto.CreatePasteRequest;
import com.pastebin.demo.dto.CreatePasteResponse;
import com.pastebin.demo.dto.PasteResponse;
import com.pastebin.demo.model.Paste;
import com.pastebin.demo.service.PasteService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final PasteService pasteService;

    public ApiController(PasteService pasteService) {
        this.pasteService = pasteService;
    }

    @GetMapping("/healthz")
    public ResponseEntity<Map<String, Boolean>> healthCheck() {
        boolean isHealthy = pasteService.checkHealth();
        Map<String, Boolean> response = new HashMap<>();
        response.put("ok", isHealthy);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/pastes")
    public ResponseEntity<?> createPaste(@RequestBody CreatePasteRequest request, HttpServletRequest httpRequest) {
        // Validate input
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "content is required and must be non-empty"));
        }
        
        if (request.getTtl_seconds() != null && request.getTtl_seconds() < 1) {
            return ResponseEntity.badRequest().body(Map.of("error", "ttl_seconds must be >= 1"));
        }
        
        if (request.getMax_views() != null && request.getMax_views() < 1) {
            return ResponseEntity.badRequest().body(Map.of("error", "max_views must be >= 1"));
        }

        CreatePasteResponse response = pasteService.createPaste(request, httpRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pastes/{id}")
    public ResponseEntity<?> getPaste(@PathVariable String id, HttpServletRequest request) {
        PasteResponse response = pasteService.getPaste(id, request, true);
        
        if (response == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Paste not found or unavailable"));
        }
        
        return ResponseEntity.ok(response);
    }
}
