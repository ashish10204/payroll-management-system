package com.incubyte.payflow.salary;

import com.incubyte.payflow.domain.Employee;
import com.incubyte.payflow.domain.PayFrequency;
import com.incubyte.payflow.domain.SalaryActionType;
import com.incubyte.payflow.domain.SalaryAudit;
import com.incubyte.payflow.domain.SalaryRecord;
import com.incubyte.payflow.repository.EmployeeRepository;
import com.incubyte.payflow.repository.SalaryAuditRepository;
import com.incubyte.payflow.repository.SalaryRecordRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SalaryService {

    private final EmployeeRepository employeeRepository;
    private final SalaryRecordRepository salaryRecordRepository;
    private final SalaryAuditRepository salaryAuditRepository;
    private final Clock clock;

    public SalaryService(EmployeeRepository employeeRepository,
                         SalaryRecordRepository salaryRecordRepository,
                         SalaryAuditRepository salaryAuditRepository,
                         Clock clock) {
        this.employeeRepository = employeeRepository;
        this.salaryRecordRepository = salaryRecordRepository;
        this.salaryAuditRepository = salaryAuditRepository;
        this.clock = clock;
    }

    @Transactional
    public SalaryHistoryItem createSalaryChange(SalaryChangeRequest request) {
        return saveSalaryRecord(request, false, null);
    }

    @Transactional
    public SalaryHistoryItem createInitialSalary(SalaryChangeRequest request, LocalDate hireDate) {
        validateRequest(request);
        if (hireDate == null) {
            throw new IllegalArgumentException("Employee hire date is required");
        }
        if (request.effectiveFrom().isBefore(hireDate)) {
            throw new IllegalArgumentException("Initial salary cannot be effective before the employee hire date");
        }
        if (salaryRecordRepository.existsByEmployee_EmployeeId(request.employeeId())) {
            throw new IllegalArgumentException("An initial salary has already been recorded for this employee");
        }
        return saveSalaryRecord(request, true, hireDate);
    }

    private SalaryHistoryItem saveSalaryRecord(SalaryChangeRequest request, boolean initialSalary, LocalDate hireDate) {
        validateRequest(request);

        Employee employee = employeeRepository.findById(request.employeeId())
            .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        LocalDate businessDate = LocalDate.now(clock);
        if (!initialSalary && request.effectiveFrom().isBefore(businessDate)) {
            throw new IllegalArgumentException("Retroactive salary change is not allowed");
        }
        if (initialSalary && request.effectiveFrom().isBefore(hireDate)) {
            throw new IllegalArgumentException("Initial salary cannot be effective before the employee hire date");
        }

        List<SalaryRecord> existingRecords = salaryRecordRepository.findByEmployeeEmployeeIdOrderByEffectiveFromAsc(request.employeeId());
        SalaryRecord oldRecord = existingRecords.stream()
            .filter(record -> !record.getEffectiveFrom().isAfter(request.effectiveFrom()))
            .filter(record -> record.getEffectiveTo() == null || !record.getEffectiveTo().isBefore(request.effectiveFrom()))
            .findFirst()
            .orElse(null);
        String actor = currentActor();
        SalaryRecord newRecord = new SalaryRecord(
            employee,
            request.amount(),
            request.currencyCode().trim().toUpperCase(Locale.ROOT),
            PayFrequency.valueOf(request.payFrequency().trim().toUpperCase(Locale.ROOT)),
            request.effectiveFrom(),
            null,
            request.reason().trim(),
            actor
        );

        List<SalaryRecord> recordsToPersist = new ArrayList<>();
        for (SalaryRecord record : existingRecords) {
            if (record.getEffectiveFrom().isBefore(request.effectiveFrom())
                && (record.getEffectiveTo() == null || !record.getEffectiveTo().isBefore(request.effectiveFrom()))) {
                record.setEffectiveTo(request.effectiveFrom().minusDays(1));
                recordsToPersist.add(record);
            }
        }

        Optional<SalaryRecord> nextRecord = existingRecords.stream()
            .filter(r -> !r.getEffectiveFrom().isBefore(request.effectiveFrom()))
            .findFirst();

        if (nextRecord.isPresent() && nextRecord.get().getEffectiveFrom().isAfter(request.effectiveFrom())) {
            newRecord.setEffectiveTo(nextRecord.get().getEffectiveFrom().minusDays(1));
        }

        SalaryRecord saved = salaryRecordRepository.save(newRecord);
        for (SalaryRecord record : recordsToPersist) {
            salaryRecordRepository.save(record);
        }

        SalaryActionType actionType = request.effectiveFrom().isEqual(businessDate)
            ? oldRecord == null ? SalaryActionType.SALARY_CREATED : SalaryActionType.SALARY_CHANGED
            : initialSalary ? SalaryActionType.SALARY_CREATED : SalaryActionType.FUTURE_CHANGE_SCHEDULED;

        SalaryAudit audit = new SalaryAudit(
            employee,
            oldRecord,
            saved,
            actionType,
            actor,
            LocalDateTime.now(clock),
            request.reason().trim()
        );
        salaryAuditRepository.save(audit);

        return toHistoryItem(saved);
    }

    @Transactional
    public CurrentSalaryResponse getCurrentSalary(Long employeeId, LocalDate asOfDate) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        LocalDate targetDate = asOfDate == null ? LocalDate.now(clock) : asOfDate;

        SalaryRecord record = salaryRecordRepository.findByEmployeeEmployeeIdOrderByEffectiveFromAsc(employeeId).stream()
            .filter(r -> !r.getEffectiveFrom().isAfter(targetDate))
            .filter(r -> r.getEffectiveTo() == null || !r.getEffectiveTo().isBefore(targetDate))
            .sorted(Comparator.comparing(SalaryRecord::getEffectiveFrom).reversed())
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No salary record found for the supplied date"));

        return new CurrentSalaryResponse(
            employee.getEmployeeId(),
            record.getSalaryAmount(),
            record.getCurrencyCode(),
            record.getPayFrequency().name(),
            record.getEffectiveFrom(),
            record.getEffectiveTo(),
            record.getChangeReason()
        );
    }

    @Transactional
    public List<SalaryHistoryItem> getSalaryHistory(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new IllegalArgumentException("Employee not found");
        }

        return salaryRecordRepository.findByEmployeeEmployeeIdOrderByEffectiveFromDesc(employeeId).stream()
            .map(this::toHistoryItem)
            .toList();
    }

    public List<SalaryHistoryItem> getAuditTrail(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new IllegalArgumentException("Employee not found");
        }

        return salaryAuditRepository.findAll().stream()
            .filter(audit -> audit.getEmployee().getEmployeeId().equals(employeeId))
            .map(audit -> new SalaryHistoryItem(
                audit.getNewSalaryRecord() == null ? null : audit.getNewSalaryRecord().getSalaryRecordId(),
                audit.getEmployee().getEmployeeId(),
                audit.getNewSalaryRecord() == null ? null : audit.getNewSalaryRecord().getSalaryAmount(),
                audit.getNewSalaryRecord() == null ? null : audit.getNewSalaryRecord().getCurrencyCode(),
                audit.getNewSalaryRecord() == null ? null : audit.getNewSalaryRecord().getPayFrequency().name(),
                audit.getNewSalaryRecord() == null ? null : audit.getNewSalaryRecord().getEffectiveFrom(),
                audit.getNewSalaryRecord() == null ? null : audit.getNewSalaryRecord().getEffectiveTo(),
                audit.getReason(),
                audit.getActorId()
            ))
            .toList();
    }

    @Transactional
    public void clearTestData() {
        salaryAuditRepository.deleteAll();
        salaryRecordRepository.deleteAll();
    }

    private void validateRequest(SalaryChangeRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Salary update request is required");
        }
        if (request.employeeId() == null) {
            throw new IllegalArgumentException("Employee id is required");
        }
        if (request.amount() == null || request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Salary amount must be positive");
        }
        if (request.amount().scale() > 2 || request.amount().precision() - request.amount().scale() > 16) {
            throw new IllegalArgumentException("Salary amount must fit a maximum of 16 integer and 2 decimal digits");
        }
        if (request.currencyCode() == null || request.currencyCode().isBlank()) {
            throw new IllegalArgumentException("Currency code is required");
        }
        if (!request.currencyCode().trim().matches("[A-Za-z]{3}")) {
            throw new IllegalArgumentException("Currency code must be a three-letter ISO 4217 code");
        }
        if (request.payFrequency() == null || request.payFrequency().isBlank()) {
            throw new IllegalArgumentException("Pay frequency is required");
        }
        if (request.effectiveFrom() == null) {
            throw new IllegalArgumentException("Effective from date is required");
        }
        if (request.reason() == null || request.reason().isBlank()) {
            throw new IllegalArgumentException("Reason is required");
        }
        try {
            PayFrequency.valueOf(request.payFrequency().trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unsupported pay frequency");
        }
    }

    private SalaryHistoryItem toHistoryItem(SalaryRecord record) {
        return new SalaryHistoryItem(
            record.getSalaryRecordId(),
            record.getEmployee().getEmployeeId(),
            record.getSalaryAmount(),
            record.getCurrencyCode(),
            record.getPayFrequency().name(),
            record.getEffectiveFrom(),
            record.getEffectiveTo(),
            record.getChangeReason(),
            record.getCreatedBy()
        );
    }

    private String currentActor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null || !authentication.isAuthenticated()
            ? "system"
            : authentication.getName();
    }
}
