package com.example.entities;

import jakarta.persistence.*;

@Entity
public class Indice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private Integer cout;

    @ManyToOne
    @JoinColumn(name = "enigme_id", nullable = false)
    private Enigme enigme;

    // Getters and Setters
}
