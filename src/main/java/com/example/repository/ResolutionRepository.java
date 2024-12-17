package com.example.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.entities.Resolution;
import com.example.entities.Utilisateur;

@Repository
public interface ResolutionRepository extends JpaRepository<Resolution, Long> {
    List<Resolution> findByUtilisateurId(Long utilisateurId);
    List<Resolution> findByEnigmeId(Long enigmeId);
    Optional<Resolution> findByUtilisateurIdAndEnigmeId(Long utilisateurId, Long enigmeId);
    void deleteByUtilisateur(Utilisateur utilisateur);
}
    