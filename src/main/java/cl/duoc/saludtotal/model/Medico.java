package cl.duoc.saludtotal.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "medico")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    private Integer valorConsulta;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "especialidad_id")
    private Especialidad especialidad;

    @Column(length = 5)
    private String horaInicio;

    @Column(length = 5)
    private String horaFin;

    private Integer duracionMinutos;

    @Column(nullable = false)
    private boolean activo = true;
}
