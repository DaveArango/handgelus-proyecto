package com.uniquindio.handgelus_back.service.impl;


import com.uniquindio.handgelus_back.dto.request.ActualizarUsuarioRequest;
import com.uniquindio.handgelus_back.dto.request.CambiarContrasenaRequest;
import com.uniquindio.handgelus_back.dto.request.CrearUsuarioRequest;
import com.uniquindio.handgelus_back.dto.response.UsuarioResponse;
import com.uniquindio.handgelus_back.entity.Usuario;
import com.uniquindio.handgelus_back.enums.EstadoUsuario;
import com.uniquindio.handgelus_back.enums.Rol;
import com.uniquindio.handgelus_back.exception.RecursoNoEncontradoException;
import com.uniquindio.handgelus_back.exception.ReglaNegocioException;
import com.uniquindio.handgelus_back.repository.UsuarioRepository;
import com.uniquindio.handgelus_back.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación de la lógica de negocio de F-01 "Gestionar usuarios del
 * sistema": creación con rol único (SWR-01), unicidad de correo (RF-01),
 * cifrado de contraseña (SWR-03) y borrado lógico.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UsuarioResponse crear(CrearUsuarioRequest request) {
        return crearConRol(request, request.getRol());
    }

    @Override
    public UsuarioResponse registrarCliente(CrearUsuarioRequest request) {
        // RF-01: el autorregistro siempre crea un CLIENTE, sin importar
        // qué rol venga en el payload (evita escalamiento de privilegios).
        return crearConRol(request, Rol.CLIENTE);
    }

    private UsuarioResponse crearConRol(CrearUsuarioRequest request, Rol rolAsignado) {
        // RF-01: "El correo debe ser único en el sistema".
        if (usuarioRepository.existsByCorreo(request.getCorreo())) {
            throw new ReglaNegocioException("Ya existe un usuario registrado con el correo: " + request.getCorreo());
        }

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .correo(request.getCorreo())
                .telefono(request.getTelefono())
                // SWR-03: la contraseña nunca se persiste en texto plano.
                .contrasena(passwordEncoder.encode(request.getContrasena()))
                .rol(rolAsignado)
                .estado(EstadoUsuario.ACTIVO)
                .activo(true)
                .build();

        Usuario guardado = usuarioRepository.save(usuario);
        return UsuarioResponse.fromEntity(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse obtenerPorId(Long id) {
        return UsuarioResponse.fromEntity(buscarActivoOExplotar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarPorRol(Rol rol) {
        return usuarioRepository.findByRolAndActivoTrue(rol).stream()
                .map(UsuarioResponse::fromEntity)
                .toList();
    }

    @Override
    public UsuarioResponse actualizar(Long id, ActualizarUsuarioRequest request) {
        Usuario usuario = buscarActivoOExplotar(id);

        if (request.getCorreo() != null && !request.getCorreo().isBlank()) {
            if (usuarioRepository.existsByCorreoAndIdNot(request.getCorreo(), id)) {
                throw new ReglaNegocioException("Ya existe otro usuario con el correo: " + request.getCorreo());
            }
            usuario.setCorreo(request.getCorreo());
        }
        if (request.getNombre() != null && !request.getNombre().isBlank()) {
            usuario.setNombre(request.getNombre());
        }
        if (request.getTelefono() != null) {
            usuario.setTelefono(request.getTelefono());
        }
        if (request.getRol() != null) {
            // SWR-01: solo el administrador puede llegar a este endpoint
            // (restringido en el controller/SecurityConfig), por lo que
            // reasignar el rol único aquí es seguro.
            usuario.setRol(request.getRol());
        }
        if (request.getEstado() != null) {
            usuario.setEstado(request.getEstado());
        }

        return UsuarioResponse.fromEntity(usuarioRepository.save(usuario));
    }

    @Override
    public void cambiarContrasena(Long id, CambiarContrasenaRequest request) {
        Usuario usuario = buscarActivoOExplotar(id);

        if (!passwordEncoder.matches(request.getContrasenaActual(), usuario.getContrasena())) {
            throw new ReglaNegocioException("La contraseña actual no es correcta");
        }

        usuario.setContrasena(passwordEncoder.encode(request.getContrasenaNueva()));
        usuarioRepository.save(usuario);
    }

    @Override
    public void desactivar(Long id) {
        Usuario usuario = buscarActivoOExplotar(id);
        usuario.setActivo(false);
        usuario.setEstado(EstadoUsuario.INACTIVO);
        usuarioRepository.save(usuario);
    }

    @Override
    public void reactivar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un usuario con id: " + id));
        usuario.setActivo(true);
        usuario.setEstado(EstadoUsuario.ACTIVO);
        usuarioRepository.save(usuario);
    }

    private Usuario buscarActivoOExplotar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un usuario con id: " + id));
        if (!usuario.isActivo()) {
            throw new RecursoNoEncontradoException("El usuario con id " + id + " se encuentra desactivado");
        }
        return usuario;
    }
}
