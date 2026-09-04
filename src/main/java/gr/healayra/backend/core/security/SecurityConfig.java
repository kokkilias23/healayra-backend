package gr.healayra.backend.core.security;

import gr.healayra.backend.authentication.CustomUserDetailsService;
import gr.healayra.backend.core.security.filter.JwtAuthenticationFilter;
import gr.healayra.backend.core.security.handler.CustomAccessDeniedHandler;
import gr.healayra.backend.core.security.handler.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationProvider authenticationProvider
    ) throws Exception {

        http
                .cors(cors ->
                        cors.configurationSource(
                                corsConfigurationSource()
                        )
                )

                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        // Public endpoints
                        .requestMatchers(
                                "/api/auth/**",
                                "/error",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        .requestMatchers(
                                "/actuator/health"
                        ).permitAll()

                        // Current authenticated user
                        .requestMatchers(
                                "/api/users/me"
                        ).hasAnyRole(
                                "DOCTOR",
                                "CLIENT"
                        )

                        // Doctors can be viewed by both roles
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/doctors/**"
                        ).hasAnyRole(
                                "DOCTOR",
                                "CLIENT"
                        )

                        // Only doctors can modify doctor profiles
                        .requestMatchers(
                                "/api/doctors/**"
                        ).hasRole(
                                "DOCTOR"
                        )

                        // Availability can be viewed by both roles
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/availability/**"
                        ).hasAnyRole(
                                "DOCTOR",
                                "CLIENT"
                        )

                        // Only doctors can modify availability
                        .requestMatchers(
                                "/api/availability/**"
                        ).hasRole(
                                "DOCTOR"
                        )

                        // Client management is doctor-only
                        .requestMatchers(
                                "/api/clients/**"
                        ).hasRole(
                                "DOCTOR"
                        )

                        // Visit history is doctor-only
                        .requestMatchers(
                                "/api/visits/**"
                        ).hasRole(
                                "DOCTOR"
                        )

                        // Clinical notes are doctor-only
                        .requestMatchers(
                                "/api/notes/**"
                        ).hasRole(
                                "DOCTOR"
                        )

                        // CLIENT can create an appointment
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/appointments"
                        ).hasRole(
                                "CLIENT"
                        )

                        // CLIENT can see only their own appointments
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/appointments/me"
                        ).hasRole(
                                "CLIENT"
                        )

                        // Remaining appointment endpoints are doctor-only
                        .requestMatchers(
                                "/api/appointments/**"
                        ).hasRole(
                                "DOCTOR"
                        )

                        .anyRequest().authenticated()
                )

                .exceptionHandling(exception ->
                        exception
                                .authenticationEntryPoint(
                                        customAuthenticationEntryPoint
                                )
                                .accessDeniedHandler(
                                        customAccessDeniedHandler
                                )
                )

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authenticationProvider(
                        authenticationProvider
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(
            PasswordEncoder passwordEncoder
    ) {

        DaoAuthenticationProvider authenticationProvider =
                new DaoAuthenticationProvider(
                        customUserDetailsService
                );

        authenticationProvider.setPasswordEncoder(
                passwordEncoder
        );

        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of(
                        "http://localhost:5173"
                )
        );

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "PATCH",
                        "DELETE",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                List.of("*")
        );

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }
}