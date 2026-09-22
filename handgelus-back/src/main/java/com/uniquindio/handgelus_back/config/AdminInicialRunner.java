package com.uniquindio.handgelus_back.config;


import com.uniquindio.handgelus_back.entity.Usuario;
import com.uniquindio.handgelus_back.enums.EstadoUsuario;
import com.uniquindio.handgelus_back.enums.Rol;
import com.uniquindio.handgelus_back.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Crea la cuenta de administrador inicial al arrancar la aplicación,
 * únicamente si todavía no existe ningún usuario con rol ADMINISTRADOR.
 *
 * Justificación: SWR-01 exige que solo un administrador pueda crear las
 * demás cuentas (asistente, tatuador/artista, otros administradores), por
 * lo que debe existir al menos uno desde el primer arranque; de lo
 * contrario nadie podría autenticarse para crear al primero (problema del
 * huevo y la gallina).
 *
 * Las credenciales por defecto se parametrizan por variables de entorno y
 * DEBEN cambiarse inmediatamente después del primer login en un entorno
 * real (ver PATCH /api/usuarios/{id}/contrasena).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInicialRunner implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin-inicial.correo:admin@handgelus.com}")
    private String correoAdmin;

    @Value("${app.admin-inicial.contrasena:Handgelus2026*}")
    private String contrasenaAdmin;

    @Override
    public void run(String... args) {
        boolean existeAdmin = !usuarioRepository.findByRolAndActivoTrue(Rol.ADMINISTRADOR).isEmpty();

        if (existeAdmin) {
            log.info("Ya existe al menos un administrador activo. Se omite la creación del administrador inicial.");
            return;
        }

        Usuario admin = Usuario.builder()
                .nombre("Administrador Handgelus")
                .correo(correoAdmin)
                .telefono(null)
                .contrasena(passwordEncoder.encode(contrasenaAdmin))
                .rol(Rol.ADMINISTRADOR)
                .estado(EstadoUsuario.ACTIVO)
                .activo(true)
                .build();

        usuarioRepository.save(admin);

        log.warn("Administrador inicial creado con correo '{}'. " +
                "Cambie la contraseña por defecto inmediatamente en un entorno real.", correoAdmin);
    }
}
