package com.example.controllers;

import com.example.entities.Utilisateur;
import com.example.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegisterController {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;  // Utilisation de PasswordEncoder

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

        // Vérifier si les mots de passe correspondent
        if (!motDePasse.equals(confirmMotDePasse)) {
            model.addAttribute("error", "Les mots de passe ne correspondent pas.");
            return "register";
        }

        // Hacher le mot de passe avec le PasswordEncoder configuré
        String hashedPassword = passwordEncoder.encode(motDePasse);

        // Créer l'utilisateur avec 1000 sous par défaut
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(nom);
        utilisateur.setEmail(email);
        utilisateur.setMotDePasse(hashedPassword);
        utilisateur.setSous(1000);

        utilisateurRepository.save(utilisateur);

        return "redirect:/home";
    }
}
