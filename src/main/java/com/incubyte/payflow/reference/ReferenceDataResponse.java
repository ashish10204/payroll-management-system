package com.incubyte.payflow.reference;

import java.util.List;

public record ReferenceDataResponse(
    List<LookupOption> countries,
    List<LookupOption> departments,
    List<LookupOption> jobTitles,
    List<LookupOption> employmentStatuses
) {
}
