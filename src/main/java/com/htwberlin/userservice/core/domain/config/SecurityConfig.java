package com.htwberlin.userservice.core.domain.config;

import com.htwberlin.userservice.core.domain.model.Role;
import com.htwberlin.userservice.core.domain.service.interfaces.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
  @Value("${keycloak.realm}")
  private String kcRealm;

  private IUserService userService;

  public SecurityConfig(IUserService userService) {
    this.userService = userService;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .cors().disable()
        .csrf().disable()
        .authorizeHttpRequests((authorize) -> authorize
            .requestMatchers("/v1/user/cart", "/v1/user/login")
            .permitAll()
            .anyRequest()
            .authenticated()
        )
        .oauth2Login()
        .and()
        .oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer
            .jwt(jwt -> jwt
                .jwtAuthenticationConverter(this.userService.getJwtAuthenticationConverter())
            )
        );

    return http.build();
  }
}