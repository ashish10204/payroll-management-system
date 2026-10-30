import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { DashboardComponent } from './features/dashboard/dashboard';
import { EmployeeDetailsComponent } from './features/employees/employee-details/employee-details';
import { EmployeeDirectorySummaryComponent } from './features/employees/employee-directory-summary/employee-directory-summary';
import { LoginComponent } from './features/login/login';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'employees/new', component: EmployeeDetailsComponent, canActivate: [authGuard] },
  { path: 'employees/:id/edit', component: EmployeeDetailsComponent, canActivate: [authGuard] },
  { path: 'employees', component: EmployeeDirectorySummaryComponent, canActivate: [authGuard] },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: '**', redirectTo: '/login' }
];
