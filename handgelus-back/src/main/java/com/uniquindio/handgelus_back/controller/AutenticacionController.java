package com.uniquindio.handgelus_back.controller;

import com.uniquindio.handgelus_back.dto.request.CrearUsuarioRequest;
import com.uniquindio.handgelus_back.dto.request.LoginRequest;
import com.uniquindio.handgelus_back.dto.response.LoginResponse;
import com.uniquindio.handgelus_back.dto.response.UsuarioResponse;
import com.uniquindio.handgelus_back.service.UsuarioService;
import com.uniquindio.handgelus_back.service.impl.AutenticacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints públicos de autenticación y autorregistro.
 * Ref. F-01 (RF-01, SWR-02): un cliente puede registrarse por sí mismo,
 * y cualquier usuario puede iniciar sesión con sus credenciales únicas.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AutenticacionController {

    private final AutenticacionService autenticacionService;
    private final UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(autenticacionService.login(request));
    }

    /**
     * Autorregistro de clientes (RF-01). El rol se fuerza a CLIENTE en el
     * service sin importar lo que llegue en el body, por seguridad.
     */
    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponse> registrarCliente(@Valid @RequestBody CrearUsuarioRequest request) {
        UsuarioResponse creado = usuarioService.registrarCliente(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }
}
