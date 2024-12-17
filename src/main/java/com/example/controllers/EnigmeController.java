package com.example.controllers;

import com.example.entities.Categorie;
import com.example.entities.Enigme;
import com.example.repository.CategorieRepository;
import com.example.repository.EnigmeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/enigmes")
public class EnigmeController {

    @Autowired
    private EnigmeRepository enigmeRepository;

    @Autowired
    private CategorieRepository categorieRepository;

    // Obtenir toutes les énigmes, avec option de filtrer par ID de catégorie
    @GetMapping
    public ResponseEntity<List<Enigme>> obtenirToutesLesEnigmes(@RequestParam(required = false) Long categorieId) {
        List<Enigme> enigmes;
        if (categorieId != null) {
            Optional<Categorie> categorieOpt = categorieRepository.findById(categorieId);
            if (categorieOpt.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            enigmes = enigmeRepository.findByCategorie(categorieOpt.get());
        } else {
            enigmes = enigmeRepository.findAll();
        }

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

    // Créer une nouvelle énigme
    @PostMapping
    public ResponseEntity<Enigme> creerEnigme(@RequestBody Enigme enigme) {
        try {
            // Validation de la catégorie
            if (enigme.getCategorie() != null) {
                Long categorieId = enigme.getCategorie().getId();
                Optional<Categorie> categorieOptional = categorieRepository.findById(categorieId);
                if (!categorieOptional.isPresent()) {
                    return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
                }
                enigme.setCategorie(categorieOptional.get());
            } else {
                enigme.setCategorie(null); // Optionnel : gérer les énigmes sans catégorie
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
                if (enigme.getCategorie() != null) {
                    Long categorieId = enigme.getCategorie().getId();
                    Optional<Categorie> categorieOptional = categorieRepository.findById(categorieId);
                    if (categorieOptional.isPresent()) {
                        enigme.setCategorie(categorieOptional.get());
                    } else {
                        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
                    }
                } else {
                    enigme.setCategorie(null); // Optionnel : gérer les énigmes sans catégorie
                }
            }

            List<Enigme> nouvellesEnigmes = enigmeRepository.saveAll(enigmes);
            return new ResponseEntity<>(nouvellesEnigmes, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Mettre à jour une énigme
    @PutMapping("/{id}")
    public ResponseEntity<Enigme> mettreAJourEnigme(@PathVariable Long id, @RequestBody Enigme detailsEnigme) {
        Optional<Enigme> enigmeExistante = enigmeRepository.findById(id);

        if (enigmeExistante.isPresent()) {
            Enigme enigme = enigmeExistante.get();
            enigme.setTitre(detailsEnigme.getTitre());
            enigme.setDescription(detailsEnigme.getDescription());
            enigme.setReponse(detailsEnigme.getReponse());
            enigme.setNiveau(detailsEnigme.getNiveau());

            // Gestion de la catégorie
            if (detailsEnigme.getCategorie() != null) {
                Long categorieId = detailsEnigme.getCategorie().getId();
                Optional<Categorie> categorieOptional = categorieRepository.findById(categorieId);
                if (categorieOptional.isPresent()) {
                    enigme.setCategorie(categorieOptional.get());
                } else {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            } else {
                enigme.setCategorie(null); // Optionnel : gérer les énigmes sans catégorie
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
