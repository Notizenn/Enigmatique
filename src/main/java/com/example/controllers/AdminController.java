package com.example.controllers;

import com.example.entities.Categorie;
import com.example.entities.Enigme;
import com.example.entities.Utilisateur;
import com.example.entities.Indice;
import com.example.repository.CategorieRepository;
import com.example.repository.EnigmeRepository;
import com.example.repository.UtilisateurRepository;
import com.example.repository.IndiceRepository;
import com.example.repository.StatistiqueRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private StatistiqueRepository statistiqueRepository;

    @Autowired
    private IndiceRepository indiceRepository;

    @Autowired
    private EnigmeRepository enigmeRepository;

    @Autowired
    private CategorieRepository categorieRepository;

    // *** Page d'administration générale ***
    @GetMapping
    public String adminPage(Model model) {
        model.addAttribute("utilisateurs", utilisateurRepository.findAll());
        model.addAttribute("statistiques", statistiqueRepository.findAll());
        model.addAttribute("indices", indiceRepository.findAll());
        model.addAttribute("enigmes", enigmeRepository.findAll());
        model.addAttribute("categories", categorieRepository.findAll());
        return "admin"; // Assurez-vous que le fichier est nommé admin.html et est dans le bon dossier
    }

    // *** Endpoint pour récupérer toutes les données en JSON ***
    @GetMapping("/data")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAllData() {
        try {
            Map<String, Object> data = new HashMap<>();

            // Utilisateurs sans mot de passe
            List<Map<String, Object>> utilisateurs = utilisateurRepository.findAll().stream().map(utilisateur -> {
                Map<String, Object> utilisateurData = new HashMap<>();
                utilisateurData.put("id", utilisateur.getId());
                utilisateurData.put("nom", utilisateur.getNom());
                utilisateurData.put("email", utilisateur.getEmail());
                utilisateurData.put("sous", utilisateur.getSous());
                utilisateurData.put("admin", utilisateur.isAdmin());
                return utilisateurData;
            }).collect(Collectors.toList());

            // Indices avec informations sur les énigmes
            List<Map<String, Object>> indices = indiceRepository.findAll().stream().map(indice -> {
                Map<String, Object> indiceData = new HashMap<>();
                indiceData.put("id", indice.getId());
                indiceData.put("description", indice.getDescription());
                indiceData.put("cout", indice.getCout());
                indiceData.put("enigmeId", indice.getEnigme().getId());
                indiceData.put("enigmeTitre", indice.getEnigme().getTitre());
                return indiceData;
            }).collect(Collectors.toList());

            // Enigmes avec informations sur les catégories
            List<Map<String, Object>> enigmes = enigmeRepository.findAll().stream().map(enigme -> {
                Map<String, Object> enigmeData = new HashMap<>();
                enigmeData.put("id", enigme.getId());
                enigmeData.put("titre", enigme.getTitre());
                enigmeData.put("description", enigme.getDescription());
                enigmeData.put("reponse", enigme.getReponse());
                enigmeData.put("niveau", enigme.getNiveau());
                enigmeData.put("categorieId", enigme.getCategorie() != null ? enigme.getCategorie().getId() : null);
                enigmeData.put("categorieNom", enigme.getCategorie() != null ? enigme.getCategorie().getNom() : "Sans Catégorie");
                return enigmeData;
            }).collect(Collectors.toList());

            // Catégories
            List<Map<String, Object>> categories = categorieRepository.findAll().stream().map(categorie -> {
                Map<String, Object> categorieData = new HashMap<>();
                categorieData.put("id", categorie.getId());
                categorieData.put("nom", categorie.getNom());
                return categorieData;
            }).collect(Collectors.toList());

            // Statistiques
            List<Map<String, Object>> statistiques = statistiqueRepository.findAll().stream().map(statistique -> {
                Map<String, Object> statistiqueData = new HashMap<>();
                statistiqueData.put("id", statistique.getId());
                statistiqueData.put("utilisateurNom", statistique.getUtilisateur().getNom());
                statistiqueData.put("score", statistique.getScore());
                return statistiqueData;
            }).collect(Collectors.toList());

            data.put("utilisateurs", utilisateurs);
            data.put("enigmes", enigmes);
            data.put("categories", categories);
            data.put("indices", indices);
            data.put("statistiques", statistiques);

            return ResponseEntity.ok(data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // *** Gestion des Utilisateurs ***
    @PostMapping("/utilisateurs/add")
    public ResponseEntity<?> addUtilisateur(@RequestBody Utilisateur utilisateur) {
        try {
            // Vérification de l'unicité de l'email
            if (utilisateurRepository.findByEmail(utilisateur.getEmail()).isPresent()) {
                return ResponseEntity.badRequest().body("L'email existe déjà. Veuillez en choisir un autre.");
            }

            // Encode le mot de passe avant de sauvegarder
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            utilisateur.setMotDePasse(passwordEncoder.encode(utilisateur.getMotDePasse()));

            // Définir une valeur par défaut pour 'sous' si nécessaire
            if (utilisateur.getSous() == 0) {
                utilisateur.setSous(100); // Par exemple, définir une valeur par défaut
            }

            // Sauvegarder l'utilisateur
            utilisateurRepository.save(utilisateur);
            return ResponseEntity.ok(utilisateur);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l'ajout de l'utilisateur.");
        }
    }

    @PutMapping("/utilisateurs/update/{id}")
    @ResponseBody
    public ResponseEntity<Utilisateur> updateUtilisateur(@PathVariable Long id, @RequestBody Utilisateur utilisateurDetails) {
        try {
            Optional<Utilisateur> utilisateurOptional = utilisateurRepository.findById(id);
            if (utilisateurOptional.isPresent()) {
                Utilisateur utilisateur = utilisateurOptional.get();
                utilisateur.setNom(utilisateurDetails.getNom());
                utilisateur.setEmail(utilisateurDetails.getEmail());
                utilisateur.setAdmin(utilisateurDetails.isAdmin());
                utilisateurRepository.save(utilisateur);
                return ResponseEntity.ok(utilisateur);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/utilisateurs/delete/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteUtilisateur(@PathVariable Long id) {
        try {
            if (utilisateurRepository.existsById(id)) {
                utilisateurRepository.deleteById(id);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // *** Gestion des Indices ***
    @PostMapping("/indices/add")
    public ResponseEntity<?> addIndice(@RequestBody Map<String, Object> requestData) {
        try {
            String description = (String) requestData.get("description");
            int cout = (Integer) requestData.get("cout");
            Long enigmeId = Long.parseLong(requestData.get("enigmeId").toString());

            Optional<Enigme> enigmeOptional = enigmeRepository.findById(enigmeId);
            if (enigmeOptional.isPresent()) {
                Enigme enigme = enigmeOptional.get();
                Indice indice = new Indice();
                indice.setDescription(description);
                indice.setCout(cout);
                indice.setEnigme(enigme);
                indiceRepository.save(indice);
                return ResponseEntity.ok(indice);
            } else {
                return ResponseEntity.badRequest().body("Enigme non trouvée");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l'ajout de l'indice.");
        }
    }

    @PutMapping("/indices/update/{id}")
    @ResponseBody
    public ResponseEntity<Indice> updateIndice(@PathVariable Long id, @RequestBody Indice indiceDetails) {
        try {
            Optional<Indice> indiceOptional = indiceRepository.findById(id);
            if (indiceOptional.isPresent()) {
                Indice indice = indiceOptional.get();
                indice.setDescription(indiceDetails.getDescription());
                indice.setCout(indiceDetails.getCout());

                // Optionnel : Mettre à jour l'énigme si nécessaire
                if (indiceDetails.getEnigme() != null) {
                    Long newEnigmeId = indiceDetails.getEnigme().getId();
                    Optional<Enigme> newEnigmeOptional = enigmeRepository.findById(newEnigmeId);
                    if (newEnigmeOptional.isPresent()) {
                        indice.setEnigme(newEnigmeOptional.get());
                    } else {
                        return ResponseEntity.badRequest().build();
                    }
                }

                indiceRepository.save(indice);
                return ResponseEntity.ok(indice);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/indices/delete/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteIndice(@PathVariable Long id) {
        try {
            if (indiceRepository.existsById(id)) {
                indiceRepository.deleteById(id);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // *** Gestion des Énigmes ***
    @PostMapping("/enigmes/add")
    public ResponseEntity<?> addEnigme(@RequestBody Map<String, Object> requestData) {
        try {
            String titre = (String) requestData.get("titre");
            String description = (String) requestData.get("description");
            String reponse = (String) requestData.get("reponse");
            String niveau = (String) requestData.get("niveau");
            Long categorieId = Long.parseLong(requestData.get("categorieId").toString());

            Optional<Categorie> categorieOptional = categorieRepository.findById(categorieId);
            if (!categorieOptional.isPresent()) {
                return ResponseEntity.badRequest().body("Catégorie non trouvée.");
            }

            Enigme enigme = new Enigme();
            enigme.setTitre(titre);
            enigme.setDescription(description);
            enigme.setReponse(reponse);
            enigme.setNiveau(niveau);
            enigme.setCategorie(categorieOptional.get());

            enigmeRepository.save(enigme);
            return ResponseEntity.ok(enigme);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l'ajout de l'énigme.");
        }
    }

    @PutMapping("/enigmes/update/{id}")
    @ResponseBody
    public ResponseEntity<?> updateEnigme(@PathVariable Long id, @RequestBody Map<String, Object> requestData) {
        try {
            Optional<Enigme> enigmeOptional = enigmeRepository.findById(id);
            if (!enigmeOptional.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Enigme enigme = enigmeOptional.get();

            // Mise à jour des champs
            if (requestData.containsKey("titre")) {
                enigme.setTitre((String) requestData.get("titre"));
            }
            if (requestData.containsKey("description")) {
                enigme.setDescription((String) requestData.get("description"));
            }
            if (requestData.containsKey("reponse")) {
                enigme.setReponse((String) requestData.get("reponse"));
            }
            if (requestData.containsKey("niveau")) {
                enigme.setNiveau((String) requestData.get("niveau"));
            }
            if (requestData.containsKey("categorieId")) {
                Long categorieId = Long.parseLong(requestData.get("categorieId").toString());
                Optional<Categorie> categorieOptional = categorieRepository.findById(categorieId);
                if (!categorieOptional.isPresent()) {
                    return ResponseEntity.badRequest().body("Catégorie non trouvée.");
                }
                enigme.setCategorie(categorieOptional.get());
            }

            enigmeRepository.save(enigme);
            return ResponseEntity.ok(enigme);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/enigmes/delete/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteEnigme(@PathVariable Long id) {
        try {
            if (enigmeRepository.existsById(id)) {
                enigmeRepository.deleteById(id);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // *** Gestion des Catégories ***
    @PostMapping("/categories/add")
    public ResponseEntity<?> addCategorie(@RequestBody Categorie categorie) {
        try {
            categorieRepository.save(categorie);
            return ResponseEntity.ok(categorie);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l'ajout de la catégorie.");
        }
    }

    @PutMapping("/categories/update/{id}")
    @ResponseBody
    public ResponseEntity<Categorie> updateCategorie(@PathVariable Long id, @RequestBody Categorie categorieDetails) {
        try {
            Optional<Categorie> categorieOptional = categorieRepository.findById(id);
            if (categorieOptional.isPresent()) {
                Categorie categorie = categorieOptional.get();
                categorie.setNom(categorieDetails.getNom());
                categorieRepository.save(categorie);
                return ResponseEntity.ok(categorie);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/categories/delete/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteCategorie(@PathVariable Long id) {
        try {
            if (categorieRepository.existsById(id)) {
                categorieRepository.deleteById(id);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
