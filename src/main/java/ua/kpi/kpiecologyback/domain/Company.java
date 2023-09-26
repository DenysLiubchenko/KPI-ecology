package ua.kpi.kpiecologyback.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "company")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Value must not be blank")
    @Column(nullable = false)
    private String companyName;

    @NotBlank(message = "Value must not be blank")
    @Column(nullable = false)
    private String activity;

    @NotBlank(message = "Value must not be blank")
    @Column(nullable = false)
    private String location;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<Pollution> pollutions;
}
