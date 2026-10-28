export interface HeadcountReportResponse {
  totalEmployees: number;
  byCountry: Record<string, number>;
  byDepartment: Record<string, number>;
  byStatus: Record<string, number>;
}

export interface CompensationGroupSummary {
  currencyCode: string;
  payFrequency: string;
  employeeCount: number;
  minimum: number;
  average: number;
  maximum: number;
  total: number;
}

export interface CompensationReportResponse {
  totalEmployees: number;
  matchingEmployees: number;
  currencyCode: string;
  payFrequency: string;
  groups: CompensationGroupSummary[];
}

export interface EmployeeResponse {
  id: number;
  employeeNumber: string;
  firstName: string;
  lastName: string;
  email: string;
  countryCode: string;
  departmentCode: string;
  jobTitle: string;
  employmentStatus: string;
  hireDate: string | null;
}

export interface EmployeeCreateRequest {
  employeeNumber: string;
  firstName: string;
  lastName: string;
  email: string;
  countryCode: string;
  departmentCode: string;
  jobTitle: string;
  employmentStatus: string;
  hireDate: string;
}

export interface SalaryChangeRequest {
  employeeId: number;
  amount: number;
  currencyCode: string;
  payFrequency: string;
  effectiveFrom: string;
  effectiveTo?: string | null;
  reason: string;
}

export interface CurrentSalaryResponse {
  employeeId: number;
  amount: number;
  currencyCode: string;
  payFrequency: string;
  effectiveFrom: string;
  effectiveTo: string | null;
  reason: string;
}

export interface SalaryHistoryItem {
  salaryRecordId: number;
  employeeId: number;
  amount: number;
  currencyCode: string;
  payFrequency: string;
  effectiveFrom: string;
  effectiveTo: string | null;
  reason: string;
  createdBy: string;
}
