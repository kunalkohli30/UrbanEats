package com.urbaneats.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
//@EnableWebSecurity
public class AppConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http ) throws Exception{

        System.out.println("-------------- appconfig---------------");
        http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/api/admin/**").hasAnyRole("RESTAURANT_OWNER", "ADMIN")
                        .requestMatchers("/api/**").authenticated()
                )
                // Add the token filter conditionally only for authenticated endpoints
                .addFilterBefore(new FirebaseTokenFilter(), BasicAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        return http.build();
    }
//    @Bean
//    SecurityFilterChain securityFilterChain(HttpSecurity http ) throws Exception{
//        http
//                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests(Authorize -> Authorize
//                        .requestMatchers("/auth/**").permitAll()
//                        .requestMatchers("/api/admin/**").hasAnyRole("RESTAURANT_OWNER", "ADMIN")
//                        .requestMatchers("/api/**").authenticated()
//                        .requestMatchers("/error").permitAll()
//                        .anyRequest().authenticated()
//                )
////                .addFilterBefore(new JwtTokenValidator(), BasicAuthenticationFilter.class)
//                .addFilterBefore(new FirebaseTokenFilter(), BasicAuthenticationFilter.class)
//                .csrf(AbstractHttpConfigurer::disable)
//                .cors(cors -> cors.configurationSource(corsConfigurationSource()));
//
//        return http.build();
//    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        return new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration cfg = new CorsConfiguration();
                cfg.setAllowedOrigins(Arrays.asList(
                        "https://zosh-food.vercel.app",
                        "http://localhost:5173",
                        "http://127.0.0.1:5173",
                        "https://urban-eats-1.web.app"
                ));
                cfg.setAllowedMethods(Collections.singletonList("*"));
                cfg.setAllowCredentials(true);
                cfg.setAllowedHeaders(Collections.singletonList("*"));
                cfg.setExposedHeaders(Arrays.asList("Authorization"));
                cfg.setMaxAge(3600L);

                return cfg;
            }
        };
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
