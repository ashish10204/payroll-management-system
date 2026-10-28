import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  CompensationReportResponse,
  CurrentSalaryResponse,
  EmployeeCreateRequest,
  EmployeeResponse,
  HeadcountReportResponse,
  SalaryChangeRequest,
  SalaryHistoryItem
} from '../models/payflow';
import { AuthService } from './auth.service';

@Injectable({ providedIn: 'root' })
export class PayflowApiService {
  private readonly http = inject(HttpClient);
  private readonly authService = inject(AuthService);
  private readonly baseUrl = environment.apiUrl;

  private authHeaders(): HttpHeaders {
    const authHeader = this.authService.basicAuthHeaderValue;
    return new HttpHeaders({
      Authorization: authHeader,
      'Content-Type': 'application/json'
    });
  }

  getHeadcount(): Observable<HeadcountReportResponse> {
    return this.http.get<HeadcountReportResponse>(`${this.baseUrl}/reports/headcount`, {
      headers: this.authHeaders()
    });
  }

  getCompensation(): Observable<CompensationReportResponse> {
    return this.http.get<CompensationReportResponse>(`${this.baseUrl}/reports/compensation`, {
      headers: this.authHeaders()
    });
  }

  getEmployees(): Observable<EmployeeResponse[]> {
    return this.http.get<EmployeeResponse[]>(`${this.baseUrl}/employees`, {
      headers: this.authHeaders()
    });
  }

  getEmployee(id: number): Observable<EmployeeResponse> {
    return this.http.get<EmployeeResponse>(`${this.baseUrl}/employees/${id}`, {
      headers: this.authHeaders()
    });
  }

  createEmployee(request: EmployeeCreateRequest): Observable<EmployeeResponse> {
    return this.http.post<EmployeeResponse>(`${this.baseUrl}/employees`, request, {
      headers: this.authHeaders()
    });
  }

  getCurrentSalary(employeeId: number): Observable<CurrentSalaryResponse> {
    return this.http.get<CurrentSalaryResponse>(`${this.baseUrl}/employees/${employeeId}/salary/current`, {
      headers: this.authHeaders()
    });
  }

  getSalaryHistory(employeeId: number): Observable<SalaryHistoryItem[]> {
    return this.http.get<SalaryHistoryItem[]>(`${this.baseUrl}/employees/${employeeId}/salary/history`, {
      headers: this.authHeaders()
    });
  }

  createSalaryChange(employeeId: number, request: Omit<SalaryChangeRequest, 'employeeId'>): Observable<SalaryHistoryItem> {
    return this.http.post<SalaryHistoryItem>(`${this.baseUrl}/employees/${employeeId}/salary`, {
      ...request,
      employeeId
    }, {
      headers: this.authHeaders()
    });
  }

  exportEmployeesCsv(): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/reports/employees/export`, {
      headers: this.authHeaders(),
      responseType: 'blob'
    });
  }
}
