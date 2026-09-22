import { Component, computed } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet, Router } from '@angular/router';
import { AutenticacionService } from '../../core/services/autenticacion.service';
import { Rol } from '../../core/models/usuario.model';

const ETIQUETAS_ROL: Record<Rol, string> = {
  CLIENTE: 'Cliente',
  ASISTENTE: 'Asistente',
  TATUADOR_ARTISTA: 'Tatuador / Artista',
  ADMINISTRADOR: 'Administrador',
};

/**
 * Cascarón visual compartido por todas las pantallas autenticadas:
 * sidebar de navegación + topbar con identidad de la sesión activa.
 */
@Component({
  selector: 'app-panel-layout',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './panel-layout.component.html',
  styleUrl: './panel-layout.component.css',
})
export class PanelLayoutComponent {
  constructor(
    private readonly autenticacionService: AutenticacionService,
    private readonly router: Router,
  ) {}

  readonly nombreUsuario = computed(() => this.autenticacionService.usuarioActual()?.nombre ?? '');

  readonly etiquetaRol = computed(() => {
    const rol = this.autenticacionService.rolActual();
    return rol ? ETIQUETAS_ROL[rol] : '';
  });

  salir(): void {
    this.autenticacionService.cerrarSesion();
    this.router.navigate(['/login']);
  }
}
