package com.example.entities;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Enigme {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    private String titre;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private String reponse;
    
    @Column(nullable = false)
    private String niveau;

    @OneToMany(mappedBy = "enigme", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Indice> indices;

    @OneToMany(mappedBy = "enigme", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Resolution> resolutions;

    @ManyToMany
    @JoinTable(
        name = "Enigme_Categorie",
        joinColumns = @JoinColumn(name = "enigme_id"),
        inverseJoinColumns = @JoinColumn(name = "categorie_id")
    )
    private List<Categorie> categories;

    // Getters and Setters
}
