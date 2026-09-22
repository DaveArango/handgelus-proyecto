import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AutenticacionService } from '../services/autenticacion.service';

/**
 * Interceptor funcional (estilo Angular 15+) que adjunta el JWT a toda
 * petición saliente hacia la API, replicando lo que antes hacíamos a mano
 * en Postman con el header Authorization.
 */
export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const autenticacionService = inject(AutenticacionService);
  const token = autenticacionService.obtenerToken();

  if (!token) {
    return next(req);
  }

  const solicitudConToken = req.clone({
    setHeaders: { Authorization: `Bearer ${token}` },
  });

  return next(solicitudConToken);
};
