package ar.unrn.tp.dto;

public record ProductoRecord(
        Long id,
        String descripcion,
        String codigo,
        Double precio,
        String marca,
        Long idCategoria,
        Long version
) {}
