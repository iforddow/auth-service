package com.iforddow.authservice.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

/**
* A class to manage the CORS configuration of
* the auth-service app.
*
* @author IFD
* @since 2025-10-27
* */
@Configuration
public class CorsConfig {

    @Value("${cors.allowed.origins}")
    private String allowedOrigins;

    @Value("${cors.allowed.headers}")
    private String allowedHeaders;

    @Value("${cors.allowed.methods}")
    private String allowedMethods;

    @Value("${cors.allow.credentials}")
    private Boolean allowCredentials;

    /**
    * Bean to create apps cors configuration.
    *
    * @author IFD
    * @since 2025-10-27
    * */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.addAllowedOriginPattern(allowedOrigins);
        corsConfiguration.addAllowedHeader(allowedHeaders);
        corsConfiguration.addAllowedMethod(allowedMethods);
        corsConfiguration.setAllowCredentials(allowCredentials);

        UrlBasedCorsConfigurationSource  urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);

        return urlBasedCorsConfigurationSource;

    }

}
