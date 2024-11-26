package com.example.service;

import com.example.entities.Utilisateur;
import com.example.repository.UtilisateurRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailService.class);

    private final UtilisateurRepository utilisateurRepository;

    public CustomUserDetailService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if (email == null || email.isEmpty()) {
            throw new UsernameNotFoundException("L'email ne peut pas être vide");
        }

        logger.info("Recherche de l'utilisateur avec l'email : {}", email);

        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'email : " + email));

        return org.springframework.security.core.userdetails.User.builder()
                .username(utilisateur.getEmail()) // Utilisez `email` comme nom d'utilisateur
                .password(utilisateur.getMotDePasse())
                .roles(utilisateur.isAdmin() ? "ADMIN" : "USER")
                .build();
    }

}
