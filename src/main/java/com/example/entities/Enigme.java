package com.example.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Enigme {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String titre;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private String reponse;
    
    @Column(nullable = false)
    private String niveau;

    @OneToMany(mappedBy = "enigme", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // Gérer la relation parent avec Indice
    private List<Indice> indices;

    @OneToMany(mappedBy = "enigme", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // Gérer la relation parent avec Statistique
    private List<Statistique> statistiques;

    @ManyToMany
    @JoinTable(name = "Enigme_Categorie", joinColumns = @JoinColumn(name = "enigme_id"), inverseJoinColumns = @JoinColumn(name = "categorie_id"))
    private List<Categorie> categories;
}
