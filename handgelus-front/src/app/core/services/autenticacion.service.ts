import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  CrearUsuarioRequest,
  LoginRequest,
  LoginResponse,
  Usuario,
} from '../models/usuario.model';

const CLAVE_TOKEN = 'hg_token';
const CLAVE_USUARIO = 'hg_usuario';

/**
 * Gestiona el ciclo de vida de la sesión: login (SWR-02), autorregistro de
 * clientes (RF-01), y persistencia del token/usuario en localStorage para
 * que la sesión sobreviva a un refresh de página.
 *
 * Expone el usuario actual como signal para que cualquier componente pueda
 * reaccionar reactivamente (p. ej. mostrar el nombre en la topbar, o el
 * guard de rutas verificar el rol) sin necesidad de suscripciones manuales.
 */
@Injectable({ providedIn: 'root' })
export class AutenticacionService {
  private readonly usuarioActualSignal = signal<Usuario | null>(this.leerUsuarioGuardado());

  readonly usuarioActual = this.usuarioActualSignal.asReadonly();
  readonly estaAutenticado = computed(() => this.usuarioActualSignal() !== null);
  readonly rolActual = computed(() => this.usuarioActualSignal()?.rol ?? null);

  constructor(private readonly http: HttpClient) {}

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${environment.apiUrl}/auth/login`, request).pipe(
      tap((respuesta) => this.guardarSesion(respuesta)),
    );
  }

  registrarCliente(request: CrearUsuarioRequest): Observable<Usuario> {
    return this.http.post<Usuario>(`${environment.apiUrl}/auth/registro`, request);
  }

  cerrarSesion(): void {
    localStorage.removeItem(CLAVE_TOKEN);
    localStorage.removeItem(CLAVE_USUARIO);
    this.usuarioActualSignal.set(null);
  }

  obtenerToken(): string | null {
    return localStorage.getItem(CLAVE_TOKEN);
  }

  private guardarSesion(respuesta: LoginResponse): void {
    localStorage.setItem(CLAVE_TOKEN, respuesta.token);
    localStorage.setItem(CLAVE_USUARIO, JSON.stringify(respuesta.usuario));
    this.usuarioActualSignal.set(respuesta.usuario);
  }

  private leerUsuarioGuardado(): Usuario | null {
    const crudo = localStorage.getItem(CLAVE_USUARIO);
    if (!crudo) {
      return null;
    }
    try {
      return JSON.parse(crudo) as Usuario;
    } catch {
      return null;
    }
  }
}
