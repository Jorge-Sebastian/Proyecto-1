package test;

import Boletamaster.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TiqueteNumeradoTest {

    @Test
    void crearTiqueteNumerado_funciona() {
        Localidad loc = new Localidad("L1", "VIP", 200000, true, 100, null);
        TiqueteNumerado t = new TiqueteNumerado("T1", 150000, loc, "A12");

        assertEquals("T1", t.getId());
        assertEquals("A12", t.getAsiento());
        assertEquals(loc, t.getLocalidad());
        assertEquals("DISPONIBLE", t.getEstado());
    }

    @Test
    void marcarVendido_actualizaEstado() {
        Localidad loc = new Localidad("L1", "VIP", 200000, true, 100, null);
        Cliente c = new Cliente("C1", "pablo", "abc");
        TiqueteNumerado t = new TiqueteNumerado("T1", 150000, loc, "A10");

        t.marcarVendido(c);

        assertEquals("VENDIDO", t.getEstado());
        assertEquals(c, t.getPropietario());
        assertEquals(1, loc.getVendidos());
    }

    @Test
    void transferir_cambiaPropietario() {
        Localidad loc = new Localidad("L1", "VIP", 200000, true, 100, null);
        Cliente c1 = new Cliente("C1", "pablo", "123");
        Cliente c2 = new Cliente("C2", "ana", "456");
        TiqueteNumerado t = new TiqueteNumerado("T1", 150000, loc, "A1");

        t.marcarVendido(c1);

        boolean ok = t.transferir(c2);

        assertTrue(ok);
        assertEquals(c2, t.getPropietario());
        assertEquals("TRANSFERIDO", t.getEstado());
    }
}