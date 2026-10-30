import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { ToastService } from '../../core/ui/toast/toast.service';
import { LoginComponent } from './login';

describe('LoginComponent', () => {
  const authService = {
    login: vi.fn()
  };
  const router = {
    navigateByUrl: vi.fn()
  };
  const toastService = {
    show: vi.fn()
  };

  beforeEach(async () => {
    authService.login.mockReset();
    router.navigateByUrl.mockReset();
    toastService.show.mockReset();

    await TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: Router, useValue: router },
        { provide: ToastService, useValue: toastService }
      ]
    }).compileComponents();
  });

  it('renders the sign-in form and responsive welcome content', () => {
    const fixture = TestBed.createComponent(LoginComponent);
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('Sign in to PayFlow');
    expect(fixture.nativeElement.textContent).toContain('Better visibility.');
    expect(fixture.nativeElement.querySelector('input[autocomplete="username"]')).toBeTruthy();
    expect(fixture.nativeElement.querySelector('input[autocomplete="current-password"]')).toBeTruthy();
  });

  it('navigates to the dashboard after a successful login', async () => {
    authService.login.mockResolvedValue(true);
    const fixture = TestBed.createComponent(LoginComponent);
    const component = fixture.componentInstance;
    component.username = 'payflow-user';
    component.password = 'secret';

    await component.submit();

    expect(authService.login).toHaveBeenCalledWith('payflow-user', 'secret');
    expect(router.navigateByUrl).toHaveBeenCalledWith('/dashboard');
    expect(toastService.show).not.toHaveBeenCalled();
    expect(component.loading()).toBe(false);
  });

  it('shows a toast and remains on the page when login fails', async () => {
    authService.login.mockResolvedValue(false);
    const fixture = TestBed.createComponent(LoginComponent);
    const component = fixture.componentInstance;

    await component.submit();

    expect(toastService.show).toHaveBeenCalledWith('Invalid username or password.', 'error');
    expect(router.navigateByUrl).not.toHaveBeenCalled();
    expect(component.loading()).toBe(false);
  });

  it('toggles password visibility', () => {
    const fixture = TestBed.createComponent(LoginComponent);
    const component = fixture.componentInstance;
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('input[autocomplete="current-password"]').type).toBe('password');
    component.togglePasswordVisibility();
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('input[autocomplete="current-password"]').type).toBe('text');
  });
});
