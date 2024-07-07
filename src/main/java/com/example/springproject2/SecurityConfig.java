package com.example.springproject2;

import com.example.service.CustomReactiveUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import reactor.core.publisher.Mono;
import java.util.Collection;

@Configuration
@EnableTransactionManagement
@EnableWebFluxSecurity
public class SecurityConfig {
    @Autowired
    private JwtUtil jwtUtil;

    private final CustomReactiveUserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig (CustomReactiveUserDetailsService userDetailsService){
        this.userDetailsService = userDetailsService;
    }

     @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/authenticate").permitAll()
                        .anyExchange().authenticated())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .addFilterAt(jwtAuthenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION);
        return http.build();
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager() {
        UserDetailsRepositoryReactiveAuthenticationManager authenticationManager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        authenticationManager.setPasswordEncoder(passwordEncoder());
        return authenticationManager;
    }

    @Bean
    public ReactiveAuthenticationManager jwtAuthenticationManager() {
        return authentication -> {
            String token = (String) authentication.getCredentials();
            try {
                if (token != null && jwtUtil.validateToken(token, jwtUtil.extractUsername(token))) {
                    String username = jwtUtil.extractUsername(token);
                    Collection<GrantedAuthority> authorities = jwtUtil.getAuthorities(token);
                    return Mono.just(new UsernamePasswordAuthenticationToken(username, null, authorities));
                } else {
                    return Mono.error(new RuntimeException("Unauthorized"));
                }
            } catch (Exception e) {
                return Mono.error(new RuntimeException("JWT validation error", e));
            }
        };
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    private AuthenticationWebFilter jwtAuthenticationWebFilter() {
        AuthenticationWebFilter jwtAuthenticationWebFilter = new AuthenticationWebFilter(jwtAuthenticationManager());
        jwtAuthenticationWebFilter.setServerAuthenticationConverter(new JwtAuthenticationConverter(jwtUtil));
        jwtAuthenticationWebFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/api/**","/v1/**"));
        return jwtAuthenticationWebFilter;
    }
}


