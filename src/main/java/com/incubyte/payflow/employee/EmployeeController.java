package com.incubyte.payflow.employee;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/employees")
    public ResponseEntity<EmployeeResponse> createEmployee(@RequestBody EmployeeCreateRequest request) {
        return ResponseEntity.ok(employeeService.createEmployee(request));
    }

    @PostMapping("/employees/onboard")
    public ResponseEntity<EmployeeOnboardingResponse> onboardEmployee(@RequestBody EmployeeOnboardingRequest request) {
        return ResponseEntity.ok(employeeService.onboardEmployee(request));
    }

    @GetMapping("/employees/{id}")
    public ResponseEntity<EmployeeResponse> getEmployee(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployee(id));
    }

    @PutMapping("/employees/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(@PathVariable Long id, @RequestBody EmployeeUpdateRequest request) {
        return ResponseEntity.ok(employeeService.updateEmployee(new EmployeeUpdateRequest(
            id,
            request.firstName(),
            request.lastName(),
            request.email(),
            request.countryCode(),
            request.departmentCode(),
            request.jobTitle(),
            request.employmentStatus(),
            request.hireDate()
        )));
    }

    @DeleteMapping("/employees/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/employees")
    public ResponseEntity<EmployeePageResponse> listEmployees(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page must be non-negative and size must be between 1 and 100");
        }

        Page<EmployeeResponse> employees = employeeService.listEmployees(
            PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "employeeId"))
        );
        return ResponseEntity.ok(new EmployeePageResponse(
            employees.getContent(),
            employees.getTotalElements(),
            employees.getTotalPages(),
            employees.getNumber(),
            employees.getSize()
        ));
    }
}
