package com.example.controllers;

import com.example.entities.Categorie;
import com.example.entities.Enigme;
import com.example.repository.EnigmeRepository;
import com.example.repository.CategorieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/enigmes")
public class EnigmeController {

    @Autowired
    private EnigmeRepository enigmeRepository;

    @Autowired
    private CategorieRepository categorieRepository;

    // Obtenir toutes les énigmes
    @GetMapping
    public ResponseEntity<List<Enigme>> obtenirToutesLesEnigmes() {
        List<Enigme> enigmes = enigmeRepository.findAll();
        if (enigmes.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(enigmes, HttpStatus.OK);
    }

    // Obtenir une énigme par ID
    @GetMapping("/{id}")
    public ResponseEntity<Enigme> obtenirEnigmeParId(@PathVariable Long id) {
        Optional<Enigme> enigme = enigmeRepository.findById(id);
        return enigme.map(ResponseEntity::ok)
                     .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<?> creerEnigme(@RequestBody Enigme enigme) {
        try {
            // Vérifier le niveau non vide
            if (enigme.getNiveau() == null || enigme.getNiveau().isEmpty()) {
                return ResponseEntity.badRequest().body("Le champ niveau est obligatoire.");
            }
    
            // Vérifier la catégorie si nécessaire
            if (enigme.getCategorie() != null && enigme.getCategorie().getId() != null) {
                Optional<Categorie> catOpt = categorieRepository.findById(enigme.getCategorie().getId());
                if (catOpt.isEmpty()) {
                    return ResponseEntity.badRequest().body("Catégorie non trouvée.");
                }
                enigme.setCategorie(catOpt.get());
            }
    
            Enigme nouvelleEnigme = enigmeRepository.save(enigme);
            return new ResponseEntity<>(nouvelleEnigme, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Ajouter plusieurs énigmes en une seule requête
    @PostMapping("/bulk")
    public ResponseEntity<List<Enigme>> ajouterEnigmes(@RequestBody List<Enigme> enigmes) {
        try {
            for (Enigme enigme : enigmes) {
                if (enigme.getCategorie() != null && enigme.getCategorie().getId() != null) {
                    Optional<Categorie> catOpt = categorieRepository.findById(enigme.getCategorie().getId());
                    if (catOpt.isEmpty()) {
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }
                    enigme.setCategorie(catOpt.get());
                }
            }

            List<Enigme> nouvellesEnigmes = enigmeRepository.saveAll(enigmes);
            return new ResponseEntity<>(nouvellesEnigmes, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Enigme> mettreAJourEnigme(@PathVariable Long id, @RequestBody Enigme detailsEnigme) {
        Optional<Enigme> enigmeExistante = enigmeRepository.findById(id);
    
        if (enigmeExistante.isPresent()) {
            Enigme enigme = enigmeExistante.get();
            enigme.setTitre(detailsEnigme.getTitre());
            enigme.setDescription(detailsEnigme.getDescription());
            enigme.setReponse(detailsEnigme.getReponse());
            enigme.setNiveau(detailsEnigme.getNiveau());
    
            // Vérifier le niveau non vide si nécessaire
            if (enigme.getNiveau() == null || enigme.getNiveau().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
    
            // Vérifier la catégorie
            if (detailsEnigme.getCategorie() != null && detailsEnigme.getCategorie().getId() != null) {
                Optional<Categorie> catOpt = categorieRepository.findById(detailsEnigme.getCategorie().getId());
                if (catOpt.isEmpty()) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
                enigme.setCategorie(catOpt.get());
            }
    
            Enigme enigmeMisAJour = enigmeRepository.save(enigme);
            return ResponseEntity.ok(enigmeMisAJour);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Supprimer une énigme
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerEnigme(@PathVariable Long id) {
        Optional<Enigme> enigme = enigmeRepository.findById(id);
        if (enigme.isPresent()) {
            enigmeRepository.delete(enigme.get());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
