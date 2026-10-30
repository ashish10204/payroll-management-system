import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  CompensationReportResponse,
  CurrentSalaryResponse,
  EmployeeCreateRequest,
  EmployeeOnboardingRequest,
  EmployeeOnboardingResponse,
  EmployeePageResponse,
  EmployeeResponse,
  EmployeeUpdateRequest,
  HeadcountReportResponse,
  ReferenceDataResponse,
  ReportFilters,
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
    const authHeader = this.authService.bearerAuthHeaderValue;
    return new HttpHeaders({
      Authorization: authHeader,
      'Content-Type': 'application/json'
    });
  }

  getHeadcount(filters: ReportFilters = {}): Observable<HeadcountReportResponse> {
    return this.http.get<HeadcountReportResponse>(`${this.baseUrl}/reports/headcount`, {
      params: this.reportParams(filters),
      headers: this.authHeaders()
    });
  }

  getCompensation(filters: ReportFilters = {}): Observable<CompensationReportResponse> {
    return this.http.get<CompensationReportResponse>(`${this.baseUrl}/reports/compensation`, {
      params: this.reportParams(filters),
      headers: this.authHeaders()
    });
  }

  private reportParams(filters: ReportFilters): HttpParams {
    let params = new HttpParams();
    Object.entries(filters).forEach(([key, value]) => {
      if (value?.trim()) {
        params = params.set(key, value);
      }
    });
    return params;
  }

  getEmployees(page: number, size: number): Observable<EmployeePageResponse> {
    return this.http.get<EmployeePageResponse | EmployeeResponse[]>(`${this.baseUrl}/employees`, {
      params: { page, size },
      headers: this.authHeaders()
    }).pipe(map((response) => {
      if (Array.isArray(response)) {
        const start = page * size;
        return {
          content: response.slice(start, start + size),
          totalElements: response.length,
          totalPages: Math.ceil(response.length / size),
          number: page,
          size
        };
      }

      if (!response || !Array.isArray(response.content)) {
        throw new Error('Employee API returned an invalid page response.');
      }

      return response;
    }));
  }

  getReferenceData(): Observable<ReferenceDataResponse> {
    return this.http.get<ReferenceDataResponse>(`${this.baseUrl}/reference-data`, {
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

  onboardEmployee(request: EmployeeOnboardingRequest): Observable<EmployeeOnboardingResponse> {
    return this.http.post<EmployeeOnboardingResponse>(`${this.baseUrl}/employees/onboard`, request, {
      headers: this.authHeaders()
    });
  }

  updateEmployee(id: number, request: EmployeeUpdateRequest): Observable<EmployeeResponse> {
    return this.http.put<EmployeeResponse>(`${this.baseUrl}/employees/${id}`, request, {
      headers: this.authHeaders()
    });
  }

  deleteEmployee(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/employees/${id}`, {
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
