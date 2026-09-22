package com.uniquindio.handgelus_back.exception;

/**
 * Excepción genérica para violaciones de reglas de negocio, p. ej.:
 * correo ya registrado (RF-01), contraseña actual incorrecta, etc.
 */
public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
