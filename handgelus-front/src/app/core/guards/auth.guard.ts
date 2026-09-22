import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AutenticacionService } from '../services/autenticacion.service';

/**
 * Bloquea el acceso a rutas que requieren sesión iniciada. Si no hay
 * usuario autenticado, redirige al login.
 */
export const authGuard: CanActivateFn = () => {
  const autenticacionService = inject(AutenticacionService);
  const router = inject(Router);

  if (autenticacionService.estaAutenticado()) {
    return true;
  }

  router.navigate(['/login']);
  return false;
};
