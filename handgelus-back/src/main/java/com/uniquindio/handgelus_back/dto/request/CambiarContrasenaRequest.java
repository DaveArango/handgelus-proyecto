package com.uniquindio.handgelus_back.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para el cambio de contraseña de un usuario (SWR-03).
 * Se separa del update general de datos para poder exigir la contraseña
 * actual y aplicar reglas de validación específicas sin mezclarlas con
 * la actualización de datos de perfil.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CambiarContrasenaRequest {

    @NotBlank(message = "Debe indicar la contraseña actual")
    private String contrasenaActual;

    @NotBlank(message = "Debe indicar la nueva contraseña")
    @Size(min = 8, message = "La nueva contraseña debe tener al menos 8 caracteres")
    private String contrasenaNueva;
}
