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
    private String description;

    @Column(nullable = false)
    private String reponse;

    @Column(nullable = false)
    private String indice;


    @ManyToMany
    @JoinTable(name = "Enigme_Categorie", joinColumns = @JoinColumn(name = "enigme_id"), inverseJoinColumns = @JoinColumn(name = "categorie_id"))
    private List<Categorie> categories;
}