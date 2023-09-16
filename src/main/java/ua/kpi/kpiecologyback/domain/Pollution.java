package ua.kpi.kpiecologyback.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pollution")
public class Pollution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPollution;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne
    @JoinColumn(name = "pollutant_id", nullable = false)
    private Pollutant pollutant;

    @Column(nullable = false)
    private Double valuePollution;

    @Column(nullable = false)
    private Date date;
}
