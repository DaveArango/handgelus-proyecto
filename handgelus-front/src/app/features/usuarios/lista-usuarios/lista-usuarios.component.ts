import { Component, OnInit, signal } from '@angular/core';
import { UsuarioService } from '../../../core/services/usuario.service';
import { ErrorApi, Rol, Usuario } from '../../../core/models/usuario.model';
import {FormularioUsuarioComponent} from '../formulario-usuarios/formulario-usuario.component';


const ETIQUETAS_ROL: Record<Rol, string> = {
  CLIENTE: 'Cliente',
  ASISTENTE: 'Asistente',
  TATUADOR_ARTISTA: 'Tatuador / Artista',
  ADMINISTRADOR: 'Administrador',
};

/**
 * Listado principal de F-01: consume GET /api/usuarios (con filtro opcional
 * por rol) y orquesta la apertura del formulario de creación/edición.
 */
@Component({
  selector: 'app-lista-usuarios',
  standalone: true,
  imports: [FormularioUsuarioComponent],
  templateUrl: './lista-usuarios.component.html',
  styleUrl: './lista-usuarios.component.css',
})
export class ListaUsuariosComponent implements OnInit {
  readonly roles: Rol[] = ['CLIENTE', 'ASISTENTE', 'TATUADOR_ARTISTA', 'ADMINISTRADOR'];

  readonly usuarios = signal<Usuario[]>([]);
  readonly cargando = signal(false);
  readonly mensajeError = signal<string | null>(null);
  readonly filtroRol = signal<Rol | null>(null);

  readonly mostrarFormulario = signal(false);
  readonly usuarioSeleccionado = signal<Usuario | null>(null);

  constructor(private readonly usuarioService: UsuarioService) {}

  ngOnInit(): void {
    this.cargar();
  }

  etiquetaRol(rol: Rol): string {
    return ETIQUETAS_ROL[rol];
  }

  filtrarPor(rol: Rol | null): void {
    this.filtroRol.set(rol);
    this.cargar();
  }

  cargar(): void {
    this.cargando.set(true);
    this.mensajeError.set(null);

    this.usuarioService.listar(this.filtroRol() ?? undefined).subscribe({
      next: (usuarios) => {
        this.usuarios.set(usuarios);
        this.cargando.set(false);
      },
      error: (error: { error?: ErrorApi }) => {
        this.mensajeError.set(error.error?.mensaje ?? 'No se pudo cargar el listado de usuarios.');
        this.cargando.set(false);
      },
    });
  }

  abrirCreacion(): void {
    this.usuarioSeleccionado.set(null);
    this.mostrarFormulario.set(true);
  }

  abrirEdicion(usuario: Usuario): void {
    this.usuarioSeleccionado.set(usuario);
    this.mostrarFormulario.set(true);
  }

  cerrarFormulario(): void {
    this.mostrarFormulario.set(false);
  }

  alGuardar(): void {
    this.mostrarFormulario.set(false);
    this.cargar();
  }

  desactivar(usuario: Usuario): void {
    const confirmado = confirm(`¿Desactivar la cuenta de ${usuario.nombre}?`);
    if (!confirmado) {
      return;
    }

    this.usuarioService.desactivar(usuario.id).subscribe({
      next: () => this.cargar(),
      error: (error: { error?: ErrorApi }) => {
        this.mensajeError.set(error.error?.mensaje ?? 'No se pudo desactivar el usuario.');
      },
    });
  }

  reactivar(usuario: Usuario): void {
    this.usuarioService.reactivar(usuario.id).subscribe({
      next: () => this.cargar(),
      error: (error: { error?: ErrorApi }) => {
        this.mensajeError.set(error.error?.mensaje ?? 'No se pudo reactivar el usuario.');
      },
    });
  }
}
