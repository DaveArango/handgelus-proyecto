package com.uniquindio.handgelus_back.controller;

import com.uniquindio.handgelus_back.dto.request.ActualizarUsuarioRequest;
import com.uniquindio.handgelus_back.dto.request.CambiarContrasenaRequest;
import com.uniquindio.handgelus_back.dto.request.CrearUsuarioRequest;
import com.uniquindio.handgelus_back.dto.response.UsuarioResponse;
import com.uniquindio.handgelus_back.enums.Rol;
import com.uniquindio.handgelus_back.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CRUD de gestión de usuarios — F-01.
 *
 * Ref. SWR-01: "El sistema debe permitir al administrador crear cuentas de
 * asistente, tatuador/artista y administrador, asignando un rol único a
 * cada cuenta." Por eso todo este controller está restringido al rol
 * ADMINISTRADOR, tanto a nivel de SecurityConfig (/api/usuarios/**) como
 * con @PreAuthorize explícito por método (defensa en profundidad).
 */
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody CrearUsuarioRequest request) {
        UsuarioResponse creado = usuarioService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listarTodos(
            @RequestParam(required = false) Rol rol) {
        List<UsuarioResponse> usuarios = (rol != null)
                ? usuarioService.listarPorRol(rol)
                : usuarioService.listarTodos();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> actualizar(@PathVariable Long id,
                                                      @Valid @RequestBody ActualizarUsuarioRequest request) {
        return ResponseEntity.ok(usuarioService.actualizar(id, request));
    }

    @PatchMapping("/{id}/contrasena")
    public ResponseEntity<Void> cambiarContrasena(@PathVariable Long id,
                                                  @Valid @RequestBody CambiarContrasenaRequest request) {
        usuarioService.cambiarContrasena(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        usuarioService.desactivar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<Void> reactivar(@PathVariable Long id) {
        usuarioService.reactivar(id);
        return ResponseEntity.noContent().build();
    }
}
