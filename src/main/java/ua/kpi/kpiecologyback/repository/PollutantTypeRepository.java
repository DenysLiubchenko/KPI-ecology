package ua.kpi.kpiecologyback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.kpi.kpiecologyback.domain.PollutantType;

public interface PollutantTypeRepository extends JpaRepository<PollutantType, Long> {
}
