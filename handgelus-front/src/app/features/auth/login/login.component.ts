import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AutenticacionService } from '../../../core/services/autenticacion.service';
import { ErrorApi } from '../../../core/models/usuario.model';

/**
 * Pantalla de login (SWR-02). Al autenticar exitosamente, redirige según
 * el rol: el administrador va al panel de usuarios; el resto, por ahora,
 * a una pantalla de bienvenida simple (placeholder hasta implementar sus
 * módulos correspondientes en futuras iteraciones).
 */
@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  readonly cargando = signal(false);
  readonly mensajeError = signal<string | null>(null);

  // Se construye en el constructor (no como inicializador de propiedad)
  // porque en ese punto Angular todavía no ha inyectado `fb`: usarlo antes
  // dispara TS2729 "used before its initialization" con
  // useDefineForClassFields habilitado (default en TS moderno).
  readonly formulario: FormGroup;

  constructor(
    private readonly fb: FormBuilder,
    private readonly autenticacionService: AutenticacionService,
    private readonly router: Router,
  ) {
    this.formulario = this.fb.group({
      correo: ['', [Validators.required, Validators.email]],
      contrasena: ['', [Validators.required]],
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

    const { correo, contrasena } = this.formulario.getRawValue();

    this.autenticacionService.login({ correo: correo!, contrasena: contrasena! }).subscribe({
      next: (respuesta) => {
        this.cargando.set(false);
        const destino = respuesta.usuario.rol === 'ADMINISTRADOR' ? '/usuarios' : '/bienvenida';
        this.router.navigate([destino]);
      },
      error: (error: { error?: ErrorApi }) => {
        this.cargando.set(false);
        this.mensajeError.set(error.error?.mensaje ?? 'No se pudo iniciar sesión. Intenta de nuevo.');
      },
    });
  }
}
