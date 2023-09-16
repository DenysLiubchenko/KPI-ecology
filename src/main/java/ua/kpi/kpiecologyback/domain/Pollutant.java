package ua.kpi.kpiecologyback.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pollutant")
public class Pollutant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPollutant;

    @Column(nullable = false)
    private String namePollutant;

    @Column(nullable = false)
    private Integer gdk;

    @Column(nullable = false)
    private Integer massConsumption;
}
