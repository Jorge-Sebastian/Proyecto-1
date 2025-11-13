package test;

import Boletamaster.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TiqueteSimpleTest {

    @Test
    void crearTiqueteSimple_funciona() {
        Localidad loc = new Localidad("L1", "VIP", 200000, false, 100, null);
        TiqueteSimple t = new TiqueteSimple("T1", 150000, loc);

        assertEquals("T1", t.getId());
        assertEquals(150000, t.getPrecio());
        assertEquals("DISPONIBLE", t.getEstado());
        assertTrue(t.getPropietario() == null);
        assertEquals(loc, t.getLocalidad());
    }

    @Test
    void marcarVendido_cambiaEstadoYPropietario() {
        Localidad loc = new Localidad("L1", "VIP", 100000, false, 100, null);
        Cliente c = new Cliente("C1", "pablo", "123");
        TiqueteSimple t = new TiqueteSimple("T1", 90000, loc);

        t.marcarVendido(c);

        assertEquals("VENDIDO", t.getEstado());
        assertEquals(c, t.getPropietario());
        assertEquals(1, loc.getVendidos()); // aforo vendido aumenta
    }

    @Test
    void transferir_cambiaPropietarioYEstado() {
        Localidad loc = new Localidad("L1", "VIP", 100000, false, 100, null);
        Cliente c1 = new Cliente("C1", "pablo", "123");
        Cliente c2 = new Cliente("C2", "ana", "abc");
        TiqueteSimple t = new TiqueteSimple("T1", 90000, loc);
        t.marcarVendido(c1);

        boolean ok = t.transferir(c2);

        assertTrue(ok);
        assertEquals("TRANSFERIDO", t.getEstado());
        assertEquals(c2, t.getPropietario());
    }
}