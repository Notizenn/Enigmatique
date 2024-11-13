package com.example.repository;






import org.springframework.data.jpa.repository.JpaRepository;

import com.example.entities.Utilisateur;



public interface UserRepository extends JpaRepository<Utilisateur, Long> {

}