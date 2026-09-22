package com.uniquindio.handgelus_back.entity;

import com.uniquindio.handgelus_back.enums.EstadoUsuario;
import com.uniquindio.handgelus_back.enums.Rol;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString(exclude = "contrasena")
@EqualsAndHashCode(callSuper = false, of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "usuarios",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_usuarios_correo", columnNames = "correo")
        }
)
public class Usuario extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id")
    private Long id;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    /**
     * Correo electrónico: identificador único de autenticación (username).
     * RF-01: "El correo debe ser único en el sistema".
     */
    @Column(name = "correo", nullable = false, length = 150, unique = true)
    private String correo;

    @Column(name = "telefono", length = 30)
    private String telefono;

    /**
     * Contraseña cifrada con bcrypt (SWR-03). Nunca se expone en DTOs de
     * salida ni se incluye en toString() (ver anotación @ToString(exclude)).
     */
    @Column(name = "contrasena", nullable = false, length = 255)
    private String contrasena;

    /**
     * Rol único asignado a la cuenta (SWR-01: "asignando un rol único a
     * cada cuenta"). Determina el acceso a los módulos (SWR-02).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false, length = 30)
    private Rol rol;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoUsuario estado = EstadoUsuario.ACTIVO;

    /**
     * Marca de borrado lógico. Se prefiere sobre DELETE físico para no
     * perder trazabilidad de solicitudes/pagos/citas históricos asociados
     * (coherente con RN-12: un cliente cancelado no puede eliminarse sin
     * regularizar su deuda).
     */
    @Builder.Default
    @Column(name = "activo", nullable = false)
    private boolean activo = true;
}
