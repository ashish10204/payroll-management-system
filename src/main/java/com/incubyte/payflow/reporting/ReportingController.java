package com.incubyte.payflow.reporting;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ReportingController {

    private final ReportingService reportingService;

    public ReportingController(ReportingService reportingService) {
        this.reportingService = reportingService;
    }

    @GetMapping("/reports/headcount")
    public ResponseEntity<HeadcountReportResponse> getHeadcount(
        @RequestParam(required = false) String country,
        @RequestParam(required = false) String department,
        @RequestParam(required = false) String jobTitle,
        @RequestParam(required = false) String employmentStatus) {
        return ResponseEntity.ok(reportingService.headcount(country, department, jobTitle, employmentStatus));
    }

    @GetMapping("/reports/compensation")
    public ResponseEntity<CompensationReportResponse> getCompensation(
        @RequestParam(required = false) String country,
        @RequestParam(required = false) String department,
        @RequestParam(required = false) String jobTitle,
        @RequestParam(required = false) String employmentStatus,
        @RequestParam(required = false) String currency,
        @RequestParam(required = false) String payFrequency,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate) {
        return ResponseEntity.ok(reportingService.compensation(country, department, jobTitle, employmentStatus, currency, payFrequency, asOfDate));
    }

    @GetMapping("/reports/employees/export")
    public ResponseEntity<String> exportEmployees(
        @RequestParam(required = false) String country,
        @RequestParam(required = false) String department,
        @RequestParam(required = false) String jobTitle,
        @RequestParam(required = false) String employmentStatus) {
        String csv = reportingService.exportEmployeesCsv(country, department, jobTitle, employmentStatus);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new org.springframework.http.MediaType("text", "csv", java.nio.charset.StandardCharsets.UTF_8));
        headers.setContentDisposition(ContentDisposition.attachment()
            .filename("employee-directory.csv")
            .build());
        return ResponseEntity.ok().headers(headers).body(csv);
    }
}
