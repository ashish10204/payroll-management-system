import { Injectable, signal } from '@angular/core';

export type ToastKind = 'success' | 'error';

export interface ToastMessage {
  text: string;
  kind: ToastKind;
}

@Injectable({ providedIn: 'root' })
export class ToastService {
  readonly message = signal<ToastMessage | null>(null);
  private timeoutId: ReturnType<typeof setTimeout> | null = null;

  show(text: string, kind: ToastKind = 'success', durationMs = 5000): void {
    if (this.timeoutId !== null) {
      clearTimeout(this.timeoutId);
    }

    this.message.set({ text, kind });
    this.timeoutId = setTimeout(() => this.dismiss(), durationMs);
  }

  dismiss(): void {
    if (this.timeoutId !== null) {
      clearTimeout(this.timeoutId);
      this.timeoutId = null;
    }

    this.message.set(null);
  }
}
