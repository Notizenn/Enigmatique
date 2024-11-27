package com.example.controllers;

import com.example.entities.*;
import com.example.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    @GetMapping("/data")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAllData() {
        try {
            Map<String, Object> data = new HashMap<>();
            
            // Remplir la carte des données avec des utilisateurs sans mot de passe et seulement les données nécessaires
            List<Map<String, Object>> utilisateurs = utilisateurRepository.findAll().stream().map(utilisateur -> {
                Map<String, Object> utilisateurData = new HashMap<>();
                utilisateurData.put("id", utilisateur.getId());
                utilisateurData.put("nom", utilisateur.getNom());
                utilisateurData.put("email", utilisateur.getEmail());
                utilisateurData.put("sous", utilisateur.getSous());
                utilisateurData.put("admin", utilisateur.isAdmin());
                return utilisateurData;
            }).collect(Collectors.toList());

            data.put("utilisateurs", utilisateurs);
            data.put("enigmes", enigmeRepository.findAll());
            data.put("categories", categorieRepository.findAll());
            data.put("indices", indiceRepository.findAll());
            data.put("statistiques", statistiqueRepository.findAll());

            return ResponseEntity.ok(data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }



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
    public ResponseEntity<?> addEnigme(@RequestBody Enigme enigme, @RequestParam(value = "categoriesIds", required = false) List<Long> categoriesIds) {
        try {
            if (enigme.getIndices() == null) {
                enigme.setIndices(new ArrayList<>());
            }
            if (categoriesIds != null && !categoriesIds.isEmpty()) {
                List<Categorie> categories = categorieRepository.findAllById(categoriesIds);
                enigme.setCategories(categories);
            } else {
                enigme.setCategories(new ArrayList<>());
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
                enigme.setCategories(enigmeDetails.getCategories());
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
