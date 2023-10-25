package ar.unrn.tp.modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tarjeta")
public class Tarjeta {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nombre;
    private int numero;

    public Tarjeta(String nombre, int numero) {
        this.nombre = nombre;
        this.numero = numero;
    }

    public boolean esTarjeta(String tarjeta) {
        return this.nombre.equals(tarjeta);
    }
}