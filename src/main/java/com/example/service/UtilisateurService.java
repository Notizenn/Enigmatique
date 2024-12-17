package com.example.service;

import com.example.entities.Utilisateur;
import com.example.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.repository.ResolutionRepository;



@Service
public class UtilisateurService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private ResolutionRepository resolutionRepository;

    public void deleteUtilisateur(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("L'utilisateur avec l'id " + id + " n'existe pas"));

        // Supprimer toutes les résolutions associées
        resolutionRepository.deleteByUtilisateur(utilisateur);

        // Maintenant, supprimer l'utilisateur
        utilisateurRepository.delete(utilisateur);
    }
}
