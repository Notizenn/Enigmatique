# Enigmathique - Projet JEE

## Description

**Enigmathique** est une application web développée avec **SpringBoot**. Elle permet aux utilisateurs de résoudre des énigmes classées par catégories, suivre leur progression grâce à des statistiques, et utiliser des indices pour les aider. Le projet intègre des fonctionnalités d'administration pour gérer les utilisateurs et les énigmes.

## Comment ça fonctionne

- **Connexion Utilisateur** :
  - Lorsque vous ouvrez l’application, vous devez d'abord vous connecter. 
  - Si vous n'avez pas encore de compte, vous pouvez en créer un directement sur la page de connexion.


- **Authentification et Autorisation** :
  - Connexion des utilisateurs avec des rôles différents (ADMIN et USER).
  - Gestion sécurisée des mots de passe.

- **Résoudre des Énigmes** :
  - Une fois connecté, vous pouvez voir une liste d'énigmes classées par catégories. Vous pouvez en choisir une et essayer de la résoudre.

- **Indices** :
  - Si une énigme est trop difficile, vous pouvez consulter les indices associés pour vous aider à la résoudre.

---

## Fonctionnalités

- **Authentification et Autorisation** :
  - Connexion des utilisateurs avec des rôles différents (ADMIN et USER).
  - Gestion sécurisée des mots de passe.

- **Gestion des utilisateurs** :
  - Création de compte utilisateur.
  - Suivi personnalisé des énigmes résolues pour chaque utilisateur.

- **Gestion des énigmes** :
  - Catégorisation des énigmes (Logique, Maths, Cryptographie).
  - Possibilité de consulter des indices pour résoudre les énigmes.

- **Suivi de progression** :
  - Chaque utilisateur peut voir quelles énigmes il a résolues.

- **Gestion des administrateurs** :
  - Un compte administrateur permet d'ajouter, modifier et supprimer des énigmes.

---

## Accès aux comptes de démonstration

### **Compte Administrateur**
- **Email** : `admin@admin`
- **Mot de passe** : `admin`

### **Comptes Utilisateurs**
1. **Utilisateur 1** :
   - **Email** : `utilisateur1@gmail.com`
   - **Mot de passe** : `utilisateur1`

2. **Utilisateur 2** :
   - **Email** : `utilisateur2@gmail.com`
   - **Mot de passe** : `utilisateur2`

---

## Technologies Utilisées

- **Backend** :
  - Spring Boot (3.x)
  - Spring Data JPA
  - Spring Security

- **Frontend** :
  - Thymeleaf
  - HTML, CSS (Bootstrap)
  - JavaScript (dynamisation des pages)

- **Base de Données** :
  - H2 (par défaut, en mémoire)
  - Compatible MySQL / PostgreSQL

- **Autres Outils** :
  - Maven (gestion des dépendances)
  - BCrypt (sécurisation des mots de passe)

---

## Prérequis

- **Java** : Version 11 ou supérieure.
- **Maven** : Pour gérer les dépendances.

---

## Installation

### Étapes d'installation

1. **Cloner le projet** :
   ```bash
   git clone https://github.com/Notizenn/Enigmatique.git
   ```

2. **Accéder au répertoire** :
   ```bash
   cd Enigmathique
   ```

3. **Configurer la base de données** (fichier `application.properties` par défaut pour H2) :
   ```properties
   spring.datasource.url=jdbc:h2:file:./data/testdb
   spring.datasource.username=sa
   spring.datasource.password=
   spring.jpa.show-sql=true
   spring.h2.console.enabled=true
   spring.jpa.hibernate.ddl-auto=update
   ```

4. **Lancer le projet** :
   ```bash
   mvn spring-boot:run
   ```

5. **Accéder à l'application** :
   ```
   http://localhost:8080
   ```

---

## Structure du Projet

```plaintext
enigmathique/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── com/
│   │   │   │   └── example/
│   │   │   │       ├── controllers/     # Contrôleurs (API REST)
│   │   │   │       ├── entities/        # Entités JPA
│   │   │   │       ├── repository/      # Interfaces pour les accès à la base
│   │   │   │       ├── service/         # Logique métier
│   │   │   │       └── security/        # Configuration Spring Security
│   │   └── resources/
│   │       ├── application.properties   # Configuration du projet
│   │       ├── templates/               # Vues Thymeleaf
│   │       └── static/                  # Fichiers statiques (CSS/JS)
├── pom.xml                              # Dépendances Maven
└── README.md                            # Documentation
```

---

## Utilisation

1. **Connexion** :
   - Utilisez l'un des comptes préconfigurés pour accéder à l'application.

2. **Résolution des énigmes** :
   - Parcourez les différentes catégories d'énigmes : Logique, Maths, Cryptographie.
   - Résolvez les énigmes et suivez votre progression.

3. **Administration** :
   - Connectez-vous avec le compte `admin@admin` pour ajouter, modifier ou supprimer des énigmes.

4. **Base de données H2** :
   - Accessible via :
     ```
     http://localhost:8080/h2-console
     ```
   - **JDBC URL** : `jdbc:h2:file:./data/testdb`

---

## Fonctionnement

- **Suivi personnalisé** : Les énigmes résolues sont sauvegardées pour chaque utilisateur grâce à des entités **Resolution**.
- **Sécurisation** : Les utilisateurs ne peuvent résoudre que leurs propres énigmes et voir leur progression personnelle.
- **Catégories** :
   - **1** : Logique
   - **2** : Maths
   - **3** : Cryptographie

---

## Auto-Évaluation

### Fonctionnalités (5/5)
- Toutes les fonctionnalités demandées sont présentes.
- L'application permet de gérer les entités (CRUD complet) et de créer des liens entre elles (relations 1-N et N-N).
- Une logique métier est implémentée : suivi personnalisé des énigmes résolues, gestion des indices et statistiques des utilisateurs.

### Technique (5/5)
- L'application utilise l'architecture MVC.
- Les contrôleurs respectent les conventions HTTP (GET, POST, PUT, DELETE).
- Chaque vue manipule des données transmises par son contrôleur.

### Qualité (5/5)
- Une attention particulière a été portée au design de l'application avec **Bootstrap** pour une interface agréable et réactive.
- Le code est bien structuré et les commits sont réguliers.
- Le repository Git est complet et organisé.

### Soutenance (4/5)
- Le projet est bien préparé pour la soutenance avec une démo fonctionnelle et des explications claires.
- Les réponses aux questions sont anticipées et la répartition du travail est bien respectée.

---

## Conclusion

**Enigmathique** est une plateforme ludique et éducative qui combine **puzzles**, **résolutions logiques** et suivi de progression. Grâce à l'intégration de **SpringBoot**, **Spring Security** et **Thymeleaf**, l'application offre une expérience utilisateur fluide et sécurisée. 

---

Bon jeu et bonne résolution d'énigmes ! 🎉
