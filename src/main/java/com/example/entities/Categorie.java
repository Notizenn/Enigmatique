package com.example.entities;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Categorie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    private String nom;

    @ManyToMany(mappedBy = "categories")
    private List<Enigme> enigmes;

    // Getters and Setters
}
