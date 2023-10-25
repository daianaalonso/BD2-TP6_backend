package ar.unrn.tp.modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "next_number")
public class NextNumber {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private int anio;
    private int actual;

    public NextNumber(int anio, int actual) {
        this.anio = anio;
        this.actual = actual;
    }

    public int recuperarSiguiente() {
        this.actual += 1;
        return this.actual;
    }

    public String codigo() {
        return actual + "-" + anio;
    }
}
