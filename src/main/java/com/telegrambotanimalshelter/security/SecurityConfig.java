package com.telegrambotanimalshelter.security;

import com.telegrambotanimalshelter.services.volunteerservice.VolunteerSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.PasswordManagementDsl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.PasswordManagementConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.net.PasswordAuthentication;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final VolunteerSecurityService volunteerSecurityService;

    @Autowired
    public SecurityConfig(VolunteerSecurityService volunteerSecurityService) {
        this.volunteerSecurityService = volunteerSecurityService;
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .userDetailsService(volunteerSecurityService)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {

                    auth.requestMatchers(
                            "/dogs",
                            "/dogs" + "/{id}",
                            "/dogs" + "/put",
                            "/dogs" + "/photo" + "/{name}").hasAnyRole("VOLUNTEER");

                    auth.requestMatchers("/cats",
                            "/cats" + "/{id}",
                            "/cats" + "/put",
                            "/cats" + "/photo" + "/{name}").hasAnyRole("VOLUNTEER");

                })
                .httpBasic(Customizer.withDefaults())
                .build();

    }


}
