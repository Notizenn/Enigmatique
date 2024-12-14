package com.example.controllers;

import com.example.entities.Utilisateur;
import com.example.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Créer un nouvel utilisateur
    @PostMapping
    public ResponseEntity<Utilisateur> creerUtilisateur(@RequestBody Utilisateur utilisateur) {
        // Encoder le mot de passe avant de l'enregistrer
        utilisateur.setMotDePasse(passwordEncoder.encode(utilisateur.getMotDePasse()));

        // Si le champ "sous" n'est pas défini dans la requête, le définir à une valeur par défaut (ex. 0)
        if (utilisateur.getSous() == 0) {
            utilisateur.setSous(0); // Valeur par défaut
        }

        Utilisateur nouvelUtilisateur = utilisateurRepository.save(utilisateur);
        return new ResponseEntity<>(nouvelUtilisateur, HttpStatus.CREATED);
    }

    // Obtenir tous les utilisateurs
    @GetMapping
    public List<Utilisateur> obtenirTousLesUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    // Obtenir un utilisateur par ID
    @GetMapping("/{id}")
    public ResponseEntity<Utilisateur> obtenirUtilisateurParId(@PathVariable Long id) {
        Optional<Utilisateur> utilisateur = utilisateurRepository.findById(id);
        return utilisateur.map(ResponseEntity::ok)
                          .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // Mettre à jour un utilisateur
    @PutMapping("/{id}")
    public ResponseEntity<Utilisateur> mettreAJourUtilisateur(@PathVariable Long id, @RequestBody Utilisateur detailsUtilisateur) {
        Optional<Utilisateur> utilisateurExistant = utilisateurRepository.findById(id);

        if (utilisateurExistant.isPresent()) {
            Utilisateur utilisateur = utilisateurExistant.get();
            utilisateur.setNom(detailsUtilisateur.getNom());
            utilisateur.setEmail(detailsUtilisateur.getEmail());

            // Si le mot de passe est fourni dans la requête, le réencoder
            if (detailsUtilisateur.getMotDePasse() != null && !detailsUtilisateur.getMotDePasse().isEmpty()) {
                utilisateur.setMotDePasse(passwordEncoder.encode(detailsUtilisateur.getMotDePasse()));
            }

            // Mettre à jour le champ "sous" si spécifié
            utilisateur.setSous(detailsUtilisateur.getSous());

            Utilisateur utilisateurMisAJour = utilisateurRepository.save(utilisateur);
            return ResponseEntity.ok(utilisateurMisAJour);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Supprimer un utilisateur
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerUtilisateur(@PathVariable Long id) {
        Optional<Utilisateur> utilisateur = utilisateurRepository.findById(id);
        if (utilisateur.isPresent()) {
            utilisateurRepository.delete(utilisateur.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
