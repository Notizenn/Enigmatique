package com.example.entities;

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
    private String question;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reponse;

    @Column(nullable = false)
    private String indice;

    @OneToMany(mappedBy = "enigme", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Statistique> statistiques; // Correction du mappedBy ici, maintenant c'est correct

    @ManyToMany
    @JoinTable(
        name = "Enigme_Categorie",
        joinColumns = @JoinColumn(name = "enigme_id"),
        inverseJoinColumns = @JoinColumn(name = "categorie_id")
    )
    private List<Categorie> categories;
}