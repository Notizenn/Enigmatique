package com.example.controllers;

import com.example.entities.*;
import com.example.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

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

    // *** Gestion des Utilisateurs ***
    @GetMapping("/utilisateurs")
    public String listUtilisateurs(Model model) {
        model.addAttribute("utilisateurs", utilisateurRepository.findAll());
        return "admin/utilisateurs";
    }

    @PostMapping("/utilisateurs/add")
    public String addUtilisateur(@ModelAttribute Utilisateur utilisateur) {
        utilisateurRepository.save(utilisateur);
        return "redirect:/admin/utilisateurs";
    }

    @PutMapping("/utilisateurs/update/{id}")
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

    @DeleteMapping("/utilisateurs/delete/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteUtilisateur(@PathVariable Long id) {
        if (utilisateurRepository.existsById(id)) {
            utilisateurRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // *** Gestion des Statistiques ***
    @GetMapping("/statistiques")
    public String listStatistiques(Model model) {
        model.addAttribute("statistiques", statistiqueRepository.findAll());
        return "admin/statistiques";
    }

    @DeleteMapping("/statistiques/delete/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteStatistique(@PathVariable Long id) {
        if (statistiqueRepository.existsById(id)) {
            statistiqueRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // *** Gestion des Indices ***
    @GetMapping("/indices")
    public String listIndices(Model model) {
        model.addAttribute("indices", indiceRepository.findAll());
        model.addAttribute("enigmes", enigmeRepository.findAll()); // Pour associer un indice à une énigme
        return "admin/indices";
    }

    @PostMapping("/indices/add")
    public String addIndice(@ModelAttribute Indice indice) {
        indiceRepository.save(indice);
        return "redirect:/admin/indices";
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
    @GetMapping("/enigmes")
    public String listEnigmes(Model model) {
        model.addAttribute("enigmes", enigmeRepository.findAll());
        model.addAttribute("categories", categorieRepository.findAll());
        return "admin/enigmes";
    }

    @PostMapping("/enigmes/add")
    public String addEnigme(@ModelAttribute Enigme enigme) {
        // Initialiser les listes vides si elles ne sont pas fournies
        if (enigme.getIndices() == null) {
            enigme.setIndices(new ArrayList<>());
        }
        if (enigme.getCategories() == null) {
            enigme.setCategories(new ArrayList<>());
        }

        enigmeRepository.save(enigme);
        return "redirect:/admin/enigmes";
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
    @GetMapping("/categories")
    public String listCategories(Model model) {
        model.addAttribute("categories", categorieRepository.findAll());
        return "admin/categories";
    }

    @PostMapping("/categories/add")
    public String addCategorie(@ModelAttribute Categorie categorie) {
        categorieRepository.save(categorie);
        return "redirect:/admin/categories";
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
