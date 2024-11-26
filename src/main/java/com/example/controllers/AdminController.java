package com.example.controllers;

import com.example.entities.*;
import com.example.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private StatistiqueRepository statistiqueRepository;

    @Autowired
    private IndiceRepository indiceRepository;

    @Autowired
    private EnigmeRepository enigmeRepository;

    @Autowired
    private CategorieRepository categorieRepository;

    // *** Page d'administration générale ***
    @GetMapping
    public String adminPage(Model model) {
        model.addAttribute("utilisateurs", utilisateurRepository.findAll());
        model.addAttribute("statistiques", statistiqueRepository.findAll());
        model.addAttribute("indices", indiceRepository.findAll());
        model.addAttribute("enigmes", enigmeRepository.findAll());
        model.addAttribute("categories", categorieRepository.findAll());
        return "admin"; // Assurez-vous que le fichier est nommé admin.html et est dans le bon dossier
    }

    @PostMapping("/utilisateurs/add")
    public String addUtilisateur(@ModelAttribute Utilisateur utilisateur, Model model) {
        // Vérification de l'unicité de l'email
        if (utilisateurRepository.findByEmail(utilisateur.getEmail()).isPresent()) {
            model.addAttribute("error", "L'email existe déjà. Veuillez en choisir un autre.");
            model.addAttribute("utilisateurs", utilisateurRepository.findAll());
            model.addAttribute("statistiques", statistiqueRepository.findAll());
            model.addAttribute("indices", indiceRepository.findAll());
            model.addAttribute("enigmes", enigmeRepository.findAll());
            model.addAttribute("categories", categorieRepository.findAll());
            return "admin"; // Retourne à la page admin avec un message d'erreur
        }

        // Encode le mot de passe avant de sauvegarder
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        utilisateur.setMotDePasse(passwordEncoder.encode(utilisateur.getMotDePasse()));

        // Sauvegarder l'utilisateur
        utilisateurRepository.save(utilisateur);
        return "redirect:/admin";
    }

    @PutMapping("/utilisateurs/update/{id}")
    @ResponseBody
    public ResponseEntity<Utilisateur> updateUtilisateur(@PathVariable Long id, @RequestBody Utilisateur utilisateurDetails) {
        Optional<Utilisateur> utilisateurOptional = utilisateurRepository.findById(id);
        if (utilisateurOptional.isPresent()) {
            Utilisateur utilisateur = utilisateurOptional.get();
            utilisateur.setNom(utilisateurDetails.getNom());
            utilisateur.setEmail(utilisateurDetails.getEmail());
            utilisateur.setAdmin(utilisateurDetails.isAdmin());
            utilisateurRepository.save(utilisateur);
            return ResponseEntity.ok(utilisateur);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/utilisateurs/delete/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteUtilisateur(@PathVariable Long id) {
        if (utilisateurRepository.existsById(id)) {
            utilisateurRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/indices/add")
    public String addIndice(@ModelAttribute Indice indice, @RequestParam("enigmeId") Long enigmeId) {
        Optional<Enigme> enigmeOptional = enigmeRepository.findById(enigmeId);
        if (enigmeOptional.isPresent()) {
            Enigme enigme = enigmeOptional.get();
            indice.setEnigme(enigme); // Lier l'indice à l'énigme
            indiceRepository.save(indice);
        }
        return "redirect:/admin";
    }

    @PutMapping("/indices/update/{id}")
    @ResponseBody
    public ResponseEntity<Indice> updateIndice(@PathVariable Long id, @RequestBody Indice indiceDetails) {
        Optional<Indice> indiceOptional = indiceRepository.findById(id);
        if (indiceOptional.isPresent()) {
            Indice indice = indiceOptional.get();
            indice.setDescription(indiceDetails.getDescription());
            indice.setCout(indiceDetails.getCout());
            indiceRepository.save(indice);
            return ResponseEntity.ok(indice);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/indices/delete/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteIndice(@PathVariable Long id) {
        if (indiceRepository.existsById(id)) {
            indiceRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // *** Gestion des Énigmes ***
    @PostMapping("/enigmes/add")
    public String addEnigme(@ModelAttribute Enigme enigme, @RequestParam(value = "categoriesIds", required = false) List<Long> categoriesIds) {
        if (enigme.getIndices() == null) {
            enigme.setIndices(new ArrayList<>());
        }
        if (categoriesIds != null && !categoriesIds.isEmpty()) {
            List<Categorie> categories = categorieRepository.findAllById(categoriesIds);
            enigme.setCategories(categories);
        } else {
            enigme.setCategories(new ArrayList<>());
        }
        enigmeRepository.save(enigme);
        return "redirect:/admin";
    }

    @PutMapping("/enigmes/update/{id}")
    @ResponseBody
    public ResponseEntity<Enigme> updateEnigme(@PathVariable Long id, @RequestBody Enigme enigmeDetails) {
        Optional<Enigme> enigmeOptional = enigmeRepository.findById(id);
        if (enigmeOptional.isPresent()) {
            Enigme enigme = enigmeOptional.get();
            enigme.setTitre(enigmeDetails.getTitre());
            enigme.setDescription(enigmeDetails.getDescription());
            enigme.setReponse(enigmeDetails.getReponse());
            enigme.setNiveau(enigmeDetails.getNiveau());
            enigme.setCategories(enigmeDetails.getCategories());
            enigmeRepository.save(enigme);
            return ResponseEntity.ok(enigme);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/enigmes/delete/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteEnigme(@PathVariable Long id) {
        if (enigmeRepository.existsById(id)) {
            enigmeRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // *** Gestion des Catégories ***
    @PostMapping("/categories/add")
    public String addCategorie(@ModelAttribute Categorie categorie) {
        categorieRepository.save(categorie);
        return "redirect:/admin";
    }

    @PutMapping("/categories/update/{id}")
    @ResponseBody
    public ResponseEntity<Categorie> updateCategorie(@PathVariable Long id, @RequestBody Categorie categorieDetails) {
        Optional<Categorie> categorieOptional = categorieRepository.findById(id);
        if (categorieOptional.isPresent()) {
            Categorie categorie = categorieOptional.get();
            categorie.setNom(categorieDetails.getNom());
            categorieRepository.save(categorie);
            return ResponseEntity.ok(categorie);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/categories/delete/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteCategorie(@PathVariable Long id) {
        if (categorieRepository.existsById(id)) {
            categorieRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
