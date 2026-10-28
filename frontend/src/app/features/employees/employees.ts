import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatTableModule } from '@angular/material/table';
import { EmployeeCreateRequest, EmployeeResponse } from '../../core/models/payflow';
import { PayflowApiService } from '../../core/services/payflow-api.service';

@Component({
  selector: 'app-employees',
  standalone: true,
  imports: [CommonModule, FormsModule, MatCardModule, MatFormFieldModule, MatInputModule, MatTableModule, MatButtonModule],
  template: `
    <section class="employees-shell">
      <h2>Employee directory</h2>

      <mat-card>
        <h3>Create employee</h3>
        <form class="create-form" (ngSubmit)="createEmployee()">
          <mat-form-field appearance="outline">
            <mat-label>Employee number</mat-label>
            <input matInput [(ngModel)]="employeeForm.employeeNumber" name="employeeNumber" />
          </mat-form-field>
          <mat-form-field appearance="outline">
            <mat-label>First name</mat-label>
            <input matInput [(ngModel)]="employeeForm.firstName" name="firstName" />
          </mat-form-field>
          <mat-form-field appearance="outline">
            <mat-label>Last name</mat-label>
            <input matInput [(ngModel)]="employeeForm.lastName" name="lastName" />
          </mat-form-field>
          <mat-form-field appearance="outline">
            <mat-label>Email</mat-label>
            <input matInput [(ngModel)]="employeeForm.email" name="email" />
          </mat-form-field>
          <mat-form-field appearance="outline">
            <mat-label>Country</mat-label>
            <input matInput [(ngModel)]="employeeForm.countryCode" name="countryCode" />
          </mat-form-field>
          <mat-form-field appearance="outline">
            <mat-label>Department</mat-label>
            <input matInput [(ngModel)]="employeeForm.departmentCode" name="departmentCode" />
          </mat-form-field>
          <mat-form-field appearance="outline">
            <mat-label>Job title</mat-label>
            <input matInput [(ngModel)]="employeeForm.jobTitle" name="jobTitle" />
          </mat-form-field>
          <mat-form-field appearance="outline">
            <mat-label>Status</mat-label>
            <input matInput [(ngModel)]="employeeForm.employmentStatus" name="employmentStatus" />
          </mat-form-field>
          <mat-form-field appearance="outline">
            <mat-label>Hire date</mat-label>
            <input matInput type="date" [(ngModel)]="employeeForm.hireDate" name="hireDate" />
          </mat-form-field>

          <button mat-flat-button color="primary" type="submit">Save employee</button>
        </form>
      </mat-card>

      <table mat-table [dataSource]="employees" class="mat-elevation-z2">
        <ng-container matColumnDef="employeeNumber">
          <th mat-header-cell *matHeaderCellDef>Employee</th>
          <td mat-cell *matCellDef="let employee">{{ employee.employeeNumber }}</td>
        </ng-container>

        <ng-container matColumnDef="name">
          <th mat-header-cell *matHeaderCellDef>Name</th>
          <td mat-cell *matCellDef="let employee">{{ employee.firstName }} {{ employee.lastName }}</td>
        </ng-container>

        <ng-container matColumnDef="countryCode">
          <th mat-header-cell *matHeaderCellDef>Country</th>
          <td mat-cell *matCellDef="let employee">{{ employee.countryCode }}</td>
        </ng-container>

        <ng-container matColumnDef="departmentCode">
          <th mat-header-cell *matHeaderCellDef>Department</th>
          <td mat-cell *matCellDef="let employee">{{ employee.departmentCode }}</td>
        </ng-container>

        <ng-container matColumnDef="status">
          <th mat-header-cell *matHeaderCellDef>Status</th>
          <td mat-cell *matCellDef="let employee">{{ employee.employmentStatus }}</td>
        </ng-container>

        <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
        <tr mat-row *matRowDef="let row; columns: displayedColumns"></tr>
      </table>
    </section>
  `,
  styles: [
    `
      .employees-shell {
        padding: 1.5rem;
      }

      .create-form {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
        gap: 1rem;
        margin-bottom: 1rem;
      }

      table {
        width: 100%;
        margin-top: 1rem;
      }
    `
  ]
})
export class EmployeesComponent implements OnInit {
  displayedColumns = ['employeeNumber', 'name', 'countryCode', 'departmentCode', 'status'];
  employees: EmployeeResponse[] = [];

  employeeForm: EmployeeCreateRequest = {
    employeeNumber: '',
    firstName: '',
    lastName: '',
    email: '',
    countryCode: 'US',
    departmentCode: 'Engineering',
    jobTitle: 'Engineer',
    employmentStatus: 'ACTIVE',
    hireDate: '2026-01-15'
  };

  constructor(private readonly apiService: PayflowApiService) {}

  ngOnInit(): void {
    this.loadEmployees();
  }

  loadEmployees(): void {
    this.apiService.getEmployees().subscribe({
      next: (employees) => {
        this.employees = employees;
      },
      error: () => {
        this.employees = [];
      }
    });
  }

  createEmployee(): void {
    this.apiService.createEmployee(this.employeeForm).subscribe({
      next: () => {
        this.loadEmployees();
        this.employeeForm = {
          employeeNumber: '',
          firstName: '',
          lastName: '',
          email: '',
          countryCode: 'US',
          departmentCode: 'Engineering',
          jobTitle: 'Engineer',
          employmentStatus: 'ACTIVE',
          hireDate: '2026-01-15'
        };
      },
      error: () => {
        console.error('Unable to create employee');
      }
    });
  }
}
