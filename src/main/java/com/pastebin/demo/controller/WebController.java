package com.pastebin.demo.controller;

import com.pastebin.demo.dto.PasteResponse;
import com.pastebin.demo.service.PasteService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class WebController {

    private final PasteService pasteService;

    public WebController(PasteService pasteService) {
        this.pasteService = pasteService;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/p/{id}")
    public String viewPaste(@PathVariable String id, HttpServletRequest request, Model model) {
        PasteResponse paste = pasteService.getPaste(id, request, true);
        
        if (paste == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Paste not found or unavailable");
        }
        
        model.addAttribute("content", paste.getContent());
        return "view";
    }
}
