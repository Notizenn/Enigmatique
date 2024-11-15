package com.example.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.entities.Resolution;




public interface ResolutionRepository extends JpaRepository<Resolution, Long> {

}