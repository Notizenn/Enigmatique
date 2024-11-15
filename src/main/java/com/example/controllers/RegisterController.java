package com.example.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.entities.Utilisateur;
import com.example.repository.UtilisateurRepository;

import org.springframework.ui.Model;



@Controller
public class RegisterController {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";  
    }


    @PostMapping("/register")
    public String registerUser(@RequestParam String nom ,
                               @RequestParam String email, 
                               @RequestParam String motDePasse, 
                               @RequestParam String confirmMotDePasse, 
                               Model model) {

     
        if (!motDePasse.equals(confirmMotDePasse)) {
            model.addAttribute("error", "Les mots de passe ne correspondent pas.");
            return "register";
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(motDePasse);


        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(motDePasse);
        utilisateur.setEmail(email);
        utilisateur.setMotDePasse(hashedPassword); 

      
        utilisateurRepository.save(utilisateur);

        return "redirect:/home";
    }
}