package com.uniquindio.handgelus_back.security;

import com.uniquindio.handgelus_back.entity.Usuario;
import com.uniquindio.handgelus_back.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Carga los datos del usuario por correo (username) para el proceso de
 * autenticación de Spring Security. Ref. SWR-02.
 */
@Service
@RequiredArgsConstructor
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("No existe un usuario con el correo: " + correo));
        return new UsuarioPrincipal(usuario);
    }
}
