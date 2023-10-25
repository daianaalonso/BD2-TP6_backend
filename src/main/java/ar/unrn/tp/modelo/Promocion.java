package ar.unrn.tp.modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Inheritance
@DiscriminatorColumn(name = "tipo_promocion")
public abstract class Promocion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;
    @Column(name = "fecha_fin")
    private LocalDate fechaFin;
    private Double porcentaje;

    public Promocion(LocalDate fechaInicio, LocalDate fechaFin, Double porcentaje) {
        if (!validarFecha(fechaInicio, fechaFin))
            throw new RuntimeException("Las fechas de la promoción no son validas.");
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.porcentaje = porcentaje;
    }

    private boolean validarFecha(LocalDate fechaInicio, LocalDate fechaFin) {
        return fechaInicio.isBefore(fechaFin);
    }

    public boolean estaEnCurso() {
        LocalDate hoy = LocalDate.now();
        return hoy.isAfter(fechaInicio) && hoy.isBefore(fechaFin);
    }
}