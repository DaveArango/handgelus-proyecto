package com.uniquindio.handgelus_back.repository;

import com.uniquindio.handgelus_back.entity.Usuario;
import com.uniquindio.handgelus_back.enums.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Búsqueda por correo — usada por el login (SWR-02) y por la
     * validación de unicidad al registrar (RF-01).
     */
    Optional<Usuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);

    /**
     * Para validar unicidad al actualizar: existe otro usuario (id distinto)
     * con ese correo.
     */
    boolean existsByCorreoAndIdNot(String correo, Long id);

    List<Usuario> findByRolAndActivoTrue(Rol rol);

    List<Usuario> findByActivoTrue();
}
