package br.com.projeto.rapadura.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/index.html", "/style.css", "/script.js", "/login.html", "/admin.html", "/image/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/produtos", "/csrf", "/admin/status").permitAll()
                        .requestMatchers(HttpMethod.POST, "/login", "/logout", "/produtos").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/produtos/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/produtos/**").permitAll()
                        .anyRequest().denyAll())
                .build();
    }
}
