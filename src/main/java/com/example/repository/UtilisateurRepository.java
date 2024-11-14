package com.example.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.entities.Utilisateur;



public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByEmail(String email); 

}