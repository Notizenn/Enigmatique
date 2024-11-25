package com.example.controllers;

import com.example.entities.Categorie;
import com.example.entities.Enigme;
import com.example.repository.EnigmeRepository;
import com.example.repository.CategorieRepository;
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

    @Autowired
    private CategorieRepository categorieRepository;

  
    @GetMapping
    public ResponseEntity<List<Enigme>> obtenirToutesLesEnigmes() {
        List<Enigme> enigmes = enigmeRepository.findAll();
        if (enigmes.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(enigmes, HttpStatus.OK);
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<Enigme> obtenirEnigmeParId(@PathVariable Long id) {
        Optional<Enigme> enigme = enigmeRepository.findById(id);
        return enigme.map(ResponseEntity::ok)
                     .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

  
    @SuppressWarnings("null")
    @PostMapping
    public ResponseEntity<Enigme> creerEnigme(@RequestBody Enigme enigme) {
        try {
     
            Enigme nouvelleEnigme = enigmeRepository.save(enigme);
            return new ResponseEntity<>(nouvelleEnigme, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace(); 
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @SuppressWarnings("null")
    @PostMapping("/bulk")
    public ResponseEntity<List<Enigme>> ajouterEnigmes(@RequestBody List<Enigme> enigmes) {
        try {
         
            for (Enigme enigme : enigmes) {
                if (enigme.getCategories() != null) {
                    List<Long> categorieIds = enigme.getCategories().stream()
                            .map(Categorie::getId)
                            .toList();
                    List<Categorie> categories = categorieRepository.findAllById(categorieIds);
                    enigme.setCategories(categories);
                }
            }

          
            List<Enigme> nouvellesEnigmes = enigmeRepository.saveAll(enigmes);
            return new ResponseEntity<>(nouvellesEnigmes, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
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
            enigme.setIndice(detailsEnigme.getIndice());

            if (detailsEnigme.getCategories() != null && !detailsEnigme.getCategories().isEmpty()) {
                List<Long> categorieIds = detailsEnigme.getCategories().stream()
                        .map(categorie -> categorie.getId())
                        .toList();

                List<com.example.entities.Categorie> categories = categorieRepository.findAllById(categorieIds);

                if (categories.size() != categorieIds.size()) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
                enigme.setCategories(categories);
            }

            Enigme enigmeMisAJour = enigmeRepository.save(enigme);
            return ResponseEntity.ok(enigmeMisAJour);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

  
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
