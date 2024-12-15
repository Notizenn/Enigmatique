package com.example.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.entities.Utilisateur;
import com.example.repository.UtilisateurRepository;

@Controller
public class RegisterController {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String nom,
                               @RequestParam String email,
                               @RequestParam String motDePasse,
                               @RequestParam String confirmMotDePasse,
                               Model model) {
    
        if (!motDePasse.equals(confirmMotDePasse)) {
            model.addAttribute("error", "Les mots de passe ne correspondent pas.");
            return "register";
        }
    
        // Vérifier si l'email existe déjà
        if (utilisateurRepository.findByEmail(email).isPresent()) {
            model.addAttribute("error", "Cet email est déjà utilisé.");
            return "register";
        }
    
        // Hachage du mot de passe et création de l'utilisateur
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(motDePasse);
    
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(nom);
        utilisateur.setEmail(email);
        utilisateur.setMotDePasse(hashedPassword);
        
        utilisateurRepository.save(utilisateur);
    
    
        return "redirect:/login";
    }
    
    
}
