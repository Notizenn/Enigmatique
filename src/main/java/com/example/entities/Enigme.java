package com.example.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    @JsonManagedReference 
    private List<Indice> indices;

    @OneToMany(mappedBy = "enigme", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference 
    private List<Statistique> statistiques;

    @ManyToOne
    @JoinColumn(name = "categorie_id")
    @JsonIgnoreProperties("enigmes")
    private Categorie categorie;
}
