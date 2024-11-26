package com.example.service;

import com.example.entities.Utilisateur;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private final Utilisateur utilisateur;

    // Constructor
    public CustomUserDetails(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    // Get Authorities: Assign ROLE_ADMIN if utilisateur is admin, otherwise ROLE_USER
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = utilisateur.isAdmin() ? "ROLE_ADMIN" : "ROLE_USER";
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    // Get Password: Return encoded password
    @Override
    public String getPassword() {
        return utilisateur.getMotDePasse();
    }

    // Get Username: Return email as username
    @Override
    public String getUsername() {
        return utilisateur.getEmail();
    }

    // Account is not expired
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // Account is not locked
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // Credentials are not expired
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // Account is enabled
    @Override
    public boolean isEnabled() {
        return true;
    }

    // Get Utilisateur: Additional getter to access utilisateur details if needed
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }
}
