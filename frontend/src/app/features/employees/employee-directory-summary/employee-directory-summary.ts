import { CommonModule } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { ActivatedRoute, Router } from '@angular/router';
import { EmployeeResponse } from '../../../core/models/payflow';
import { PayflowApiService } from '../../../core/services/payflow-api.service';
import { ToastService } from '../../../core/ui/toast/toast.service';

@Component({
  selector: 'app-employee-directory-summary',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatPaginatorModule, MatTableModule],
  templateUrl: './employee-directory-summary.html',
  styleUrl: './employee-directory-summary.scss'
})
export class EmployeeDirectorySummaryComponent implements OnInit {
  readonly displayedColumns = ['employeeNumber', 'name', 'countryCode', 'departmentCode', 'status', 'actions'];
  readonly pageSizeOptions = [10, 25, 50, 100];
  readonly employees = signal<EmployeeResponse[]>([]);
  pageIndex = 0;
  pageSize = 10;
  readonly totalElements = signal(0);

  constructor(
    private readonly apiService: PayflowApiService,
    private readonly toastService: ToastService,
    private readonly router: Router,
    private readonly route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    const requestedPage = Number(this.route.snapshot.queryParamMap.get('page') ?? 0);
    this.pageIndex = Number.isInteger(requestedPage) && requestedPage >= 0 ? requestedPage : 0;
    this.loadEmployees();
  }

  loadEmployees(): void {
    this.apiService.getEmployees(this.pageIndex, this.pageSize).subscribe({
      next: (response) => {
        this.employees.set(response.content);
        this.totalElements.set(response.totalElements);
      },
      error: () => this.toastService.show('Unable to load employees. Please try again.', 'error')
    });
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadEmployees();
  }

  editEmployee(employee: EmployeeResponse): void {
    this.router.navigate(['/employees', employee.id, 'edit'], {
      queryParams: { page: this.pageIndex }
    });
  }

  addEmployee(): void {
    this.router.navigate(['/employees/new']);
  }

  deleteEmployee(employee: EmployeeResponse): void {
    if (!window.confirm(`Delete employee ${employee.employeeNumber}?`)) {
      return;
    }

    this.apiService.deleteEmployee(employee.id).subscribe({
      next: () => {
        if (this.employees().length === 1 && this.pageIndex > 0) {
          this.pageIndex--;
        }
        this.loadEmployees();
        this.toastService.show('Employee deleted successfully.');
      },
      error: (error: unknown) => this.toastService.show(this.errorMessage(error), 'error')
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
    return 'Unable to delete employee. Please try again.';
  }
}
