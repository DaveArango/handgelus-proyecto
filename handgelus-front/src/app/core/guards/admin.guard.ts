import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AutenticacionService } from '../services/autenticacion.service';

/**
 * Ref. SWR-01/SWR-02: el CRUD de usuarios es exclusivo del administrador.
 * Este guard replica en el front la misma restricción que ya aplica el
 * backend (SecurityConfig.java) — es una capa de UX, no de seguridad real:
 * la seguridad real siempre vive en el backend, esto solo evita que un
 * usuario sin permisos vea una pantalla que de todas formas le fallaría.
 */
export const adminGuard: CanActivateFn = () => {
  const autenticacionService = inject(AutenticacionService);
  const router = inject(Router);

  if (autenticacionService.rolActual() === 'ADMINISTRADOR') {
    return true;
  }

  router.navigate(['/login']);
  return false;
};
