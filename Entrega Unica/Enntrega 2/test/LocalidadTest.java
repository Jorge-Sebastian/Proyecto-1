package test;

import Boletamaster.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class LocalidadTest {

    private Localidad loc;

    @BeforeEach
    void setUp() {
        Venue v = new Venue("V1", "Coliseo", "Bogotá", 5000);
        Evento e = new Evento("E1", "Show", v,
                LocalDate.now().plusDays(10), LocalTime.of(19,0), "MUSICAL");
        loc = new Localidad("L1", "VIP", 100000, true, 100, e);
    }

    @Test
    void crearLocalidad_guardarDatos() {
        assertEquals("VIP", loc.getNombre());
        assertEquals(100000, loc.getPrecioBase());
        assertEquals(100, loc.getAforo());
        assertEquals(0, loc.getVendidos());
    }

    @Test
    void ofertasAplicanDescuento() {
        // Crea oferta y agrégala (el precio vigente toma la primera activa)
        Boletamaster.Oferta o = new Boletamaster.Oferta("OF1", 0.10);
        loc.agregarOferta(o);
        double esperado = 100000 * 0.9;
        assertEquals(esperado, loc.getPrecioVigente(), 1e-6);
    }

    @Test
    void marcarVendido_incrementaContador() {
        loc.marcarVendido();
        assertEquals(1, loc.getVendidos());
    }

    @Test
    void crearTiqueteSimple_yNumerado() {
        Tiquete t1 = loc.crearTiqueteSimple("T1");
        assertNotNull(t1);
        Tiquete t2 = loc.crearTiqueteNumerado("T2", "A1");
        assertNotNull(t2);
    }
}