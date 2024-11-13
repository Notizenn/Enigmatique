package com.example.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Resolution{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    private LocalDateTime dateTentative = LocalDateTime.now();
    
    private String reponseFournie;
    
    private Boolean reussite;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @ManyToOne
    @JoinColumn(name = "enigme_id", nullable = false)
    private Enigme enigme;

    // Getters and Setters
}
