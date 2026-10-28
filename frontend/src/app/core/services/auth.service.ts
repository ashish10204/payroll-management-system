import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly storageKey = 'payflow-auth';

  login(username: string, password: string): boolean {
    const normalizedUsername = username.trim();
    const normalizedPassword = password.trim();

    if (!normalizedUsername || !normalizedPassword) {
      return false;
    }

    sessionStorage.setItem(this.storageKey, btoa(`${normalizedUsername}:${normalizedPassword}`));
    return true;
  }

  logout(): void {
    sessionStorage.removeItem(this.storageKey);
  }

  get isAuthenticated(): boolean {
    return !!sessionStorage.getItem(this.storageKey);
  }

  get basicAuthHeaderValue(): string {
    const encoded = sessionStorage.getItem(this.storageKey);
    return encoded ? `Basic ${encoded}` : '';
  }

  get credentials(): { username: string; password: string } {
    const encoded = sessionStorage.getItem(this.storageKey);
    if (!encoded) {
      return { username: '', password: '' };
    }

    const decoded = atob(encoded);
    const [username, ...passwordParts] = decoded.split(':');

    return {
      username,
      password: passwordParts.join(':')
    };
  }
}
