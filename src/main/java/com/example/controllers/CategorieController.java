package com.example.controllers;

import com.example.entities.Categorie;
import com.example.repository.CategorieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
public class CategorieController {

    @Autowired
    private CategorieRepository categorieRepository;

    // POST - Créer une nouvelle catégorie
    @PostMapping
    public ResponseEntity<Categorie> creerCategorie(@RequestBody Categorie categorie) {
        Categorie nouvelleCategorie = categorieRepository.save(categorie);
        return new ResponseEntity<>(nouvelleCategorie, HttpStatus.CREATED);
    }

    // GET - Obtenir toutes les catégories
    @GetMapping
    public List<Categorie> obtenirToutesLesCategories() {
        return categorieRepository.findAll();
    }

    // GET - Obtenir une catégorie par ID
    @GetMapping("/{id}")
    public ResponseEntity<Categorie> obtenirCategorieParId(@PathVariable Long id) {
        Optional<Categorie> categorie = categorieRepository.findById(id);
        return categorie.map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // PUT - Mettre à jour une catégorie existante
    @PutMapping("/{id}")
    public ResponseEntity<Categorie> mettreAJourCategorie(@PathVariable Long id, @RequestBody Categorie detailsCategorie) {
        Optional<Categorie> categorieExistante = categorieRepository.findById(id);

        if (categorieExistante.isPresent()) {
            Categorie categorie = categorieExistante.get();
            categorie.setNom(detailsCategorie.getNom());
            Categorie categorieMisAJour = categorieRepository.save(categorie);
            return ResponseEntity.ok(categorieMisAJour);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // DELETE - Supprimer une catégorie par ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerCategorie(@PathVariable Long id) {
        Optional<Categorie> categorie = categorieRepository.findById(id);
        if (categorie.isPresent()) {
            categorieRepository.delete(categorie.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
