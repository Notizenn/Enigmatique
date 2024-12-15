package com.example.controllers;

import com.example.entities.Statistique;
import com.example.entities.Utilisateur;
import com.example.repository.StatistiqueRepository;
import com.example.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/statistiques")
public class StatistiqueController {

    @Autowired
    private StatistiqueRepository statistiqueRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    // GET - Obtenir toutes les statistiques
    @GetMapping
    public ResponseEntity<Iterable<Statistique>> obtenirToutesLesStatistiques() {
        Iterable<Statistique> statistiques = statistiqueRepository.findAll();
        return ResponseEntity.ok(statistiques);
    }

    // GET - Obtenir les statistiques globales pour un utilisateur
    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<Statistique> obtenirStatistiquesGlobales(@PathVariable Long utilisateurId) {
        return statistiqueRepository.findByUtilisateurId(utilisateurId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // POST - Ajouter ou mettre à jour les statistiques pour un utilisateur
    @PostMapping
    public ResponseEntity<Statistique> ajouterOuMettreAJourStatistique(
            @RequestParam Long utilisateurId,
            @RequestParam String categorie,
            @RequestParam boolean avecIndice,
            @RequestParam boolean avecAchatReponse,
            @RequestParam int points) {

        // Vérifier si l'utilisateur existe
        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findById(utilisateurId);
        if (utilisateurOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        Utilisateur utilisateur = utilisateurOpt.get();

        // Rechercher ou créer les statistiques globales de l'utilisateur
        Statistique statistique = statistiqueRepository.findByUtilisateur(utilisateur)
                .orElseGet(() -> {
                    Statistique nouvelleStat = new Statistique();
                    nouvelleStat.setUtilisateur(utilisateur);
                    return statistiqueRepository.save(nouvelleStat);
                });

        // Mettre à jour les statistiques en fonction de la catégorie
        try {
            statistique.ajouterEnigmeResolue(categorie, avecIndice, avecAchatReponse, points);
            statistiqueRepository.save(statistique);
            return ResponseEntity.status(HttpStatus.CREATED).body(statistique);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // DELETE - Réinitialiser les statistiques pour un utilisateur
    @DeleteMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<Void> reinitialiserStatistiques(@PathVariable Long utilisateurId) {
        Optional<Statistique> statistique = statistiqueRepository.findByUtilisateurId(utilisateurId);
        if (statistique.isPresent()) {
            statistiqueRepository.delete(statistique.get());
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
