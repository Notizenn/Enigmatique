package com.example.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/admin")
    public String showAdminPage() {
        return "admin"; 
    }

    @GetMapping("/article1")
    public String showArticle1Page() {
        return "article1"; 
    }

    @GetMapping("/article2")
    public String showArticle2Page() {
        return "article2"; 
    }

    @GetMapping("/article3")
    public String showArticle3Page() {
        return "article3"; 
    }
}
