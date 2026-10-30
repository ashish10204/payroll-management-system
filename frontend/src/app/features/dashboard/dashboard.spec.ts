import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { PayflowApiService } from '../../core/services/payflow-api.service';
import { DashboardComponent } from './dashboard';

describe('DashboardComponent', () => {
  const apiService = {
    getHeadcount: vi.fn(),
    getCompensation: vi.fn(),
    getReferenceData: vi.fn()
  };

  beforeEach(async () => {
    apiService.getReferenceData.mockReset().mockReturnValue(of({
      countries: [{ code: 'IN', label: 'India' }, { code: 'US', label: 'United States' }],
      departments: [{ code: 'ENG', label: 'Engineering' }, { code: 'HR', label: 'People' }],
      jobTitles: [],
      employmentStatuses: [{ code: 'ACTIVE', label: 'Active' }]
    }));
    apiService.getHeadcount.mockReset().mockReturnValue(of({
      totalEmployees: 3,
      byCountry: { IN: 2, US: 1 },
      byDepartment: { ENG: 2, HR: 1 },
      byEmploymentStatus: { ACTIVE: 3 }
    }));
    apiService.getCompensation.mockReset().mockReturnValue(of({
      totalEmployees: 3,
      matchedEmployees: 2,
      currencyCode: 'USD',
      payFrequency: 'MONTHLY',
      groups: [{
        currencyCode: 'USD',
        payFrequency: 'MONTHLY',
        employeeCount: 2,
        minimum: 5000,
        average: 6000,
        maximum: 7000,
        total: 12000
      }]
    }));

    await TestBed.configureTestingModule({
      imports: [DashboardComponent],
      providers: [{ provide: PayflowApiService, useValue: apiService }]
    }).compileComponents();
  });

  it('renders live dashboard metrics, breakdowns, and report filters', () => {
    const fixture = TestBed.createComponent(DashboardComponent);
    fixture.detectChanges();

    expect(apiService.getHeadcount).toHaveBeenCalledOnce();
    expect(apiService.getCompensation).toHaveBeenCalledOnce();
    expect(apiService.getReferenceData).toHaveBeenCalledOnce();
    expect(fixture.nativeElement.textContent).toContain('People overview');
    expect(fixture.nativeElement.textContent).toContain('WORKFORCE');
    expect(fixture.nativeElement.textContent).toContain('3');
    expect(fixture.nativeElement.textContent).toContain('ENG');
    expect(fixture.nativeElement.textContent).toContain('USD');
    expect(fixture.nativeElement.textContent).toContain('6,000.00');
    expect(fixture.nativeElement.textContent).toContain('Compensation as of');
    expect(fixture.nativeElement.querySelector('mat-spinner')).toBeNull();
  });

  it('reloads both reports with selected filters', () => {
    const fixture = TestBed.createComponent(DashboardComponent);
    fixture.detectChanges();
    fixture.componentInstance.filters.country = 'IN';
    fixture.componentInstance.filters.department = 'ENG';
    fixture.componentInstance.filters.employmentStatus = 'ACTIVE';
    fixture.componentInstance.filters.asOfDate = '2026-10-30';

    fixture.componentInstance.applyFilters();

    expect(apiService.getHeadcount).toHaveBeenLastCalledWith({
      country: 'IN',
      department: 'ENG',
      employmentStatus: 'ACTIVE'
    });
    expect(apiService.getCompensation).toHaveBeenLastCalledWith({
      country: 'IN',
      department: 'ENG',
      employmentStatus: 'ACTIVE',
      asOfDate: '2026-10-30'
    });
  });
});
