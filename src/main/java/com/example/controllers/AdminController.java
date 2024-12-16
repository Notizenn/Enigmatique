package com.example.controllers;

import com.example.entities.Categorie;
import com.example.entities.Enigme;
import com.example.entities.Indice;
import com.example.entities.Utilisateur;
import com.example.repository.CategorieRepository;
import com.example.repository.EnigmeRepository;
import com.example.repository.IndiceRepository;
import com.example.repository.UtilisateurRepository;

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
    private IndiceRepository indiceRepository;

    @Autowired
    private EnigmeRepository enigmeRepository;

    @Autowired
    private CategorieRepository categorieRepository;

    // Page d'administration générale
    @GetMapping
    public String adminPage(Model model) {
        model.addAttribute("utilisateurs", utilisateurRepository.findAll());
        model.addAttribute("indices", indiceRepository.findAll());
        model.addAttribute("enigmes", enigmeRepository.findAll());
        model.addAttribute("categories", categorieRepository.findAll());
        return "admin";
    }

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
                utilisateurData.put("admin", utilisateur.isAdmin());
                return utilisateurData;
            }).collect(Collectors.toList());

            List<Map<String, Object>> indices = indiceRepository.findAll().stream().map(indice -> {
                Map<String, Object> indiceData = new HashMap<>();
                indiceData.put("id", indice.getId());
                indiceData.put("description", indice.getDescription());
                indiceData.put("enigmeId", indice.getEnigme().getId());
                indiceData.put("enigmeTitre", indice.getEnigme().getTitre());
                return indiceData;
            }).collect(Collectors.toList());

            data.put("utilisateurs", utilisateurs);
            data.put("enigmes", enigmeRepository.findAll());
            data.put("categories", categorieRepository.findAll());
            data.put("indices", indices);

            return ResponseEntity.ok(data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/utilisateurs/add")
    public ResponseEntity<?> addUtilisateur(@RequestBody Utilisateur utilisateur) {
        try {
            if (utilisateurRepository.findByEmail(utilisateur.getEmail()).isPresent()) {
                return ResponseEntity.badRequest().body("L'email existe déjà.");
            }

            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            utilisateur.setMotDePasse(passwordEncoder.encode(utilisateur.getMotDePasse()));

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

    @PostMapping("/indices/add")
    public ResponseEntity<?> addIndice(@RequestBody Map<String, Object> requestData) {
        try {
            String description = (String) requestData.get("description");
            Long enigmeId = Long.parseLong(requestData.get("enigmeId").toString());

            Optional<Enigme> enigmeOptional = enigmeRepository.findById(enigmeId);
            if (enigmeOptional.isPresent()) {
                Enigme enigme = enigmeOptional.get();
                Indice indice = new Indice();
                indice.setDescription(description);
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

    @PostMapping("/enigmes/add")
    public ResponseEntity<?> addEnigme(@RequestBody Enigme enigme) {
        try {
            // Si la liste d'indices est nulle, on la remplace par une liste vide
            if (enigme.getIndices() == null) {
                enigme.setIndices(new ArrayList<>());
            }

            // Vérifier que le niveau n'est pas vide
            if (enigme.getNiveau() == null || enigme.getNiveau().isEmpty()) {
                return ResponseEntity.badRequest().body("Le champ niveau est obligatoire.");
            }

            // Vérifier et charger la catégorie si une catégorie est spécifiée
            if (enigme.getCategorie() != null && enigme.getCategorie().getId() != null) {
                Optional<Categorie> categorieOptional = categorieRepository.findById(enigme.getCategorie().getId());
                if (categorieOptional.isEmpty()) {
                    return ResponseEntity.badRequest().body("Catégorie non trouvée");
                }
                enigme.setCategorie(categorieOptional.get());
            }

            enigmeRepository.save(enigme);
            return ResponseEntity.ok(enigme);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l'ajout de l'énigme.");
        }
    }

    @PutMapping("/enigmes/update/{id}")
    @ResponseBody
    public ResponseEntity<Enigme> updateEnigme(@PathVariable Long id, @RequestBody Enigme enigmeDetails) {
        try {
            Optional<Enigme> enigmeOptional = enigmeRepository.findById(id);
            if (enigmeOptional.isPresent()) {
                Enigme enigme = enigmeOptional.get();
                enigme.setTitre(enigmeDetails.getTitre());
                enigme.setDescription(enigmeDetails.getDescription());
                enigme.setReponse(enigmeDetails.getReponse());
                enigme.setNiveau(enigmeDetails.getNiveau());

                // Vérifier le niveau
                if (enigme.getNiveau() == null || enigme.getNiveau().isEmpty()) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }

                // Gérer la catégorie
                if (enigmeDetails.getCategorie() != null && enigmeDetails.getCategorie().getId() != null) {
                    Optional<Categorie> catOpt = categorieRepository.findById(enigmeDetails.getCategorie().getId());
                    if (catOpt.isEmpty()) {
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }
                    enigme.setCategorie(catOpt.get());
                }

                enigmeRepository.save(enigme);
                return ResponseEntity.ok(enigme);
            }
            return ResponseEntity.notFound().build();
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
