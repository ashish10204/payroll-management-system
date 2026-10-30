import { TestBed } from '@angular/core/testing';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { of } from 'rxjs';
import { App } from './app';
import { AuthService } from './core/services/auth.service';

describe('App', () => {
  const authService = {
    isAuthenticated: true,
    logout: vi.fn()
  };
  const router = {
    navigateByUrl: vi.fn()
  };
  let dialog: MatDialog;

  beforeEach(async () => {
    vi.restoreAllMocks();
    authService.logout.mockClear();
    router.navigateByUrl.mockClear();
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: Router, useValue: router }
      ]
    }).compileComponents();
    dialog = TestBed.inject(MatDialog);
    vi.spyOn(dialog, 'open').mockReturnValue({
      afterClosed: () => of(false)
    } as MatDialogRef<never, unknown>);
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it('asks for confirmation before logging out', () => {
    const fixture = TestBed.createComponent(App);

    fixture.componentInstance.logout();

    expect(dialog.open).toHaveBeenCalledOnce();
    expect(authService.logout).not.toHaveBeenCalled();
    expect(router.navigateByUrl).not.toHaveBeenCalled();
  });

  it('logs out and navigates to login only after confirmation', () => {
    vi.mocked(dialog.open).mockReturnValue({
      afterClosed: () => of(true)
    } as MatDialogRef<never, unknown>);
    const fixture = TestBed.createComponent(App);

    fixture.componentInstance.logout();

    expect(authService.logout).toHaveBeenCalledOnce();
    expect(router.navigateByUrl).toHaveBeenCalledWith('/login');
  });
});
