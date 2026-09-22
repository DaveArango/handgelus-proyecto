import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AutenticacionService } from '../services/autenticacion.service';

/**
 * Si el backend responde 401 (token expirado o credenciales inválidas en
 * una ruta protegida), cierra la sesión local y redirige al login, en
 * lugar de dejar a la persona atascada viendo errores silenciosos.
 */
export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const autenticacionService = inject(AutenticacionService);
  const router = inject(Router);

  return next(req).pipe(
    catchError((error) => {
      if (error.status === 401) {
        autenticacionService.cerrarSesion();
        router.navigate(['/login']);
      }
      return throwError(() => error);
    }),
  );
};
