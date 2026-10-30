import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly storageKey = 'payflow-auth-token';
  private readonly http = inject(HttpClient);

  async login(username: string, password: string): Promise<boolean> {
    const normalizedUsername = username.trim();
    const normalizedPassword = password.trim();

    if (!normalizedUsername || !normalizedPassword) {
      return false;
    }

    try {
      const response = await firstValueFrom(
        this.http.post<{ token: string; username: string; role: string }>(`${environment.apiUrl}/auth/login`, {
          username: normalizedUsername,
          password: normalizedPassword
        })
      );

      sessionStorage.setItem(this.storageKey, response.token);
      return true;
    } catch {
      return false;
    }
  }

  logout(): void {
    sessionStorage.removeItem(this.storageKey);
  }

  get isAuthenticated(): boolean {
    return !!sessionStorage.getItem(this.storageKey);
  }

  get jwtToken(): string {
    return sessionStorage.getItem(this.storageKey) ?? '';
  }

  get bearerAuthHeaderValue(): string {
    const token = this.jwtToken;
    return token ? `Bearer ${token}` : '';
  }

  get basicAuthHeaderValue(): string {
    return this.bearerAuthHeaderValue;
  }

  get credentials(): { username: string; password: string } {
    return { username: '', password: '' };
  }
}
