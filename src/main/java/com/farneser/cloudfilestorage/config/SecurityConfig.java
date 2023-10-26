package com.farneser.cloudfilestorage.config;

import com.farneser.cloudfilestorage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserService userService;
    private final PasswordConfig passwordConfig;

    @Autowired
    public SecurityConfig(UserService userDetailsService, PasswordConfig passwordConfig) {
        this.userService = userDetailsService;
        this.passwordConfig = passwordConfig;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(req -> {
                    req.requestMatchers("/register").permitAll();
                    req.requestMatchers(HttpMethod.GET, "/styles/**", "/scripts/**").permitAll();
                    req.anyRequest().authenticated();
                })
                .formLogin(login -> {
                    login.usernameParameter("username");
                    login.passwordParameter("password");
                    login.loginPage("/login");
                    login.defaultSuccessUrl("/");
                    login.permitAll();
                })
                .logout(LogoutConfigurer::permitAll)
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(HttpSecurity http) throws Exception {
        var authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder
                .userDetailsService(userService)
                .passwordEncoder(passwordConfig.passwordEncoder());

        return authenticationManagerBuilder.build();
    }
}