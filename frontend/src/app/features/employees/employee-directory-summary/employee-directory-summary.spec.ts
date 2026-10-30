import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';
import { EmployeePageResponse } from '../../../core/models/payflow';
import { PayflowApiService } from '../../../core/services/payflow-api.service';
import { ToastService } from '../../../core/ui/toast/toast.service';
import { EmployeeDirectorySummaryComponent } from './employee-directory-summary';

describe('EmployeeDirectorySummaryComponent', () => {
  const pageResponse: EmployeePageResponse = {
    content: [],
    totalElements: 0,
    totalPages: 0,
    number: 0,
    size: 10
  };
  const employee = {
    id: 8,
    employeeNumber: 'EMP-8',
    firstName: 'Alex',
    lastName: 'Employee',
    email: 'alex@example.com',
    countryCode: 'US',
    departmentCode: 'ENG',
    jobTitle: 'Engineer',
    employmentStatus: 'ACTIVE',
    hireDate: '2024-01-15'
  };
  const apiService = {
    getEmployees: vi.fn().mockReturnValue(of(pageResponse)),
    deleteEmployee: vi.fn().mockReturnValue(of(undefined))
  };
  const toastService = {
    show: vi.fn()
  };
  const router = {
    navigate: vi.fn()
  };

  beforeEach(async () => {
    apiService.getEmployees.mockClear();
    apiService.getEmployees.mockReturnValue(of(pageResponse));
    toastService.show.mockClear();
    router.navigate.mockClear();

    await TestBed.configureTestingModule({
      imports: [EmployeeDirectorySummaryComponent],
      providers: [
        { provide: PayflowApiService, useValue: apiService },
        { provide: ToastService, useValue: toastService },
        { provide: Router, useValue: router },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { queryParamMap: { get: () => '0' } } }
        }
      ]
    }).compileComponents();
  });

  it('renders employees from the server-paginated response', () => {
    apiService.getEmployees.mockReturnValue(of({
      ...pageResponse,
      content: [employee],
      totalElements: 1,
      totalPages: 1
    }));
    const fixture = TestBed.createComponent(EmployeeDirectorySummaryComponent);
    fixture.detectChanges();

    expect(apiService.getEmployees).toHaveBeenCalledWith(0, 10);
    expect(fixture.componentInstance.totalElements()).toBe(1);
    expect(fixture.nativeElement.textContent).toContain('EMP-8');
    expect(fixture.nativeElement.textContent).toContain('Alex Employee');
  });

  it('opens the details page for the selected employee', () => {
    const fixture = TestBed.createComponent(EmployeeDirectorySummaryComponent);
    const component = fixture.componentInstance;

    component.pageIndex = 2;
    component.editEmployee({
      id: 8,
      employeeNumber: 'EMP-8',
      firstName: 'Alex',
      lastName: 'Employee',
      email: 'alex@example.com',
      countryCode: 'US',
      departmentCode: 'ENG',
      jobTitle: 'Engineer',
      employmentStatus: 'ACTIVE',
      hireDate: '2024-01-15'
    });

    expect(router.navigate).toHaveBeenCalledWith(
      ['/employees', 8, 'edit'],
      { queryParams: { page: 2 } }
    );
  });

  it('reloads the current page after a confirmed delete', () => {
    const fixture = TestBed.createComponent(EmployeeDirectorySummaryComponent);
    const component = fixture.componentInstance;
    const confirmSpy = vi.spyOn(window, 'confirm').mockReturnValue(true);
    fixture.detectChanges();

    component.deleteEmployee(employee);

    expect(apiService.deleteEmployee).toHaveBeenCalledWith(8);
    expect(apiService.getEmployees).toHaveBeenCalledTimes(2);
    expect(toastService.show).toHaveBeenCalledWith('Employee deleted successfully.');
    confirmSpy.mockRestore();
  });
});
