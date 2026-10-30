import { Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule } from '@angular/material/dialog';

@Component({
  selector: 'app-logout-confirmation-dialog',
  standalone: true,
  imports: [MatButtonModule, MatDialogModule],
  templateUrl: './logout-confirmation-dialog.html',
  styleUrl: './logout-confirmation-dialog.scss'
})
export class LogoutConfirmationDialogComponent {}
