import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  ActualizarUsuarioRequest,
  CambiarContrasenaRequest,
  CrearUsuarioRequest,
  Rol,
  Usuario,
} from '../models/usuario.model';

/**
 * Consume el CRUD de F-01 en /api/usuarios. Todos estos endpoints exigen
 * rol ADMINISTRADOR en el backend (ver SecurityConfig.java); el interceptor
 * JWT adjunta el token automáticamente en cada petición.
 */
@Injectable({ providedIn: 'root' })
export class UsuarioService {
  private readonly baseUrl = `${environment.apiUrl}/usuarios`;

  constructor(private readonly http: HttpClient) {}

  listar(rol?: Rol): Observable<Usuario[]> {
    let params = new HttpParams();
    if (rol) {
      params = params.set('rol', rol);
    }
    return this.http.get<Usuario[]>(this.baseUrl, { params });
  }

  obtenerPorId(id: number): Observable<Usuario> {
    return this.http.get<Usuario>(`${this.baseUrl}/${id}`);
  }

  crear(request: CrearUsuarioRequest): Observable<Usuario> {
    return this.http.post<Usuario>(this.baseUrl, request);
  }

  actualizar(id: number, request: ActualizarUsuarioRequest): Observable<Usuario> {
    return this.http.put<Usuario>(`${this.baseUrl}/${id}`, request);
  }

  cambiarContrasena(id: number, request: CambiarContrasenaRequest): Observable<void> {
    return this.http.patch<void>(`${this.baseUrl}/${id}/contrasena`, request);
  }

  desactivar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  reactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.baseUrl}/${id}/reactivar`, {});
  }
}
