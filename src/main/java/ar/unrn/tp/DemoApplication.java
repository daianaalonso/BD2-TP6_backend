package ar.unrn.tp;

import ar.unrn.tp.api.CacheVentaService;
import ar.unrn.tp.jpa.servicios.*;
import ar.unrn.tp.modelo.Categoria;
import ar.unrn.tp.modelo.Marca;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Autowired
    ClienteServiceJPA clienteServiceJPA;
    @Autowired
    ProductoServiceJPA productoServiceJPA;
    @Autowired
    PromocionServiceJPA promocionServiceJPA;
    @Autowired
    VentaServiceJPA ventaServiceJPA;
    @Autowired
    EntityManagerFactory emf;

    private void inTransactionExecute() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Categoria indumentaria = new Categoria("Indumentaria");
            em.persist(indumentaria);
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            if (em != null && em.isOpen())
                em.close();
        }
    }

    private void agregarCliente() {
        clienteServiceJPA.crearCliente("Daiana", "Alonso", "42448077", "alonso@gmail.com");
        clienteServiceJPA.agregarTarjeta(1L, "123", "VISA");
        clienteServiceJPA.agregarTarjeta(1L, "123456", "Naranja");
        clienteServiceJPA.agregarTarjeta(1L, "5646", "Mastercard");
    }

    private void agregarProductos() {
        productoServiceJPA.crearProducto("1", "Remera", 3000.0, 1L, "Nike");
        productoServiceJPA.crearProducto("2", "Pantalon", 6000.0, 1L, "Nike");
        productoServiceJPA.crearProducto("3", "Campera", 10000.0, 1L, "Adidas");
        productoServiceJPA.crearProducto("4", "Jean", 8000.0, 1L, "Adidas");
    }

    private void agregarPromociones() {
        promocionServiceJPA.crearDescuento("Nike", LocalDate.now().minusDays(3), LocalDate.now().plusDays(2), 0.05);
        promocionServiceJPA.crearDescuento("Adidas", LocalDate.now().minusDays(6), LocalDate.now().minusDays(1), 0.05);
        promocionServiceJPA.crearDescuentoSobreTotal("VISA", LocalDate.now().minusDays(2), LocalDate.now().plusDays(4), 0.08);
        promocionServiceJPA.crearDescuentoSobreTotal("Naranja", LocalDate.now().minusDays(10), LocalDate.now().minusDays(4), 0.08);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            inTransactionExecute();
            agregarPromociones();
            agregarCliente();
            agregarProductos();
            ventaServiceJPA.realizarVenta(1L, new ArrayList<>(Arrays.asList(1L, 2L)), 1L);
            ventaServiceJPA.realizarVenta(1L, new ArrayList<>(Arrays.asList(1L)), 1L);
        };
    }
}