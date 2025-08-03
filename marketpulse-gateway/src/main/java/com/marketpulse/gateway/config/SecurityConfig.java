package com.marketpulse.gateway.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        http.authorizeExchange(auth -> auth
            .pathMatchers("/actuator/**").permitAll()
            .pathMatchers("/auth/**").permitAll()
            .pathMatchers("/api/**").authenticated()
            .anyExchange().authenticated()
        )
        .csrf(csrf -> csrf.disable())
        .cors(cors -> cors.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // This is used to hash passwords (we'll use it later)
        return new BCryptPasswordEncoder();
    }
}
