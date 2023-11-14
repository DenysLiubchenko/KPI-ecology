package ua.kpi.kpiecologyback.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import ua.kpi.kpiecologyback.domain.Company;
import ua.kpi.kpiecologyback.domain.Pollutant;

public interface PollutantRepository extends JpaRepository<Pollutant, Long> {
}
