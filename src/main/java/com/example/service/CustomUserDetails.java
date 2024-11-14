package com.example.service;

import com.example.entities.Utilisateur;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;


public class CustomUserDetails implements UserDetails {

    private Utilisateur utilisateur;
    
    // Constructeur
    public CustomUserDetails(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.createAuthorityList("USER");  // Ajouter les rôles si nécessaire
    }

    @Override
    public String getPassword() {
        return utilisateur.getMotDePasse();  // Retourner le mot de passe haché
    }

    @Override
    public String getUsername() {
        return utilisateur.getEmail();  // Retourner l'email comme identifiant
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
