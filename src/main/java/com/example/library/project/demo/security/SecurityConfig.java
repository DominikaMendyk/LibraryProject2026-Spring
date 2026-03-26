package com.example.library.project.demo.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;


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
        // Most important part of security in spring!
        return http.addFilterBefore(new JWTTokenFilter(jwtTokenService), UsernamePasswordAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException)-> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                        }))
                .authorizeHttpRequests(
                        authorizationManagerRequestMatcherRegistry ->
                                authorizationManagerRequestMatcherRegistry
                                        .requestMatchers("/login").permitAll()
                                        .requestMatchers("/user/add").permitAll()
                                        .requestMatchers("/user/who-am-i").permitAll()
                                        .requestMatchers("/user/**").hasRole("LIBRARIAN")
                                        .requestMatchers("/book/add").hasRole("LIBRARIAN")
                                        .requestMatchers("/book/remove/**").hasRole("LIBRARIAN")
                                        .requestMatchers("/book/details").hasRole("READER")
                                        .requestMatchers("/book/**").authenticated()
                                        .requestMatchers("/test").hasRole("USER")
                                        .anyRequest().authenticated()
                )
                .sessionManagement(httpSecuritySessionManagementConfigurer ->
                        httpSecuritySessionManagementConfigurer
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }
}
