package com.example.controllers;

import com.example.entities.Enigme;
import com.example.repository.EnigmeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/enigmes")
public class EnigmeController {

    @Autowired
    private EnigmeRepository enigmeRepository;

    // POST - Créer une nouvelle énigme
    @PostMapping
    public ResponseEntity<Enigme> creerEnigme(@RequestBody Enigme enigme) {
        Enigme nouvelleEnigme = enigmeRepository.save(enigme);
        return new ResponseEntity<>(nouvelleEnigme, HttpStatus.CREATED);
    }

    // GET - Obtenir toutes les énigmes
    @GetMapping
    public List<Enigme> obtenirToutesLesEnigmes() {
        return enigmeRepository.findAll();
    }

    // GET - Obtenir une énigme par ID
    @GetMapping("/{id}")
    public ResponseEntity<Enigme> obtenirEnigmeParId(@PathVariable Long id) {
        Optional<Enigme> enigme = enigmeRepository.findById(id);
        return enigme.map(ResponseEntity::ok)
                     .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // PUT - Mettre à jour une énigme existante
    @PutMapping("/{id}")
    public ResponseEntity<Enigme> mettreAJourEnigme(@PathVariable Long id, @RequestBody Enigme detailsEnigme) {
        Optional<Enigme> enigmeExistante = enigmeRepository.findById(id);

        if (enigmeExistante.isPresent()) {
            Enigme enigme = enigmeExistante.get();
            enigme.setTitre(detailsEnigme.getTitre());
            enigme.setDescription(detailsEnigme.getDescription());
            enigme.setReponse(detailsEnigme.getReponse());
            enigme.setNiveau(detailsEnigme.getNiveau());
            enigme.setCategories(detailsEnigme.getCategories());
            Enigme enigmeMisAJour = enigmeRepository.save(enigme);
            return ResponseEntity.ok(enigmeMisAJour);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // DELETE - Supprimer une énigme par ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerEnigme(@PathVariable Long id) {
        Optional<Enigme> enigme = enigmeRepository.findById(id);
        if (enigme.isPresent()) {
            enigmeRepository.delete(enigme.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
