package ua.kpi.kpiecologyback.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "emergency")
public class Emergency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Value must not be null")
    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @NotNull(message = "Value must not be null")
    @ManyToOne
    @JoinColumn(name = "pollutant_id", nullable = false)
    private Pollutant pollutant;

    @Min(value = 0, message = "Year must be positive")
    @Column(nullable = false)
    private Double pollutionValue;

    @NotNull(message = "Value must not be null")
    @Min(value = 0, message = "Year must be positive")
    @Column(nullable = false)
    private Double pollutionConcentration;

    @Min(value = 0, message = "Year must be positive")
    @Column(nullable = false)
    private Integer peopleMinorInjury;

    @Min(value = 0, message = "Year must be positive")
    @Column(nullable = false)
    private Integer peopleSeriousInjury;

    @Min(value = 0, message = "Year must be positive")
    @Column(nullable = false)
    private Integer peopleDisability;

    @Min(value = 0, message = "Year must be positive")
    @Column(nullable = false)
    private Integer peopleDead;

    @NotNull(message = "Value must not be null")
    @Min(value = 0, message = "Year must be positive")
    @Column(nullable = false)
    private Double PollutionLoss;

    @NotNull(message = "Value must not be null")
    @Min(value = 0, message = "Year must be positive")
    @Column(nullable = false)
    private Double PeopleLoss;
}
