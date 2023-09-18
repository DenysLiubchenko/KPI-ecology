package ua.kpi.kpiecologyback.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.time.LocalDate;
import java.time.Year;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pollution")
public class Pollution {
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

    @NotNull(message = "Value must not be null")
    @Column(nullable = false)
    private Double pollutionValue;

    @NotNull(message = "Value must not be null")
    @Column(nullable = false)
    private Integer year;
}
