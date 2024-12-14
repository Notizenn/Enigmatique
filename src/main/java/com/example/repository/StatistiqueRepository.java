package com.example.repository;

import com.example.entities.Statistique;
import com.example.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StatistiqueRepository extends JpaRepository<Statistique, Long> {

    // Trouver les statistiques globales d'un utilisateur
    Optional<Statistique> findByUtilisateur(Utilisateur utilisateur);

    // Trouver les statistiques globales par ID utilisateur
    @Query("SELECT s FROM Statistique s WHERE s.utilisateur.id = :utilisateurId")
    Optional<Statistique> findByUtilisateurId(Long utilisateurId);
}
