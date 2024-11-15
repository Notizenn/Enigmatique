package com.example.controllers;

import com.example.entities.Indice;
import com.example.repository.IndiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/indices")
public class IndiceController {

    @Autowired
    private IndiceRepository indiceRepository;

    // POST - Créer un nouvel indice
    @PostMapping
    public ResponseEntity<Indice> creerIndice(@RequestBody Indice indice) {
        Indice nouvelIndice = indiceRepository.save(indice);
        return new ResponseEntity<>(nouvelIndice, HttpStatus.CREATED);
    }

    // GET - Obtenir tous les indices
    @GetMapping
    public List<Indice> obtenirTousLesIndices() {
        return indiceRepository.findAll();
    }

    // GET - Obtenir un indice par ID
    @GetMapping("/{id}")
    public ResponseEntity<Indice> obtenirIndiceParId(@PathVariable Long id) {
        Optional<Indice> indice = indiceRepository.findById(id);
        return indice.map(ResponseEntity::ok)
                     .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // PUT - Mettre à jour un indice existant
    @PutMapping("/{id}")
    public ResponseEntity<Indice> mettreAJourIndice(@PathVariable Long id, @RequestBody Indice detailsIndice) {
        Optional<Indice> indiceExistant = indiceRepository.findById(id);

        if (indiceExistant.isPresent()) {
            Indice indice = indiceExistant.get();
            indice.setDescription(detailsIndice.getDescription());
            indice.setCout(detailsIndice.getCout());
            indice.setEnigme(detailsIndice.getEnigme());
            Indice indiceMisAJour = indiceRepository.save(indice);
            return ResponseEntity.ok(indiceMisAJour);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // DELETE - Supprimer un indice par ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerIndice(@PathVariable Long id) {
        Optional<Indice> indice = indiceRepository.findById(id);
        if (indice.isPresent()) {
            indiceRepository.delete(indice.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
