package com.uniquindio.handgelus_back.dto.response;

import lombok.*;

/**
 * Respuesta de autenticación exitosa: token JWT + datos básicos del usuario
 * autenticado, para que el cliente (front) sepa qué rol tiene sin decodificar
 * el token manualmente.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private String token;
    private String tipo; // "Bearer"
    private UsuarioResponse usuario;
}
