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

    @SuppressWarnings({ "deprecation", "removal" })
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeRequests(requests -> requests
                .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN") // Accès réservé aux administrateurs
                .requestMatchers("/login", "/register", "/home").permitAll() // Accès public aux pages spécifiques
                .requestMatchers("/css/**", "/js/**", "/img/**", "/static/**").permitAll() // Accès public aux ressources statiques
                .requestMatchers("/api/utilisateurs/**").permitAll() // Accès public à l'API utilisateur
                .requestMatchers("/h2-console/**", "/").permitAll() // Accès public à la console H2
                .anyRequest().authenticated()) // Toute autre page nécessite une connexion
            .csrf(csrf -> csrf.disable()) // Désactiver CSRF (nécessaire pour la console H2)
            .headers(headers -> headers
                .frameOptions().disable()) // Désactiver X-Frame-Options pour afficher la console H2 dans des frames
            .formLogin(login -> login
                .loginPage("/login")
                .loginProcessingUrl("/perform_login")
                .usernameParameter("email")
                .passwordParameter("motDePasse")
                .defaultSuccessUrl("/home", true)
                .failureUrl("/login?error=true"))
            .logout(logout -> logout
                .logoutUrl("/perform_logout")
                .logoutSuccessUrl("/login?logout=true")
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
