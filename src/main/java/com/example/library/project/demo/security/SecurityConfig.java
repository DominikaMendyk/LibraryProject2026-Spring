package com.example.library.project.demo.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenService jwtTokenService;

    @Autowired
    public SecurityConfig(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        AccessDeniedHandler accessDeniedHandler = (request, response, accessDeniedException) -> {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Your role does not allow performing this action");
        };
        // Most important part of security in spring!
        return http.addFilterBefore(new JWTTokenFilter(jwtTokenService), UsernamePasswordAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(apiConfigurationSource()))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .authorizeHttpRequests(
                        authorizationManagerRequestMatcherRegistry ->
                                authorizationManagerRequestMatcherRegistry
                                        .requestMatchers("/login").permitAll()
                                        .requestMatchers("/user/add").hasRole("LIBRARIAN")
                                        .requestMatchers("/user/borrow/").hasRole("READER")
                                        .requestMatchers("/user/return/").hasRole("READER")
                                        //later change all to "/smth/*/**.... for LIBRARIAN and "/smth/...." to READER
                                        .requestMatchers("/user/*/accumulated-credit").hasRole("LIBRARIAN")
                                        .requestMatchers("/user/*/active-overdue-credit").hasRole("LIBRARIAN")
                                        .requestMatchers("/user/*/total-credit").hasRole("LIBRARIAN")
                                        .requestMatchers("/user/my-accumulated-credit").hasRole("READER")
                                        .requestMatchers("/user/my-active-overdue-credit").hasRole("READER")
                                        .requestMatchers("/user/my-total-credit").hasRole("READER")

                                        .requestMatchers("/user/who-am-i").permitAll()
                                        .requestMatchers("/user/me").permitAll()
                                        .requestMatchers("/user/update-email").permitAll()
                                        .requestMatchers("/user/update/**").hasAnyRole("LIBRARIAN", "READER")
                                        .requestMatchers("/user/my-currently-borrowed").hasRole("READER")
                                        .requestMatchers("/user/my-loan-history").hasRole("READER")
                                        .requestMatchers("/user/**").hasRole("LIBRARIAN")
                                        .requestMatchers("/book/add").hasRole("LIBRARIAN")
                                        .requestMatchers("/book/remove/**").hasRole("LIBRARIAN")
                                        .requestMatchers("/book/details").hasRole("READER")
                                        .requestMatchers("/book/**").authenticated()
                                        .requestMatchers("/review/**").hasAnyRole("LIBRARIAN", "READER")
                                        .requestMatchers("/loan/user/*/current").hasRole("READER")
                                        .requestMatchers("/loan/user/*/history").hasRole("READER")
                                        .requestMatchers("/test").hasRole("USER")
                                        .anyRequest().authenticated()
                )
                .sessionManagement(httpSecuritySessionManagementConfigurer ->
                        httpSecuritySessionManagementConfigurer
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    @Bean
    public CorsConfigurationSource apiConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://localhost:8080"
        ));
        configuration.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS"
        ));

        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}


