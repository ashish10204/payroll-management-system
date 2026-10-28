import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, MatCardModule, MatFormFieldModule, MatInputModule, MatButtonModule],
  template: `
    <div class="login-shell">
      <mat-card class="login-card">
        <mat-card-title>PayFlow HR Portal</mat-card-title>
        <mat-card-subtitle>Sign in with the backend credentials</mat-card-subtitle>

        <form (ngSubmit)="submit()">
          <mat-form-field appearance="outline">
            <mat-label>Username</mat-label>
            <input matInput [(ngModel)]="username" name="username" required />
          </mat-form-field>

          <mat-form-field appearance="outline">
            <mat-label>Password</mat-label>
            <input matInput type="password" [(ngModel)]="password" name="password" required />
          </mat-form-field>

          <button mat-flat-button color="primary" type="submit" [disabled]="loading">
            {{ loading ? 'Signing in...' : 'Sign in' }}
          </button>

          <p class="error" *ngIf="errorMessage">{{ errorMessage }}</p>
        </form>
      </mat-card>
    </div>
  `,
  styles: [
    `
      .login-shell {
        min-height: 100vh;
        display: grid;
        place-items: center;
        background: linear-gradient(135deg, #f4f7fb 0%, #dfeaf9 100%);
      }

      .login-card {
        width: min(430px, calc(100vw - 2rem));
        padding: 1rem;
      }

      form {
        display: flex;
        flex-direction: column;
        gap: 1rem;
        margin-top: 1rem;
      }

      .error {
        margin: 0;
        color: #c62828;
        font-size: 0.9rem;
      }
    `
  ]
})
export class LoginComponent {
  username = 'system';
  password = 'system';
  loading = false;
  errorMessage = '';

  constructor(
    private readonly authService: AuthService,
    private readonly router: Router
  ) {}

  submit(): void {
    this.errorMessage = '';
    this.loading = true;

    if (!this.authService.login(this.username, this.password)) {
      this.errorMessage = 'Username and password are required.';
      this.loading = false;
      return;
    }

    this.router.navigateByUrl('/dashboard');
    this.loading = false;
  }
}
