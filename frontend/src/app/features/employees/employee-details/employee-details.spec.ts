import { TestBed } from '@angular/core/testing';
import { NgForm } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';
import { EmployeeResponse } from '../../../core/models/payflow';
import { PayflowApiService } from '../../../core/services/payflow-api.service';
import { ToastService } from '../../../core/ui/toast/toast.service';
import { EmployeeDetailsComponent } from './employee-details';

describe('EmployeeDetailsComponent', () => {
  let requestedEmployeeId: string | null;

  const employee: EmployeeResponse = {
    id: 17,
    employeeNumber: 'EMP-17',
    firstName: 'Taylor',
    lastName: 'Example',
    email: 'taylor@example.com',
    countryCode: 'US',
    departmentCode: 'ENG',
    jobTitle: 'Engineer',
    employmentStatus: 'ACTIVE',
    hireDate: '2024-05-12'
  };
  const apiService = {
    getReferenceData: vi.fn().mockReturnValue(of({
      countries: [],
      departments: [],
      jobTitles: [],
      employmentStatuses: []
    })),
    getEmployee: vi.fn().mockReturnValue(of(employee)),
    createEmployee: vi.fn(),
    onboardEmployee: vi.fn(),
    updateEmployee: vi.fn().mockReturnValue(of(employee)),
    getSalaryHistory: vi.fn().mockReturnValue(of([])),
    createSalaryChange: vi.fn()
  };
  const toastService = {
    show: vi.fn()
  };
  const router = {
    navigate: vi.fn()
  };

  beforeEach(async () => {
    requestedEmployeeId = '17';
    apiService.getReferenceData.mockClear();
    apiService.getEmployee.mockClear();
    apiService.getSalaryHistory.mockClear();
    apiService.getSalaryHistory.mockReturnValue(of([]));
    apiService.onboardEmployee.mockClear();
    toastService.show.mockClear();
    router.navigate.mockClear();

    await TestBed.configureTestingModule({
      imports: [EmployeeDetailsComponent],
      providers: [
        { provide: PayflowApiService, useValue: apiService },
        { provide: ToastService, useValue: toastService },
        { provide: Router, useValue: router },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: { get: () => requestedEmployeeId },
              queryParamMap: { get: () => '2' }
            }
          }
        }
      ]
    }).compileComponents();
  });

  it('loads the selected employee into the edit form', async () => {
    const fixture = TestBed.createComponent(EmployeeDetailsComponent);
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    expect(apiService.getEmployee).toHaveBeenCalledWith(17);
    expect(apiService.getSalaryHistory).toHaveBeenCalledWith(17);
    expect(fixture.componentInstance.employeeForm.employeeNumber).toBe('EMP-17');
    expect(fixture.componentInstance.employeeForm.firstName).toBe('Taylor');
    expect(fixture.componentInstance.employeeForm.hireDate).toEqual(new Date(2024, 4, 12));
    expect(fixture.nativeElement.querySelector('[name="employeeNumber"]').value).toBe('EMP-17');
    expect(fixture.nativeElement.querySelector('[name="firstName"]').value).toBe('Taylor');
    expect(fixture.nativeElement.querySelector('[name="email"]').value).toBe('taylor@example.com');
  });

  it('renders existing salary history and the current salary when available', async () => {
    apiService.getSalaryHistory.mockReturnValue(of([{
      salaryRecordId: 31,
      employeeId: 17,
      amount: 125000,
      currencyCode: 'USD',
      payFrequency: 'ANNUAL',
      effectiveFrom: '2020-01-01',
      effectiveTo: null,
      reason: 'Promotion',
      createdBy: 'Ashish10204'
    }]));
    const fixture = TestBed.createComponent(EmployeeDetailsComponent);
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('125,000.00 USD');
    expect(fixture.nativeElement.textContent).toContain('Promotion');
    expect(fixture.nativeElement.textContent).not.toContain('No salary changes have been recorded.');
  });

  it('returns to the same summary page when cancelled', () => {
    const fixture = TestBed.createComponent(EmployeeDetailsComponent);
    fixture.componentInstance.cancel();

    expect(router.navigate).toHaveBeenCalledWith(
      ['/employees'],
      { queryParams: { page: 2 } }
    );
  });

  it('saves edits and returns to the summary page', () => {
    const fixture = TestBed.createComponent(EmployeeDetailsComponent);
    fixture.detectChanges();
    const form = fixture.debugElement.query(By.css('form'));
    const ngForm = form.injector.get(NgForm);

    fixture.componentInstance.submit(ngForm);

    expect(apiService.updateEmployee).toHaveBeenCalledWith(17, {
      firstName: 'Taylor',
      lastName: 'Example',
      email: 'taylor@example.com',
      countryCode: 'US',
      departmentCode: 'ENG',
      jobTitle: 'Engineer',
      employmentStatus: 'ACTIVE',
      hireDate: '2024-05-12'
    });
    expect(router.navigate).toHaveBeenCalledWith(
      ['/employees'],
      { queryParams: { page: 2 } }
    );
    expect(toastService.show).toHaveBeenCalledWith('Employee updated successfully.');
  });

  it('records a salary change from the salary form without leaving employee details', () => {
    apiService.createSalaryChange.mockReturnValue(of({
      salaryRecordId: 31,
      employeeId: 17,
      amount: 125000,
      currencyCode: 'USD',
      payFrequency: 'ANNUAL',
      effectiveFrom: '2026-10-30',
      effectiveTo: null,
      reason: 'Promotion',
      createdBy: 'Ashish10204'
    }));
    const fixture = TestBed.createComponent(EmployeeDetailsComponent);
    fixture.detectChanges();
    const component = fixture.componentInstance;
    component.salaryChange = {
      amount: 125000,
      currencyCode: 'USD',
      payFrequency: 'ANNUAL',
      effectiveFrom: new Date(2026, 9, 30),
      reason: 'Promotion'
    };
    fixture.detectChanges();
    const forms = fixture.debugElement.queryAll(By.css('form'));
    const salaryForm = forms[1].injector.get(NgForm);

    component.submitSalaryChange(salaryForm);

    expect(apiService.createSalaryChange).toHaveBeenCalledWith(17, {
      amount: 125000,
      currencyCode: 'USD',
      payFrequency: 'ANNUAL',
      effectiveFrom: '2026-10-30',
      reason: 'Promotion'
    });
    expect(toastService.show).toHaveBeenCalledWith('Salary change recorded successfully.');
    expect(router.navigate).not.toHaveBeenCalled();
    expect(component.salaryHistory[0].salaryRecordId).toBe(31);
  });

  it('submits new employee and initial salary as one onboarding request', () => {
    requestedEmployeeId = null;
    apiService.onboardEmployee.mockReturnValue(of({
      employee,
      initialSalary: {
        salaryRecordId: 32,
        employeeId: employee.id,
        amount: 95000,
        currencyCode: 'USD',
        payFrequency: 'ANNUAL',
        effectiveFrom: '2024-05-12',
        effectiveTo: null,
        reason: 'Initial salary',
        createdBy: 'system'
      }
    }));
    const fixture = TestBed.createComponent(EmployeeDetailsComponent);
    const component = fixture.componentInstance;
    component.employeeForm = {
      employeeNumber: 'EMP-NEW',
      firstName: 'Taylor',
      lastName: 'Example',
      email: 'new@example.com',
      countryCode: 'US',
      departmentCode: 'ENG',
      jobTitle: 'Engineer',
      employmentStatus: 'ACTIVE',
      hireDate: new Date(2024, 4, 12)
    };
    component.initialSalary = {
      amount: 95000,
      currencyCode: 'USD',
      payFrequency: 'ANNUAL',
      effectiveFrom: new Date(2024, 4, 12),
      reason: 'Initial salary'
    };
    fixture.detectChanges();
    const form = fixture.debugElement.query(By.css('form'));
    const ngForm = form.injector.get(NgForm);

    component.submit(ngForm);

    expect(apiService.onboardEmployee).toHaveBeenCalledWith({
      employee: {
        employeeNumber: 'EMP-NEW',
        firstName: 'Taylor',
        lastName: 'Example',
        email: 'new@example.com',
        countryCode: 'US',
        departmentCode: 'ENG',
        jobTitle: 'Engineer',
        employmentStatus: 'ACTIVE',
        hireDate: '2024-05-12'
      },
      initialSalary: {
        amount: 95000,
        currencyCode: 'USD',
        payFrequency: 'ANNUAL',
        effectiveFrom: '2024-05-12',
        reason: 'Initial salary'
      }
    });
  });
});
