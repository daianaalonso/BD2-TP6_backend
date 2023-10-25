package ar.unrn.tp.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class Marca {

    @Column(name = "marca")
    private String nombre;

    public Marca(String nombre) {
        this.nombre = nombre;
    }

    public boolean esMarca(String marca) {
        return this.nombre.equals(marca);
    }
}
