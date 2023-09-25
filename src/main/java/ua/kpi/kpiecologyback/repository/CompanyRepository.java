package ua.kpi.kpiecologyback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.kpi.kpiecologyback.domain.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    public Company findByCompanyName (String companyName);
}
