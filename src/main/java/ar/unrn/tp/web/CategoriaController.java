package ar.unrn.tp.web;

import ar.unrn.tp.api.CategoriaService;
import ar.unrn.tp.modelo.Categoria;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categorias")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:5173/")
public class CategoriaController {
    private CategoriaService categoriaService;

    @GetMapping
    public ResponseEntity<List<Categoria>> listarCategorias() {
        return new ResponseEntity<>(categoriaService.listarCategoria(), HttpStatus.OK);
    }
}
