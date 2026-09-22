package com.uniquindio.handgelus_back.config;

import com.uniquindio.handgelus_back.security.JwtAuthenticationFilter;
import com.uniquindio.handgelus_back.security.UsuarioDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Configuración central de seguridad.
 *
 * Ref. SWR-03: "Las contraseñas ... deben almacenarse cifradas con bcrypt.
 * La comunicación debe realizarse exclusivamente por HTTPS."
 *  - El cifrado bcrypt se aplica aquí vía BCryptPasswordEncoder.
 *  - La exigencia de HTTPS se aplica a nivel de despliegue (terminación TLS
 *    en el balanceador/servidor cloud, ver RNF-06); adicionalmente se
 *    puede forzar con requiresChannel(...).requiresSecure() cuando el
 *    proxy reenvía el esquema original (ver comentario más abajo).
 *
 * Ref. SWR-02: control de acceso por rol mediante authorizeHttpRequests
 * y @PreAuthorize en los controllers (EnableMethodSecurity).
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UsuarioDetailsService usuarioDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public PasswordEncoder passwordEncoder() {
        // SWR-03: cifrado de contraseñas con bcrypt.
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        // A partir de Spring Security 6.3, DaoAuthenticationProvider recibe
        // el UserDetailsService por constructor en lugar de un setter.
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(usuarioDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable()) // API stateless con JWT, no usa cookies de sesión
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos: login y autorregistro de clientes (RF-01)
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // SWR-01: solo el administrador crea cuentas internas
                        .requestMatchers("/api/usuarios/**").hasRole("ADMINISTRADOR")
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // Si el despliegue termina TLS en la propia app (no recomendado en
        // cloud detrás de balanceador), se puede forzar HTTPS así:
        // http.requiresChannel(channel -> channel.anyRequest().requiresSecure());

        return http.build();
    }
}