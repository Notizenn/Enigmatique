package com.example.security;

import com.example.service.CustomUserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailService userDetailsService;

    public SecurityConfig(CustomUserDetailService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(requests -> requests
                .requestMatchers("/admin/**").hasRole("ADMIN") // Accès réservé aux administrateurs
                .requestMatchers("/login", "/register", "/home", "/").permitAll() // Pages publiques
                .requestMatchers("/css/**", "/js/**", "/img/**", "/static/**").permitAll() // Ressources statiques accessibles à tous
                .anyRequest().authenticated()) // Toute autre page nécessite une authentification
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/admin/utilisateurs/delete/**") // Désactiver CSRF pour DELETE
                .disable()) // Désactiver CSRF globalement si nécessaire
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable())) // Autoriser la console H2
            .formLogin(login -> login
                .loginPage("/login") // Page de connexion personnalisée
                .loginProcessingUrl("/perform_login")
                .usernameParameter("email")
                .passwordParameter("motDePasse")
                .defaultSuccessUrl("/home", true) // Redirection après connexion
                .failureUrl("/login?error=true")) // En cas d'erreur de connexion
            .logout(logout -> logout
                .logoutUrl("/perform_logout")
                .logoutSuccessUrl("/login?logout=true") // Redirection après déconnexion
                .deleteCookies("JSESSIONID"));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }
}
