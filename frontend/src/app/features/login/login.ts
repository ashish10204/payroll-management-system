import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { ToastService } from '../../core/ui/toast/toast.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, MatCardModule, MatFormFieldModule, MatInputModule, MatButtonModule],
  template: `
    <div class="login-shell">
      <mat-card class="login-card">
        <mat-card-title>PayFlow HR Portal</mat-card-title>
        <mat-card-subtitle>Sign in with the backend credentials</mat-card-subtitle>

        <form (ngSubmit)="submit()" autocomplete="off" autocorrect="off" autocapitalize="none" spellcheck="false">
          <mat-form-field appearance="outline">
            <mat-label>Username</mat-label>
            <input matInput [(ngModel)]="username" name="username" required autocomplete="off" />
          </mat-form-field>

          <mat-form-field appearance="outline">
            <mat-label>Password</mat-label>
            <input matInput type="password" [(ngModel)]="password" name="password" required autocomplete="new-password" />
          </mat-form-field>

          <button mat-flat-button color="primary" type="submit" [disabled]="loading">
            {{ loading ? 'Signing in...' : 'Sign in' }}
          </button>

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

    `
  ]
})
export class LoginComponent {
  username = '';
  password = '';
  loading = false;

  constructor(
    private readonly authService: AuthService,
    private readonly router: Router,
    private readonly toastService: ToastService
  ) {}

  async submit(): Promise<void> {
    this.loading = true;

    const loggedIn = await this.authService.login(this.username, this.password);

    if (!loggedIn) {
      this.toastService.show('Invalid username or password.', 'error');
      this.loading = false;
      return;
    }

    this.router.navigateByUrl('/dashboard');
    this.loading = false;
  }
}
