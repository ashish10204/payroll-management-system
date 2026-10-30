import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatToolbarModule } from '@angular/material/toolbar';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { AuthService } from './core/services/auth.service';
import { ToastComponent } from './core/ui/toast/toast.component';
import { LogoutConfirmationDialogComponent } from './core/ui/logout-confirmation-dialog/logout-confirmation-dialog';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, MatToolbarModule, MatButtonModule, ToastComponent],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  constructor(
    public readonly authService: AuthService,
    private readonly router: Router,
    private readonly dialog: MatDialog
  ) {}

  logout(): void {
    this.dialog.open(LogoutConfirmationDialogComponent, {
      width: 'min(420px, calc(100vw - 2rem))',
      autoFocus: false,
      restoreFocus: true
    }).afterClosed().subscribe((confirmed) => {
      if (confirmed) {
        this.authService.logout();
        this.router.navigateByUrl('/login');
      }
    });
  }
}
