import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { forkJoin } from 'rxjs';
import { CompensationReportResponse, HeadcountReportResponse } from '../../core/models/payflow';
import { PayflowApiService } from '../../core/services/payflow-api.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatProgressSpinnerModule],
  template: `
    <section class="dashboard">
      <h2>Dashboard</h2>

      <div class="loading" *ngIf="loading">
        <mat-spinner diameter="32"></mat-spinner>
      </div>

      <div class="error" *ngIf="errorMessage">{{ errorMessage }}</div>

      <div class="grid" *ngIf="!loading && !errorMessage">
        <mat-card>
          <mat-card-title>Total employees</mat-card-title>
          <mat-card-content>{{ headcount?.totalEmployees ?? 0 }}</mat-card-content>
        </mat-card>

        <mat-card>
          <mat-card-title>By country</mat-card-title>
          <mat-card-content>
            <div *ngFor="let item of countryEntries">{{ item[0] }}: {{ item[1] }}</div>
          </mat-card-content>
        </mat-card>

        <mat-card>
          <mat-card-title>Compensation</mat-card-title>
          <mat-card-content>
            <div *ngIf="compensation?.groups?.length; else emptyState">
              <div *ngFor="let group of compensation?.groups ?? []">
                {{ group.currencyCode }} / {{ group.payFrequency }} — avg {{ group.average | number: '1.2-2' }}
              </div>
            </div>
            <ng-template #emptyState>No compensation groups available.</ng-template>
          </mat-card-content>
        </mat-card>
      </div>
    </section>
  `,
  styles: [
    `
      .dashboard {
        padding: 1.5rem;
      }

      .grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
        gap: 1rem;
        margin-top: 1rem;
      }

      .loading {
        display: flex;
        justify-content: center;
        margin-top: 2rem;
      }

      .error {
        color: #c62828;
        margin-top: 1rem;
      }
    `
  ]
})
export class DashboardComponent implements OnInit {
  loading = true;
  errorMessage = '';
  headcount: HeadcountReportResponse | null = null;
  compensation: CompensationReportResponse | null = null;

  constructor(private readonly apiService: PayflowApiService) {}

  ngOnInit(): void {
    forkJoin({
      headcount: this.apiService.getHeadcount(),
      compensation: this.apiService.getCompensation()
    }).subscribe({
      next: ({ headcount, compensation }) => {
        this.headcount = headcount;
        this.compensation = compensation;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.errorMessage = 'Unable to load the dashboard metrics.';
      }
    });
  }

  get countryEntries(): Array<[string, number]> {
    return Object.entries(this.headcount?.byCountry ?? {});
  }
}
