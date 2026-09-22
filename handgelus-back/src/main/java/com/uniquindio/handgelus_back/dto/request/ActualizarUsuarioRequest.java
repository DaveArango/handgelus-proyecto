package com.uniquindio.handgelus_back.dto.request;

import com.uniquindio.handgelus_back.enums.EstadoUsuario;
import com.uniquindio.handgelus_back.enums.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO de entrada para actualización de usuarios (RF-16 / SWR-01/02).
 * Todos los campos son opcionales: solo se actualizan los que llegan
 * distintos de null (actualización parcial tipo PATCH).
 * La contraseña se actualiza mediante un endpoint separado por seguridad.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActualizarUsuarioRequest {

    @Size(max = 150, message = "El nombre no puede superar 150 caracteres")
    private String nombre;

    @Email(message = "El correo no tiene un formato válido")
    @Size(max = 150, message = "El correo no puede superar 150 caracteres")
    private String correo;

    @Size(max = 30, message = "El teléfono no puede superar 30 caracteres")
    private String telefono;

    private Rol rol;

    private EstadoUsuario estado;
}
