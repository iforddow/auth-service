package com.iforddow.authservice.common.security;

import com.iforddow.authservice.auth.repository.redis.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
* A configuration class for application security.
*
* @author IFD
* @since 2025-10-27
* */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final SessionRepository sessionRepository;

    /**
    * Bean implements a security filter chain, allowing certain routes
    * to be accessed.
    *
    * @author IFD
    * @since 2025-10-27
    * */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, SessionFilter sessionFilter) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((requests) ->
                        requests
                                .requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers("/actuator/**").permitAll()
                                .anyRequest().authenticated())
                .addFilterBefore(sessionFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
    * A password encoder for the app. Using Argon for strong
    * password hashing.
    *
    * @author IFD
    * @since 2025-10-27
    * */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }

    /**
    * A bean to create the authentication manager for the app.
    *
    * @author IFD
    * @since 2025-10-27
    * */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
    * A bean to initialize the SessionFilter.
    *
    * @author IFD
    * @since 2025-10-27
    * */
    @Bean
    public SessionFilter sessionFilter() {
        return new SessionFilter(sessionRepository);
    }

}
