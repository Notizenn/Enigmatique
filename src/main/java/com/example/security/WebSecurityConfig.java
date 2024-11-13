package com.example.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {

    @SuppressWarnings("deprecation")
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeRequests(authorize -> authorize
                .requestMatchers("/login", "/css/**", "/js/**", "/h2-console/**").permitAll() // Autoriser l'accès à H2
                .anyRequest().authenticated() // Protéger toutes les autres pages
            )
            .formLogin(form -> form
                .loginPage("/login") // Spécifie votre page de login personnalisée
                .permitAll()
            )
            .logout(logout -> logout
                .permitAll()
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**") // Désactiver CSRF pour la console H2
            )
            .headers(headers -> headers
                .frameOptions().disable() // Permettre l'affichage de la console H2 dans un iframe
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
