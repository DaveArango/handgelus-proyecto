import { Component, computed } from '@angular/core';
import {AutenticacionService} from '../core/services/autenticacion.service';


/**
 * Placeholder para clientes, asistentes y tatuadores/artistas mientras se
 * implementan sus módulos específicos (F-02 en adelante). Por ahora solo
 * confirma que el login y la sesión funcionan de punta a punta para
 * cualquier rol, no solo para el administrador.
 */
@Component({
  selector: 'app-bienvenida',
  standalone: true,
  template: `
    <div class="hg-bienvenida">
      <h1>Hola, {{ nombre() }}</h1>
      <p>Tu módulo de trabajo estará disponible próximamente.</p>
    </div>
  `,
  styles: [`
    .hg-bienvenida {
      min-height: 100vh;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      gap: var(--sp-3);
      text-align: center;
      background: var(--hg-crema);
      padding: var(--sp-6);
    }
    .hg-bienvenida h1 {
      font-size: 2rem;
    }
    .hg-bienvenida p {
      color: var(--hg-oliva);
    }
  `],
})
export class BienvenidaComponent {
  constructor(private readonly autenticacionService: AutenticacionService) {}

  readonly nombre = computed(() => this.autenticacionService.usuarioActual()?.nombre ?? '');
}
