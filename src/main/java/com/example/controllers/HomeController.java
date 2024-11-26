package com.example.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String homePage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() &&
            !"anonymousUser".equals(authentication.getPrincipal())) {
            model.addAttribute("username", authentication.getName());
        }
        return "home"; 
    }


    @GetMapping("/admin-home")
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

    @GetMapping("/game")
    public String showGamePage() {
        return "game"; 
    }

    @GetMapping("/logique")
    public String showLogiquePage() {
        return "logique"; 
    }

    @GetMapping("/maths")
    public String showMathsPage() {
        return "maths"; 
    }

    @GetMapping("/crypto")
    public String showCryptoPage() {
        return "crypto"; 
    }

    @GetMapping("/sous")
    public String showSousPage() {
        return "sous"; 
    }

    @GetMapping("/leaderboard")
    public String showLeaderboardPage() {
        return "leaderboard"; 
    }





}
