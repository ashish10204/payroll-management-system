import { CommonModule } from '@angular/common';
import { Component, signal } from '@angular/core';
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
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class LoginComponent {
  username = '';
  password = '';
  readonly currentYear = new Date().getFullYear();
  readonly loading = signal(false);
  readonly passwordVisible = signal(false);

  constructor(
    private readonly authService: AuthService,
    private readonly router: Router,
    private readonly toastService: ToastService
  ) {}

  togglePasswordVisibility(): void {
    this.passwordVisible.update((visible) => !visible);
  }

  async submit(): Promise<void> {
    if (this.loading()) {
      return;
    }

    this.loading.set(true);
    try {
      const loggedIn = await this.authService.login(this.username, this.password);

      if (!loggedIn) {
        this.toastService.show('Invalid username or password.', 'error');
        return;
      }

      await this.router.navigateByUrl('/dashboard');
    } finally {
      this.loading.set(false);
    }
  }
}
