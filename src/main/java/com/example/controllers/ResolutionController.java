package com.example.controllers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.entities.Resolution;
import com.example.repository.EnigmeRepository;
import com.example.repository.ResolutionRepository;
import com.example.repository.UtilisateurRepository;

@RestController
@RequestMapping("/api/resolutions")
public class ResolutionController {

    @Autowired
    private ResolutionRepository resolutionRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private EnigmeRepository enigmeRepository;

    @PostMapping
    public ResponseEntity<Resolution> sauvegarderResolution(@RequestParam Long utilisateurId, @RequestParam Long enigmeId) {
        // Vérifier l'utilisateur
        var utilisateurOpt = utilisateurRepository.findById(utilisateurId);
        if (utilisateurOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Vérifier l'énigme
        var enigmeOpt = enigmeRepository.findById(enigmeId);
        if (enigmeOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Vérifier si déjà résolu
        var dejaResolu = resolutionRepository.findByUtilisateurIdAndEnigmeId(utilisateurId, enigmeId);
        if (dejaResolu.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Resolution resolution = new Resolution();
        resolution.setUtilisateur(utilisateurOpt.get());
        resolution.setEnigme(enigmeOpt.get());
        resolution.setDateResolution(LocalDateTime.now());

        Resolution saved = resolutionRepository.save(resolution);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // Nouveau endpoint pour obtenir la liste des énigmes résolues par un utilisateur
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Long>> obtenirEnigmesResoluesParUtilisateur(@PathVariable Long userId) {
        // Vérifier que l'utilisateur existe
        var utilisateurOpt = utilisateurRepository.findById(userId);
        if (utilisateurOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Récupérer les résolutions pour cet utilisateur
        List<Resolution> resolutions = resolutionRepository.findByUtilisateurId(userId);
        // Extraire les IDs d'énigme
        List<Long> enigmesResoluesIds = resolutions.stream()
                .map(res -> res.getEnigme().getId())
                .toList();

        return ResponseEntity.ok(enigmesResoluesIds);
    }
}
