import { TestBed } from '@angular/core/testing';
import { ToastComponent } from './toast.component';
import { ToastService } from './toast.service';

describe('ToastComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ToastComponent],
      providers: [ToastService]
    }).compileComponents();
  });

  it('renders and dismisses a notification', () => {
    const fixture = TestBed.createComponent(ToastComponent);
    const toastService = TestBed.inject(ToastService);
    toastService.show('Employee saved.');
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('Employee saved.');

    fixture.nativeElement.querySelector('button').click();
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).not.toContain('Employee saved.');
    toastService.dismiss();
  });
});
