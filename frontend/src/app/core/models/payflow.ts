export interface LookupOption {
  code: string;
  label: string;
}

export interface ReferenceDataResponse {
  countries: LookupOption[];
  departments: LookupOption[];
  jobTitles: LookupOption[];
  employmentStatuses: LookupOption[];
}

export interface HeadcountReportResponse {
  totalEmployees: number;
  byCountry: Record<string, number>;
  byDepartment: Record<string, number>;
  byEmploymentStatus: Record<string, number>;
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
  matchedEmployees: number;
  currencyCode: string;
  payFrequency: string;
  groups: CompensationGroupSummary[];
}

export interface ReportFilters {
  country?: string;
  department?: string;
  employmentStatus?: string;
  jobTitle?: string;
  currency?: string;
  payFrequency?: string;
  asOfDate?: string;
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

export interface EmployeePageResponse {
  content: EmployeeResponse[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
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

export interface EmployeeUpdateRequest {
  firstName: string;
  lastName: string;
  email: string;
  countryCode: string;
  departmentCode: string;
  jobTitle: string;
  employmentStatus: string;
  hireDate: string;
}

export interface InitialSalaryRequest {
  amount: number;
  currencyCode: string;
  payFrequency: string;
  effectiveFrom: string;
  reason: string;
}

export interface EmployeeOnboardingRequest {
  employee: EmployeeCreateRequest;
  initialSalary: InitialSalaryRequest;
}

export interface EmployeeOnboardingResponse {
  employee: EmployeeResponse;
  initialSalary: SalaryHistoryItem;
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
