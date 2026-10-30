import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { ActivatedRoute, Router } from '@angular/router';
import {
  EmployeeCreateRequest,
  EmployeeOnboardingRequest,
  EmployeeResponse,
  EmployeeUpdateRequest,
  InitialSalaryRequest,
  ReferenceDataResponse,
  SalaryHistoryItem
} from '../../../core/models/payflow';
import { PayflowApiService } from '../../../core/services/payflow-api.service';
import { ToastService } from '../../../core/ui/toast/toast.service';

type EmployeeForm = Omit<EmployeeCreateRequest, 'hireDate'> & { hireDate: Date | null };

const emptyReferenceData: ReferenceDataResponse = {
  countries: [],
  departments: [],
  jobTitles: [],
  employmentStatuses: []
};

@Component({
  selector: 'app-employee-details',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatButtonModule,
    MatCardModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule
  ],
  templateUrl: './employee-details.html',
  styleUrl: './employee-details.scss'
})
export class EmployeeDetailsComponent implements OnInit {
  readonly payFrequencies = ['HOURLY', 'MONTHLY', 'ANNUAL'];
  readonly employeeId: number | null;
  readonly returnPage: number;
  employee: EmployeeResponse | null = null;
  referenceData: ReferenceDataResponse = emptyReferenceData;
  loading = false;
  salarySaving = false;
  salaryHistory: SalaryHistoryItem[] = [];

  initialSalary: Omit<InitialSalaryRequest, 'effectiveFrom'> & { effectiveFrom: Date | null } = {
    amount: 0,
    currencyCode: 'USD',
    payFrequency: 'ANNUAL',
    effectiveFrom: new Date(),
    reason: 'Initial salary'
  };

  salaryChange: Omit<InitialSalaryRequest, 'effectiveFrom'> & { effectiveFrom: Date | null } = {
    amount: 0,
    currencyCode: 'USD',
    payFrequency: 'ANNUAL',
    effectiveFrom: new Date(),
    reason: ''
  };

  employeeForm: EmployeeForm = {
    employeeNumber: '',
    firstName: '',
    lastName: '',
    email: '',
    countryCode: '',
    departmentCode: '',
    jobTitle: '',
    employmentStatus: '',
    hireDate: null
  };

  constructor(
    private readonly apiService: PayflowApiService,
    private readonly toastService: ToastService,
    private readonly router: Router,
    private readonly changeDetector: ChangeDetectorRef,
    route: ActivatedRoute
  ) {
    const routeId = route.snapshot.paramMap.get('id');
    this.employeeId = routeId === null ? null : Number(routeId);
    const page = Number(route.snapshot.queryParamMap.get('page') ?? 0);
    this.returnPage = Number.isInteger(page) && page >= 0 ? page : 0;
  }

  ngOnInit(): void {
    this.apiService.getReferenceData().subscribe({
      next: (data) => {
        this.referenceData = data;
        this.changeDetector.markForCheck();
      },
      error: () => {
        this.toastService.show('Unable to load employee form options.', 'error');
        this.changeDetector.markForCheck();
      }
    });

    if (this.employeeId !== null) {
      if (!Number.isInteger(this.employeeId) || this.employeeId < 1) {
        this.toastService.show('Invalid employee identifier.', 'error');
        this.cancel();
        return;
      }
      this.loadEmployee(this.employeeId);
      this.loadSalaryHistory(this.employeeId);
    }
  }

  submit(form: NgForm): void {
    if (form.invalid) {
      form.form.markAllAsTouched();
      this.toastService.show('Please complete all required fields and enter a valid email address.', 'error');
      return;
    }

    if (!this.employeeForm.hireDate) {
      this.toastService.show('Hire date is required.', 'error');
      return;
    }

    const hireDate = this.toLocalDateString(this.employeeForm.hireDate);
    this.loading = true;

    if (this.employeeId === null) {
      const request: EmployeeCreateRequest = { ...this.employeeForm, hireDate };
      const onboardingRequest: EmployeeOnboardingRequest = {
        employee: request,
        initialSalary: {
          ...this.initialSalary,
          effectiveFrom: this.toLocalDateString(this.initialSalary.effectiveFrom ?? new Date())
        }
      };
      this.apiService.onboardEmployee(onboardingRequest).subscribe({
        next: () => {
          this.toastService.show('Employee and initial salary added successfully.');
          this.router.navigate(['/employees']);
        },
        error: (error: unknown) => {
          this.loading = false;
          this.toastService.show(this.errorMessage(error), 'error');
          this.changeDetector.markForCheck();
        }
      });
      return;
    }

    const request: EmployeeUpdateRequest = {
      firstName: this.employeeForm.firstName,
      lastName: this.employeeForm.lastName,
      email: this.employeeForm.email,
      countryCode: this.employeeForm.countryCode,
      departmentCode: this.employeeForm.departmentCode,
      jobTitle: this.employeeForm.jobTitle,
      employmentStatus: this.employeeForm.employmentStatus,
      hireDate
    };
    this.apiService.updateEmployee(this.employeeId, request).subscribe({
      next: () => {
        this.toastService.show('Employee updated successfully.');
        this.router.navigate(['/employees'], { queryParams: { page: this.returnPage } });
      },
      error: (error: unknown) => {
        this.loading = false;
        this.toastService.show(this.errorMessage(error), 'error');
        this.changeDetector.markForCheck();
      }
    });
  }

  submitSalaryChange(form: NgForm): void {
    if (form.invalid) {
      form.form.markAllAsTouched();
      this.toastService.show('Enter a valid salary, currency, pay frequency, effective date, and reason.', 'error');
      return;
    }

    if (this.employeeId === null || !this.salaryChange.effectiveFrom) {
      this.toastService.show('Employee and salary effective date are required.', 'error');
      return;
    }

    this.salarySaving = true;
    this.apiService.createSalaryChange(this.employeeId, {
      amount: this.salaryChange.amount,
      currencyCode: this.salaryChange.currencyCode,
      payFrequency: this.salaryChange.payFrequency,
      effectiveFrom: this.toLocalDateString(this.salaryChange.effectiveFrom),
      reason: this.salaryChange.reason
    }).subscribe({
      next: (salary) => {
        this.salaryHistory = [salary, ...this.salaryHistory]
          .sort((left, right) => right.effectiveFrom.localeCompare(left.effectiveFrom));
        this.salaryChange = {
          amount: 0,
          currencyCode: salary.currencyCode,
          payFrequency: salary.payFrequency,
          effectiveFrom: new Date(),
          reason: ''
        };
        this.salarySaving = false;
        this.toastService.show('Salary change recorded successfully.');
        this.changeDetector.markForCheck();
      },
      error: (error: unknown) => {
        this.salarySaving = false;
        this.toastService.show(this.errorMessage(error), 'error');
        this.changeDetector.markForCheck();
      }
    });
  }

  currentSalary(): SalaryHistoryItem | null {
    const today = this.toLocalDateString(new Date());
    return this.salaryHistory.find((salary) =>
      salary.effectiveFrom <= today && (!salary.effectiveTo || salary.effectiveTo >= today)
    ) ?? null;
  }

  cancel(): void {
    this.router.navigate(['/employees'], {
      queryParams: this.returnPage > 0 ? { page: this.returnPage } : {}
    });
  }

  private loadEmployee(id: number): void {
    this.loading = true;
    this.apiService.getEmployee(id).subscribe({
      next: (employee) => {
        this.employee = employee;
        this.employeeForm = {
          employeeNumber: employee.employeeNumber,
          firstName: employee.firstName,
          lastName: employee.lastName,
          email: employee.email,
          countryCode: employee.countryCode,
          departmentCode: employee.departmentCode,
          jobTitle: employee.jobTitle,
          employmentStatus: employee.employmentStatus,
          hireDate: employee.hireDate ? this.parseLocalDate(employee.hireDate) : null
        };
        this.loading = false;
        this.changeDetector.markForCheck();
      },
      error: (error: unknown) => {
        this.loading = false;
        this.toastService.show(this.errorMessage(error), 'error');
        this.cancel();
        this.changeDetector.markForCheck();
      }
    });
  }

  private loadSalaryHistory(id: number): void {
    this.apiService.getSalaryHistory(id).subscribe({
      next: (history) => {
        this.salaryHistory = history;
        this.changeDetector.markForCheck();
      },
      error: () => {
        this.toastService.show('Unable to load salary history.', 'error');
        this.changeDetector.markForCheck();
      }
    });
  }

  private errorMessage(error: unknown): string {
    if (typeof error === 'object' && error !== null && 'error' in error) {
      const body: unknown = error.error;
      if (typeof body === 'string' && body.trim()) {
        return body;
      }
      if (typeof body === 'object' && body !== null) {
        if ('detail' in body && typeof body.detail === 'string') {
          return body.detail;
        }
        if ('message' in body && typeof body.message === 'string') {
          return body.message;
        }
      }
    }
    return 'Unable to save employee. Please check the details and try again.';
  }

  private toLocalDateString(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  private parseLocalDate(date: string): Date {
    const [year, month, day] = date.split('-').map(Number);
    return new Date(year, month - 1, day);
  }
}
