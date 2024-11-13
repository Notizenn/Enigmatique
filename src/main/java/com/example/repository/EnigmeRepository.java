package com.example.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.entities.Enigme;




public interface EnigmeRepository extends JpaRepository<Enigme, Long> {

}