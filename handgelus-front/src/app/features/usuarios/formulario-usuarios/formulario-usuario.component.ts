import { Component, EventEmitter, Input, OnInit, Output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { UsuarioService } from '../../../core/services/usuario.service';
import { ErrorApi, Usuario } from '../../../core/models/usuario.model';

/**
 * Formulario modal reutilizado para crear y editar usuarios (F-01).
 * En modo edición se ocultan los campos que no aplican a un PUT parcial
 * (la contraseña se cambia por un flujo separado, PATCH /contrasena, que
 * no se expone aquí para no mezclar responsabilidades en un solo formulario).
 */
@Component({
  selector: 'app-formulario-usuario',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './formulario-usuario.component.html',
  styleUrl: './formulario-usuario.component.css',
})
export class FormularioUsuarioComponent implements OnInit {
  @Input() usuarioAEditar: Usuario | null = null;
  @Output() cerrar = new EventEmitter<void>();
  @Output() guardado = new EventEmitter<void>();

  readonly guardando = signal(false);
  readonly mensajeError = signal<string | null>(null);

  // Se construye en el constructor: usar `fb` como inicializador de
  // propiedad dispara TS2729 (ver login.component.ts para el detalle).
  readonly formulario: FormGroup;

  constructor(
    private readonly fb: FormBuilder,
    private readonly usuarioService: UsuarioService,
  ) {
    this.formulario = this.fb.group({
      nombre: ['', [Validators.required]],
      correo: ['', [Validators.required, Validators.email]],
      telefono: [''],
      rol: ['CLIENTE', [Validators.required]],
      estado: ['ACTIVO'],
      contrasena: [''],
    });
  }

  ngOnInit(): void {
    if (this.usuarioAEditar) {
      this.formulario.patchValue({
        nombre: this.usuarioAEditar.nombre,
        correo: this.usuarioAEditar.correo,
        telefono: this.usuarioAEditar.telefono ?? '',
        rol: this.usuarioAEditar.rol,
        estado: this.usuarioAEditar.estado,
      });
    } else {
      // Solo en creación la contraseña es obligatoria.
      this.formulario.get('contrasena')?.setValidators([Validators.required, Validators.minLength(8)]);
      this.formulario.get('contrasena')?.updateValueAndValidity();
    }
  }

  esEdicion(): boolean {
    return this.usuarioAEditar !== null;
  }

  campoInvalido(nombre: string): boolean {
    const control = this.formulario.get(nombre);
    return !!control && control.invalid && (control.touched || control.dirty);
  }

  cerrarSiEsFondo(evento: MouseEvent): void {
    if ((evento.target as HTMLElement).classList.contains('hg-modal-fondo')) {
      this.cerrar.emit();
    }
  }

  guardar(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    this.guardando.set(true);
    this.mensajeError.set(null);

    const valores = this.formulario.getRawValue();

    const peticion = this.esEdicion()
      ? this.usuarioService.actualizar(this.usuarioAEditar!.id, {
        nombre: valores.nombre!,
        correo: valores.correo!,
        telefono: valores.telefono || undefined,
        rol: valores.rol as Usuario['rol'],
        estado: valores.estado as Usuario['estado'],
      })
      : this.usuarioService.crear({
        nombre: valores.nombre!,
        correo: valores.correo!,
        telefono: valores.telefono || undefined,
        contrasena: valores.contrasena!,
        rol: valores.rol as Usuario['rol'],
      });

    peticion.subscribe({
      next: () => {
        this.guardando.set(false);
        this.guardado.emit();
      },
      error: (error: { error?: ErrorApi }) => {
        this.guardando.set(false);
        this.mensajeError.set(error.error?.mensaje ?? 'No se pudo guardar el usuario.');
      },
    });
  }
}
