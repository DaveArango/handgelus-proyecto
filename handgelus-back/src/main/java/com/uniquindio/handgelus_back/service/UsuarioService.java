package com.uniquindio.handgelus_back.service;

import com.uniquindio.handgelus_back.dto.request.ActualizarUsuarioRequest;
import com.uniquindio.handgelus_back.dto.request.CambiarContrasenaRequest;
import com.uniquindio.handgelus_back.dto.request.CrearUsuarioRequest;
import com.uniquindio.handgelus_back.dto.response.UsuarioResponse;
import com.uniquindio.handgelus_back.enums.Rol;

import java.util.List;

public interface UsuarioService {

    /**
     * Crea una cuenta con rol único. Usado por el administrador para crear
     * asistente/tatuador-artista/administrador (SWR-01), y también por el
     * autorregistro de clientes (RF-01), donde el llamador debe forzar
     * rol = CLIENTE antes de invocar este método o usar registrarCliente().
     */
    UsuarioResponse crear(CrearUsuarioRequest request);

    /**
     * Autorregistro público de clientes (RF-01). Fuerza el rol a CLIENTE
     * sin importar lo que reciba el DTO, evitando que un cliente se
     * autoasigne un rol privilegiado.
     */
    UsuarioResponse registrarCliente(CrearUsuarioRequest request);

    UsuarioResponse obtenerPorId(Long id);

    List<UsuarioResponse> listarTodos();

    List<UsuarioResponse> listarPorRol(Rol rol);

    UsuarioResponse actualizar(Long id, ActualizarUsuarioRequest request);

    void cambiarContrasena(Long id, CambiarContrasenaRequest request);

    /**
     * Borrado lógico (activo = false) en lugar de DELETE físico, para
     * preservar trazabilidad de solicitudes/pagos/citas asociados.
     */
    void desactivar(Long id);

    void reactivar(Long id);
}
