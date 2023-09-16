package ua.kpi.kpiecologyback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.kpi.kpiecologyback.domain.Pollution;

public interface PollutionRepository extends JpaRepository<Pollution, Long> {
}
