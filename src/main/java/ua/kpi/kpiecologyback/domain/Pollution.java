package ua.kpi.kpiecologyback.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;

import java.time.*;

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
    @Min(value = 0, message = "Year must be positive")
    @Column(nullable = false)
    private Double pollutionValue;

    @NotNull(message = "Value must not be null")
    @Min(value = 0, message = "Year must be positive")
    @Column(nullable = false)
    private Double pollutionConcentration;

    @NotNull(message = "Value must not be null")
    @Min(value = 0, message = "Year must be positive")
    @Column(nullable = false)
    private Double hq;

    @NotNull(message = "Value must not be null")
    @Min(value = 0, message = "Year must be positive")
    @Column(nullable = false)
    private Double cr;

    @NotNull(message = "Value must not be null")
    @Min(value = 0, message = "Year must be positive")
    @Column(nullable = false)
    private Double penalty;

    @NotNull(message = "Value must not be null")
    @Min(value = 0, message = "Year must be positive")
    @Column(nullable = false)
    private Integer year;

    public void setYear(Integer year) {
        if (Year.of(year).isAfter(Year.now())) throw new HttpClientErrorException(HttpStatusCode.valueOf(400));
        this.year = year;
    }
}
