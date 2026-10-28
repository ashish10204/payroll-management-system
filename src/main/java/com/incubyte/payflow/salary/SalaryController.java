package com.incubyte.payflow.salary;

import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class SalaryController {

    private final SalaryService salaryService;

    public SalaryController(SalaryService salaryService) {
        this.salaryService = salaryService;
    }

    @GetMapping("/employees/{employeeId}/salary/current")
    public ResponseEntity<CurrentSalaryResponse> getCurrentSalary(
        @PathVariable Long employeeId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate) {
        return ResponseEntity.ok(salaryService.getCurrentSalary(employeeId, asOfDate));
    }

    @GetMapping("/employees/{employeeId}/salary/history")
    public ResponseEntity<List<SalaryHistoryItem>> getSalaryHistory(@PathVariable Long employeeId) {
        return ResponseEntity.ok(salaryService.getSalaryHistory(employeeId));
    }

    @PostMapping("/employees/{employeeId}/salary")
    public ResponseEntity<SalaryHistoryItem> createSalaryChange(
        @PathVariable Long employeeId,
        @RequestBody SalaryChangeRequest request) {
        SalaryChangeRequest normalized = new SalaryChangeRequest(
            employeeId,
            request.amount(),
            request.currencyCode(),
            request.payFrequency(),
            request.effectiveFrom(),
            request.effectiveTo(),
            request.reason()
        );
        return ResponseEntity.ok(salaryService.createSalaryChange(normalized));
    }
}
