package com.example.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.entities.Categorie;
import com.example.entities.Enigme;
import java.util.List;




public interface EnigmeRepository extends JpaRepository<Enigme, Long> {
    List<Enigme> findByCategorie(Categorie categorie);
}