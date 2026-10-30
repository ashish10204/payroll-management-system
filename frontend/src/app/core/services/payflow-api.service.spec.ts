import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { environment } from '../../../environments/environment';
import { EmployeeResponse } from '../models/payflow';
import { AuthService } from './auth.service';
import { PayflowApiService } from './payflow-api.service';

describe('PayflowApiService', () => {
  let apiService: PayflowApiService;
  let httpTestingController: HttpTestingController;

  const employees: EmployeeResponse[] = [
    {
      id: 1,
      employeeNumber: 'EMP-1',
      firstName: 'Alex',
      lastName: 'One',
      email: 'alex.one@example.com',
      countryCode: 'US',
      departmentCode: 'ENG',
      jobTitle: 'Engineer',
      employmentStatus: 'ACTIVE',
      hireDate: '2024-01-15'
    },
    {
      id: 2,
      employeeNumber: 'EMP-2',
      firstName: 'Sam',
      lastName: 'Two',
      email: 'sam.two@example.com',
      countryCode: 'US',
      departmentCode: 'ENG',
      jobTitle: 'Engineer',
      employmentStatus: 'ACTIVE',
      hireDate: '2024-02-15'
    },
    {
      id: 3,
      employeeNumber: 'EMP-3',
      firstName: 'Taylor',
      lastName: 'Three',
      email: 'taylor.three@example.com',
      countryCode: 'US',
      departmentCode: 'ENG',
      jobTitle: 'Engineer',
      employmentStatus: 'ACTIVE',
      hireDate: '2024-03-15'
    }
  ];

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: AuthService, useValue: { bearerAuthHeaderValue: 'Bearer test-token' } }
      ]
    });
    apiService = TestBed.inject(PayflowApiService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpTestingController.verify());

  it('uses the paginated employee API response', () => {
    const response = {
      content: employees.slice(0, 1),
      totalElements: 3,
      totalPages: 3,
      number: 0,
      size: 1
    };

    apiService.getEmployees(0, 1).subscribe((page) => {
      expect(page).toEqual(response);
    });

    const request = httpTestingController.expectOne(`${environment.apiUrl}/employees?page=0&size=1`);
    expect(request.request.headers.get('Authorization')).toBe('Bearer test-token');
    request.flush(response);
  });

  it('loads employee rows from a legacy array response while paging locally', () => {
    apiService.getEmployees(1, 2).subscribe((page) => {
      expect(page.content).toEqual([employees[2]]);
      expect(page.totalElements).toBe(3);
      expect(page.totalPages).toBe(2);
      expect(page.number).toBe(1);
      expect(page.size).toBe(2);
    });

    const request = httpTestingController.expectOne(`${environment.apiUrl}/employees?page=1&size=2`);
    request.flush(employees);
  });

  it('passes dashboard filters to both report endpoints', () => {
    apiService.getHeadcount({
      country: 'US',
      department: 'ENG',
      employmentStatus: 'ACTIVE'
    }).subscribe();
    apiService.getCompensation({
      country: 'US',
      department: 'ENG',
      employmentStatus: 'ACTIVE',
      asOfDate: '2026-10-30'
    }).subscribe();

    const headcount = httpTestingController.expectOne((request) =>
      request.url === `${environment.apiUrl}/reports/headcount`
    );
    expect(headcount.request.params.get('country')).toBe('US');
    expect(headcount.request.params.get('department')).toBe('ENG');
    expect(headcount.request.params.get('employmentStatus')).toBe('ACTIVE');

    const compensation = httpTestingController.expectOne((request) =>
      request.url === `${environment.apiUrl}/reports/compensation`
    );
    expect(compensation.request.params.get('asOfDate')).toBe('2026-10-30');

    headcount.flush({
      totalEmployees: 1,
      byCountry: { US: 1 },
      byDepartment: { ENG: 1 },
      byEmploymentStatus: { ACTIVE: 1 }
    });
    compensation.flush({
      totalEmployees: 1,
      matchedEmployees: 1,
      currencyCode: 'USD',
      payFrequency: 'ANNUAL',
      groups: []
    });
  });
});
