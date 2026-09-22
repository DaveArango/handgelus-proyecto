package com.uniquindio.handgelus_back.service.impl;

import com.uniquindio.handgelus_back.dto.request.LoginRequest;
import com.uniquindio.handgelus_back.dto.response.LoginResponse;
import com.uniquindio.handgelus_back.dto.response.UsuarioResponse;
import com.uniquindio.handgelus_back.entity.Usuario;
import com.uniquindio.handgelus_back.repository.UsuarioRepository;
import com.uniquindio.handgelus_back.security.JwtService;
import com.uniquindio.handgelus_back.security.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

/**
 * Orquesta el proceso de login (SWR-02): delega la verificación de
 * credenciales al AuthenticationManager (que usa bcrypt internamente vía
 * DaoAuthenticationProvider) y, si es exitosa, emite el JWT correspondiente.
 */
@Service
@RequiredArgsConstructor
public class AutenticacionService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {
        // Si las credenciales son inválidas, lanza BadCredentialsException,
        // capturada centralizadamente por GlobalExceptionHandler.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getCorreo(), request.getContrasena())
        );

        Usuario usuario = usuarioRepository.findByCorreo(request.getCorreo())
                .orElseThrow(() -> new IllegalStateException("Usuario autenticado no encontrado en base de datos"));

        UsuarioPrincipal principal = new UsuarioPrincipal(usuario);
        String token = jwtService.generarToken(principal);

        return LoginResponse.builder()
                .token(token)
                .tipo("Bearer")
                .usuario(UsuarioResponse.fromEntity(usuario))
                .build();
    }
}
