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
    private PasswordEncoder passwordEncoder;  // Injecter le PasswordEncoder

    @PostMapping
    public ResponseEntity<Utilisateur> creerUtilisateur(@RequestBody Utilisateur utilisateur) {
        // Encoder le mot de passe avant de l'enregistrer
        utilisateur.setMotDePasse(passwordEncoder.encode(utilisateur.getMotDePasse()));
        
        Utilisateur nouvelUtilisateur = utilisateurRepository.save(utilisateur);
        return new ResponseEntity<>(nouvelUtilisateur, HttpStatus.CREATED);
    }

  
    @GetMapping
    public List<Utilisateur> obtenirTousLesUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Utilisateur> obtenirUtilisateurParId(@PathVariable Long id) {
        Optional<Utilisateur> utilisateur = utilisateurRepository.findById(id);
        return utilisateur.map(ResponseEntity::ok)
                          .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    
    @PutMapping("/{id}")
    public ResponseEntity<Utilisateur> mettreAJourUtilisateur(@PathVariable Long id, @RequestBody Utilisateur detailsUtilisateur) {
        Optional<Utilisateur> utilisateurExistant = utilisateurRepository.findById(id);

        if (utilisateurExistant.isPresent()) {
            Utilisateur utilisateur = utilisateurExistant.get();
            utilisateur.setNom(detailsUtilisateur.getNom());
            utilisateur.setEmail(detailsUtilisateur.getEmail());
            utilisateur.setMotDePasse(detailsUtilisateur.getMotDePasse());
            Utilisateur utilisateurMisAJour = utilisateurRepository.save(utilisateur);
            return ResponseEntity.ok(utilisateurMisAJour);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    
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
