// Roles soportados por el sistema — deben coincidir exactamente con el
// enum Rol.java del backend (mismos literales, en mayúsculas).
export type Rol = 'CLIENTE' | 'ASISTENTE' | 'TATUADOR_ARTISTA' | 'ADMINISTRADOR';

export type EstadoUsuario = 'ACTIVO' | 'INACTIVO' | 'BLOQUEADO';

export interface Usuario {
  id: number;
  nombre: string;
  correo: string;
  telefono: string | null;
  rol: Rol;
  estado: EstadoUsuario;
  activo: boolean;
  fechaCreacion: string;
  fechaActualizacion: string;
}

export interface CrearUsuarioRequest {
  nombre: string;
  correo: string;
  telefono?: string;
  contrasena: string;
  rol: Rol;
}

export interface ActualizarUsuarioRequest {
  nombre?: string;
  correo?: string;
  telefono?: string;
  rol?: Rol;
  estado?: EstadoUsuario;
}

export interface CambiarContrasenaRequest {
  contrasenaActual: string;
  contrasenaNueva: string;
}

export interface LoginRequest {
  correo: string;
  contrasena: string;
}

export interface LoginResponse {
  token: string;
  tipo: string;
  usuario: Usuario;
}

// Forma del ErrorResponse.java del backend (GlobalExceptionHandler)
export interface ErrorApi {
  timestamp: string;
  status: number;
  error: string;
  mensaje: string;
  path: string;
  errores?: Record<string, string> | null;
}
