package ua.kpi.kpiecologyback.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.kpi.kpiecologyback.domain.Company;
import ua.kpi.kpiecologyback.domain.Pollutant;
import ua.kpi.kpiecologyback.domain.Pollution;
import ua.kpi.kpiecologyback.repository.CompanyRepository;
import ua.kpi.kpiecologyback.repository.PollutantRepository;
import ua.kpi.kpiecologyback.repository.PollutionRepository;

import java.util.List;

@Service
public class DataService {
    private final CompanyRepository companyRepository;
    private final PollutantRepository pollutantRepository;
    private final PollutionRepository pollutionRepository;
    @Autowired
    public DataService(CompanyRepository companyRepository, PollutantRepository pollutantRepository, PollutionRepository pollutionRepository) {
        this.companyRepository = companyRepository;
        this.pollutantRepository = pollutantRepository;
        this.pollutionRepository = pollutionRepository;
    }

    public List<Pollution> getAllPollution() {
        return pollutionRepository.findAll();
    }
}
