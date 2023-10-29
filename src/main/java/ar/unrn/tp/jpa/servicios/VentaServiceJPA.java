package ar.unrn.tp.jpa.servicios;

import ar.unrn.tp.api.CacheVentaService;
import ar.unrn.tp.api.VentaService;
import ar.unrn.tp.modelo.*;
import jakarta.persistence.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class VentaServiceJPA implements VentaService {

    private final EntityManagerFactory emf;
    private final CacheVentaService cacheVentaService;

    public VentaServiceJPA(EntityManagerFactory emf, CacheVentaService cacheVentaService) {
        this.emf = emf;
        this.cacheVentaService = cacheVentaService;
    }

    @Override
    public void realizarVenta(Long idCliente, List<Long> productos, Long idTarjeta) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        Carrito carrito = new Carrito();
        LocalDateTime fechaActual = LocalDateTime.now();
        NextNumber nextNumber;
        try {
            tx.begin();

            Cliente cliente = em.find(Cliente.class, idCliente);
            if (cliente == null)
                throw new RuntimeException("Error. El cliente no existe.");

            Tarjeta tarjeta = em.find(Tarjeta.class, idTarjeta);
            if (tarjeta == null)
                throw new RuntimeException("Error. La tarjeta no existe.");
            if (!cliente.miTarjeta(tarjeta))
                throw new RuntimeException("Error. La tarjeta no es del cliente.");

            TypedQuery<Producto> q = em.createQuery("SELECT p FROM Producto p WHERE p.id IN :productos", Producto.class);
            q.setParameter("productos", productos);
            List<Producto> productosVendidos = q.getResultList();
            if (productosVendidos.isEmpty())
                throw new RuntimeException("Error. La lista de productos esta vacia.");
            carrito.agregarProductosAlCarrito(productosVendidos);

            TypedQuery<PagoPromocion> qp = em.createQuery("SELECT p FROM PagoPromocion p WHERE p.fechaInicio < :fecha and p.fechaFin > :fecha", PagoPromocion.class);
            qp.setParameter("fecha", LocalDate.now());
            List<PagoPromocion> pagos = qp.getResultList();
            PagoPromocion pagoPromocion = pagos.stream().findFirst().orElse(null);

            TypedQuery<MarcaPromocion> qm = em.createQuery("SELECT m FROM MarcaPromocion m", MarcaPromocion.class);
            List<MarcaPromocion> marcaPromociones = qm.getResultList();

            TypedQuery<NextNumber> query = em.createQuery(
                    "SELECT n FROM NextNumber n WHERE n.anio = :anioActual",
                    NextNumber.class);
            query.setParameter("anioActual", fechaActual.getYear());
            query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
            try {
                nextNumber = query.getSingleResult();
                nextNumber.setActual(nextNumber.recuperarSiguiente());
            } catch (Exception e) {
                nextNumber = new NextNumber(fechaActual.getYear(), 1);
            }

            Venta v = carrito.pagar(marcaPromociones, pagoPromocion, cliente, tarjeta, fechaActual, nextNumber.codigo());
            em.persist(nextNumber);
            em.persist(v);

            cacheVentaService.limpiarCache(idCliente);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            if (em != null && em.isOpen())
                em.close();
        }
    }

    @Override
    public Double calcularMonto(List<Long> productos, Long idTarjeta) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        Carrito carrito = new Carrito();
        try {
            tx.begin();
            Tarjeta tarjeta = em.find(Tarjeta.class, idTarjeta);
            if (tarjeta == null)
                throw new RuntimeException("Error. La tarjeta no existe.");

            TypedQuery<Producto> q = em.createQuery("SELECT p FROM Producto p WHERE p.id IN :productos", Producto.class);
            q.setParameter("productos", productos);
            List<Producto> productosSeleccionados = q.getResultList();
            if (productosSeleccionados.isEmpty())
                throw new RuntimeException("Error. La lista de productos esta vacia.");
            carrito.agregarProductosAlCarrito(productosSeleccionados);

            TypedQuery<PagoPromocion> qp = em.createQuery("SELECT p FROM PagoPromocion p WHERE :fecha BETWEEN p.fechaInicio AND p.fechaFin", PagoPromocion.class);
            qp.setParameter("fecha", LocalDate.now());
            List<PagoPromocion> pagos = qp.getResultList();
            PagoPromocion pagoPromocion = pagos.stream().findFirst().orElse(null);

            TypedQuery<MarcaPromocion> qm = em.createQuery("SELECT m FROM MarcaPromocion m WHERE :fecha BETWEEN m.fechaInicio AND m.fechaFin", MarcaPromocion.class);
            qm.setParameter("fecha", LocalDate.now());
            List<MarcaPromocion> marcaPromociones = qm.getResultList();

            tx.commit();
            return carrito.calcularMontoCarrito(marcaPromociones, pagoPromocion, tarjeta);
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            if (em != null && em.isOpen())
                em.close();
        }
    }

    @Override
    public List<Venta> ventas() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            TypedQuery<Venta> q = em.createQuery("SELECT v FROM Venta v", Venta.class);
            List<Venta> ventas = q.getResultList();
            tx.commit();
            return ventas;
        } catch (Exception e) {
            tx.rollback();
            throw new RuntimeException(e);
        } finally {
            if (em != null && em.isOpen())
                em.close();
        }
    }

    @Override
    public List<Venta> ultimasVentas(Long idCliente) {
        return cacheVentaService.listarVentasDeCliente(idCliente)
                .orElseGet(() -> obtenerVentasDeBD(idCliente));
    }

    private List<Venta> obtenerVentasDeBD(Long idCliente) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            TypedQuery<Venta> q = em.createQuery("SELECT v FROM Venta v WHERE v.cliente.id = :idCliente ORDER BY v.fecha DESC", Venta.class);
            q.setParameter("idCliente", idCliente);
            q.setMaxResults(3);
            List<Venta> ventas = q.getResultList();
            cacheVentaService.guardarVentas(idCliente, ventas);
            return ventas;
        } catch (Exception e) {
            tx.rollback();
            throw new RuntimeException(e);
        } finally {
            if (em != null && em.isOpen())
                em.close();
        }
    }

}