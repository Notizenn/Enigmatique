package com.example.controllers;

import com.example.entities.Utilisateur;
import com.example.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/admin/utilisateurs")
public class AdminController {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    // Afficher la liste des utilisateurs
    @GetMapping
    public String listUtilisateurs(Model model) {
        model.addAttribute("utilisateurs", utilisateurRepository.findAll());
        return "admin"; // Page admin.html
    }

    // Ajouter un utilisateur
    @PostMapping("/add")
    public String addUtilisateur(@ModelAttribute Utilisateur utilisateur) {
        utilisateurRepository.save(utilisateur);
        return "redirect:/admin/utilisateurs";
    }

    // Obtenir les détails d'un utilisateur pour modification
    @GetMapping("/edit/{id}")
    @ResponseBody
    public ResponseEntity<Utilisateur> getUtilisateurById(@PathVariable Long id) {
        Optional<Utilisateur> utilisateur = utilisateurRepository.findById(id);
        return utilisateur.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/update/{id}")
    @ResponseBody
    public ResponseEntity<Utilisateur> updateUtilisateur(@PathVariable Long id, @RequestBody Utilisateur utilisateurDetails) {
        return utilisateurRepository.findById(id).map(utilisateur -> {
            utilisateur.setNom(utilisateurDetails.getNom());
            utilisateur.setEmail(utilisateurDetails.getEmail());
            utilisateur.setAdmin(utilisateurDetails.isAdmin());
            utilisateurRepository.save(utilisateur);
            return ResponseEntity.ok(utilisateur);
        }).orElse(ResponseEntity.notFound().build());
    }


    // Supprimer un utilisateur
    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteUtilisateurAjax(@PathVariable Long id) {
        utilisateurRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
