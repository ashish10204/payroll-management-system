import { CommonModule } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { forkJoin } from 'rxjs';
import {
  CompensationReportResponse,
  HeadcountReportResponse,
  ReferenceDataResponse,
  ReportFilters
} from '../../core/models/payflow';
import { PayflowApiService } from '../../core/services/payflow-api.service';

interface DashboardFilters {
  country: string;
  department: string;
  employmentStatus: string;
  asOfDate: string;
}

const emptyReferenceData: ReferenceDataResponse = {
  countries: [],
  departments: [],
  jobTitles: [],
  employmentStatuses: []
};

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatProgressSpinnerModule,
    MatSelectModule
  ],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss'
})
export class DashboardComponent implements OnInit {
  readonly loading = signal(true);
  readonly errorMessage = signal('');
  readonly headcount = signal<HeadcountReportResponse | null>(null);
  readonly compensation = signal<CompensationReportResponse | null>(null);
  readonly referenceData = signal<ReferenceDataResponse>(emptyReferenceData);
  readonly filters: DashboardFilters = {
    country: '',
    department: '',
    employmentStatus: '',
    asOfDate: this.today()
  };

  constructor(private readonly apiService: PayflowApiService) {}

  ngOnInit(): void {
    this.apiService.getReferenceData().subscribe({
      next: (data) => this.referenceData.set(data),
      error: () => this.referenceData.set(emptyReferenceData)
    });
    this.loadReports();
  }

  applyFilters(): void {
    this.loadReports();
  }

  clearFilters(): void {
    this.filters.country = '';
    this.filters.department = '';
    this.filters.employmentStatus = '';
    this.filters.asOfDate = this.today();
    this.loadReports();
  }

  countryEntries(): Array<[string, number]> {
    return Object.entries(this.headcount()?.byCountry ?? {});
  }

  departmentEntries(): Array<[string, number]> {
    return Object.entries(this.headcount()?.byDepartment ?? {});
  }

  distributionWidth(value: number): number {
    const total = this.headcount()?.totalEmployees ?? 0;
    return total > 0 ? Math.max(6, Math.round((value / total) * 100)) : 0;
  }

  private loadReports(): void {
    this.loading.set(true);
    this.errorMessage.set('');
    const filters = this.reportFilters();
    const headcountFilters: ReportFilters = {
      country: filters.country,
      department: filters.department,
      employmentStatus: filters.employmentStatus
    };

    forkJoin({
      headcount: this.apiService.getHeadcount(headcountFilters),
      compensation: this.apiService.getCompensation(filters)
    }).subscribe({
      next: ({ headcount, compensation }) => {
        this.headcount.set(headcount);
        this.compensation.set(compensation);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.errorMessage.set('Unable to load dashboard metrics. Check your connection and try again.');
      }
    });
  }

  private reportFilters(): ReportFilters {
    return {
      country: this.filters.country,
      department: this.filters.department,
      employmentStatus: this.filters.employmentStatus,
      asOfDate: this.filters.asOfDate
    };
  }

  private today(): string {
    const date = new Date();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${date.getFullYear()}-${month}-${day}`;
  }
}
