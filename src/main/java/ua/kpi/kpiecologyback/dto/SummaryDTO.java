package ua.kpi.kpiecologyback.dto;

import ua.kpi.kpiecologyback.domain.Pollution;

public record SummaryDTO(String companyName, String pollutantName, Double pollutionValue, Integer mfr, Integer tlv, Integer year) {
}
