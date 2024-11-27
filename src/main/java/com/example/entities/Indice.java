package com.example.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Indice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer cout;

    @ManyToOne
    @JoinColumn(name = "enigme_id", nullable = false)
    @JsonBackReference // Utilisé pour éviter la boucle infinie lors de la sérialisation avec Enigme
    private Enigme enigme;

    // Getters and Setters (générés automatiquement avec Lombok)
}
