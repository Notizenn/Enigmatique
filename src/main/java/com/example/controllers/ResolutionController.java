package com.example.controllers;

import com.example.entities.Resolution;
import com.example.repository.ResolutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/resolutions")
public class ResolutionController {

    @Autowired
    private ResolutionRepository resolutionRepository;

    // POST - Créer une nouvelle résolution
    @PostMapping
    public ResponseEntity<Resolution> creerResolution(@RequestBody Resolution resolution) {
        Resolution nouvelleResolution = resolutionRepository.save(resolution);
        return new ResponseEntity<>(nouvelleResolution, HttpStatus.CREATED);
    }

    // GET - Obtenir toutes les résolutions
    @GetMapping
    public List<Resolution> obtenirToutesLesResolutions() {
        return resolutionRepository.findAll();
    }

    // GET - Obtenir une résolution par ID
    @GetMapping("/{id}")
    public ResponseEntity<Resolution> obtenirResolutionParId(@PathVariable Long id) {
        Optional<Resolution> resolution = resolutionRepository.findById(id);
        return resolution.map(ResponseEntity::ok)
                         .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // PUT - Mettre à jour une résolution existante
    @PutMapping("/{id}")
    public ResponseEntity<Resolution> mettreAJourResolution(@PathVariable Long id, @RequestBody Resolution detailsResolution) {
        Optional<Resolution> resolutionExistante = resolutionRepository.findById(id);

        if (resolutionExistante.isPresent()) {
            Resolution resolution = resolutionExistante.get();
            resolution.setDateTentative(detailsResolution.getDateTentative());
            resolution.setUtilisateur(detailsResolution.getUtilisateur());
            resolution.setEnigme(detailsResolution.getEnigme());
            Resolution resolutionMisAJour = resolutionRepository.save(resolution);
            return ResponseEntity.ok(resolutionMisAJour);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // DELETE - Supprimer une résolution par ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerResolution(@PathVariable Long id) {
        Optional<Resolution> resolution = resolutionRepository.findById(id);
        if (resolution.isPresent()) {
            resolutionRepository.delete(resolution.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
