package com.example.entities;

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

    @Column(nullable = false)
    private Integer enigmesResolues = 0;

    @Column(nullable = false)
    private Integer indicesUtilises = 0;

    @Column(nullable = false)
    private Integer enigmesAvecAchatReponse = 0;

    @Column(nullable = false)
    private Integer score = 0;

    // Champs pour chaque catégorie
    @Column(nullable = false)
    private Integer enigmesResoluesMaths = 0;

    @Column(nullable = false)
    private Integer enigmesResoluesLogique = 0;

    @Column(nullable = false)
    private Integer enigmesResoluesCrypto = 0;

    // Méthode pour ajouter une énigme résolue
    public void ajouterEnigmeResolue(String categorie, boolean avecIndice, boolean avecAchatReponse, int points) {
        enigmesResolues++;
        if (avecIndice) {
            indicesUtilises++;
        }
        if (avecAchatReponse) {
            enigmesAvecAchatReponse++;
        }
        score += points;

        // Incrémenter le compteur de la catégorie correspondante
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
