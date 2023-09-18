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
    private Long id;

    @Column(nullable = false)
    private String namePollutant;

    @Column(nullable = false)
    private Integer massFlowRate;

    @Column(nullable = false)
    private Integer tlv;
}
