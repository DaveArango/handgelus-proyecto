package com.uniquindio.handgelus_back.dto.request;

import com.uniquindio.handgelus_back.enums.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO de entrada para creación de usuarios.
 * Ref. SWR-01: el administrador crea cuentas de asistente, tatuador/artista
 * y administrador asignando un rol único. También se reutiliza para el
 * autorregistro de clientes (RF-01), donde el rol se fuerza a CLIENTE
 * en el service, ignorando lo que venga en el DTO.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearUsuarioRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar 150 caracteres")
    private String nombre;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    @Size(max = 150, message = "El correo no puede superar 150 caracteres")
    private String correo;

    @Size(max = 30, message = "El teléfono no puede superar 30 caracteres")
    private String telefono;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String contrasena;

    @NotNull(message = "El rol es obligatorio")
    private Rol rol;
}
