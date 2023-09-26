package ua.kpi.kpiecologyback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ua.kpi.kpiecologyback.domain.Pollution;

import java.util.List;

public interface PollutionRepository extends JpaRepository<Pollution, Long> {
    @Query("select distinct p from Pollution p left join fetch p.company left join fetch p.pollutant ")
    public List<Pollution> getAllBy();
}
