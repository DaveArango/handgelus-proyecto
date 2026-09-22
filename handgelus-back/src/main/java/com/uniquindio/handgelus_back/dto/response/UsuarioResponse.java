package com.uniquindio.handgelus_back.dto.response;

import com.uniquindio.handgelus_back.entity.Usuario;
import com.uniquindio.handgelus_back.enums.EstadoUsuario;
import com.uniquindio.handgelus_back.enums.Rol;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO de salida. Nunca incluye la contraseña, ni siquiera cifrada
 * (SWR-03: las credenciales no deben exponerse por la API).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponse {

    private Long id;
    private String nombre;
    private String correo;
    private String telefono;
    private Rol rol;
    private EstadoUsuario estado;
    private boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public static UsuarioResponse fromEntity(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .correo(usuario.getCorreo())
                .telefono(usuario.getTelefono())
                .rol(usuario.getRol())
                .estado(usuario.getEstado())
                .activo(usuario.isActivo())
                .fechaCreacion(usuario.getFechaCreacion())
                .fechaActualizacion(usuario.getFechaActualizacion())
                .build();
    }
}
