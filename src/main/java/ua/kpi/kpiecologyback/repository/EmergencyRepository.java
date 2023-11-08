package ua.kpi.kpiecologyback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.kpi.kpiecologyback.domain.Emergency;

public interface EmergencyRepository extends JpaRepository<Emergency, Long> {
}
