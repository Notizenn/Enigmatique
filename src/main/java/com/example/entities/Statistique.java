package com.example.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Statistique {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @ManyToOne
    @JoinColumn(name = "enigme_id", nullable = true)
    @JsonBackReference 
    private Enigme enigme;

    @Column(nullable = false)
    private Integer enigmesResolues = 0;

    @Column(nullable = false)
    private Integer indicesUtilises = 0;

    @Column(nullable = false)
    private Integer enigmesAvecAchatReponse = 0;

    @Column(nullable = false)
    private Integer score = 0;

    @Column(nullable = false)
    private Integer enigmesResoluesMaths = 0;

    @Column(nullable = false)
    private Integer enigmesResoluesLogique = 0;

    @Column(nullable = false)
    private Integer enigmesResoluesCrypto = 0;


    public void ajouterEnigmeResolue(String categorie, boolean avecIndice, boolean avecAchatReponse, int points) {
        enigmesResolues++;
        if (avecIndice) {
            indicesUtilises++;
        }
        if (avecAchatReponse) {
            enigmesAvecAchatReponse++;
        }
        score += points;

        switch (categorie.toLowerCase()) {
            case "maths":
                enigmesResoluesMaths++;
                break;
            case "logique":
                enigmesResoluesLogique++;
                break;
            case "cryptographie":
            case "crypto":
                enigmesResoluesCrypto++;
                break;
            default:
                throw new IllegalArgumentException("Catégorie invalide : " + categorie);
        }
    }

}
