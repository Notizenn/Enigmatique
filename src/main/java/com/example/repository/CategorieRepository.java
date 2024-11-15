package com.example.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.entities.Categorie;




public interface CategorieRepository extends JpaRepository<Categorie, Long> {

}