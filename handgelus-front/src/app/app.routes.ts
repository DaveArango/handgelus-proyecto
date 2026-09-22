import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login/login.component';
import { RegistroComponent } from './features/auth/registro/registro.component';
import { BienvenidaComponent } from './features/bienvenida.component';
import { PanelLayoutComponent } from './shared/layout/panel-layout.component';
import { ListaUsuariosComponent } from './features/usuarios/lista-usuarios/lista-usuarios.component';
import { authGuard } from './core/guards/auth.guard';
import { adminGuard } from './core/guards/admin.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'registro', component: RegistroComponent },

  {
    path: 'bienvenida',
    component: BienvenidaComponent,
    canActivate: [authGuard],
  },

  {
    path: '',
    component: PanelLayoutComponent,
    canActivate: [authGuard],
    children: [
      {
        path: 'usuarios',
        component: ListaUsuariosComponent,
        canActivate: [adminGuard],
      },
    ],
  },

  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: '**', redirectTo: 'login' },
];
