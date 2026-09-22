package com.uniquindio.handgelus_back.security;

import com.uniquindio.handgelus_back.entity.Usuario;
import com.uniquindio.handgelus_back.enums.EstadoUsuario;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Adapta la entidad Usuario al contrato UserDetails de Spring Security.
 * El rol se expone como authority con prefijo "ROLE_" (convención de Spring
 * Security), lo cual habilita el control de acceso por rol exigido en
 * SWR-02 mediante anotaciones @PreAuthorize / hasRole(...).
 */
@Getter
public class UsuarioPrincipal implements UserDetails {

    private final Usuario usuario;

    public UsuarioPrincipal(Usuario usuario) {
        this.usuario = usuario;
    }

    public Long getId() {
        return usuario.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name()));
    }

    @Override
    public String getPassword() {
        return usuario.getContrasena();
    }

    @Override
    public String getUsername() {
        return usuario.getCorreo();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return usuario.getEstado() != EstadoUsuario.BLOQUEADO;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return usuario.isActivo() && usuario.getEstado() == EstadoUsuario.ACTIVO;
    }
}
