package com.ifverde.config;

import com.ifverde.repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests((requests) -> requests
                                                .requestMatchers("/css/**", "/javascript/**", "/img/**", "/login",
                                                                "/cadastro", "/usuario/registrar")
                                                .permitAll()
                                                .anyRequest().authenticated())
                                .formLogin((form) -> form
                                                .loginPage("/login")
                                                .defaultSuccessUrl("/", true)
                                                .permitAll())
                                .logout((logout) -> logout
                                                .logoutSuccessUrl("/login?logout")
                                                .permitAll());

                return http.build();
        }

        @Bean
        public UserDetailsService userDetailsService(UsuarioRepository usuarioRepository) {
                return username -> usuarioRepository.findByUsername(username)
                                .map(usuario -> org.springframework.security.core.userdetails.User.builder()
                                                .username(usuario.getUsername())
                                                .password(usuario.getPassword())
                                                .roles("USER")
                                                .build())
                                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}