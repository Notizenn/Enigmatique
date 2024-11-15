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
                    .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN") // Protéger les routes /admin
                    .requestMatchers("/login", "/home", "/api/**").permitAll() // Permettre l'accès à certaines routes publiques
                    .anyRequest().authenticated()) // Exiger l'authentification pour toute autre route
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers
                    .frameOptions().disable())
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
