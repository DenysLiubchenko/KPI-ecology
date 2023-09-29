package ua.kpi.kpiecologyback.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @Column(nullable = false, unique = true)
    private String pollutantName;

    @NotNull(message = "Value must not be null")
    @Min(value = 0, message = "Value must be positive")
    @Column(nullable = false)
    private Integer mfr;

    @NotNull(message = "Value must not be null")
    @Min(value = 0, message = "Value must be positive")
    @Column(nullable = false)
    private Integer tlv;

    @JsonIgnore
    @OneToMany(mappedBy = "pollutant", cascade = CascadeType.REMOVE)
    private List<Pollution> pollutions;
}
