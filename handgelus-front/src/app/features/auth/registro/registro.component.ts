import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AutenticacionService } from '../../../core/services/autenticacion.service';
import { ErrorApi } from '../../../core/models/usuario.model';

/**
 * Autorregistro público de clientes (RF-01). El backend fuerza el rol a
 * CLIENTE sin importar lo que se envíe, así que aquí ni siquiera se expone
 * un selector de rol — es coherente con esa regla de negocio.
 */
@Component({
  selector: 'app-registro',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './registro.component.html',
  styleUrl: './registro.component.css',
})
export class RegistroComponent {
  readonly cargando = signal(false);
  readonly mensajeError = signal<string | null>(null);
  readonly registroExitoso = signal(false);

  // Se construye en el constructor: usar `fb` como inicializador de
  // propiedad dispara TS2729 (ver login.component.ts para el detalle).
  readonly formulario: FormGroup;

  constructor(
    private readonly fb: FormBuilder,
    private readonly autenticacionService: AutenticacionService,
    private readonly router: Router,
  ) {
    this.formulario = this.fb.group({
      nombre: ['', [Validators.required]],
      correo: ['', [Validators.required, Validators.email]],
      telefono: [''],
      contrasena: ['', [Validators.required, Validators.minLength(8)]],
    });
  }

  campoInvalido(nombre: string): boolean {
    const control = this.formulario.get(nombre);
    return !!control && control.invalid && (control.touched || control.dirty);
  }

  enviar(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    this.cargando.set(true);
    this.mensajeError.set(null);
    this.registroExitoso.set(false);

    const { nombre, correo, telefono, contrasena } = this.formulario.getRawValue();

    // El rol enviado aquí es irrelevante: el backend siempre fuerza CLIENTE
    // en el endpoint /api/auth/registro (ver registrarCliente en el service).
    this.autenticacionService
      .registrarCliente({
        nombre: nombre!,
        correo: correo!,
        telefono: telefono || undefined,
        contrasena: contrasena!,
        rol: 'CLIENTE',
      })
      .subscribe({
        next: () => {
          this.cargando.set(false);
          this.registroExitoso.set(true);
          setTimeout(() => this.router.navigate(['/login']), 1500);
        },
        error: (error: { error?: ErrorApi }) => {
          this.cargando.set(false);
          this.mensajeError.set(error.error?.mensaje ?? 'No se pudo crear la cuenta. Intenta de nuevo.');
        },
      });
  }
}
