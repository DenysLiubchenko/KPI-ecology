package ua.kpi.kpiecologyback.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pollutant")
public class Pollutant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Value must not be blank")
    @Column(nullable = false)
    private String pollutantName;

    @NotNull(message = "Value must not be null")
    @Min(value = 0, message = "Value must be positive")
    @Column(nullable = false)
    private Integer mfr;

    @NotNull(message = "Value must not be null")
    @Min(value = 0, message = "Value must be positive")
    @Column(nullable = false)
    private Integer tlv;

    @OneToMany(mappedBy = "pollutant", cascade = CascadeType.ALL)
    private List<Pollution> pollutions;
}
