package test;

import Boletamaster.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PaqueteDeluxeTest {

    @Test
    void agregarTiquetes_funciona() {
        PaqueteDeluxe p = new PaqueteDeluxe("P1", 400000, "Meet & Greet");
        Localidad loc = new Localidad("L1", "VIP", 200000, false, 100, null);

        TiqueteSimple t1 = new TiqueteSimple("T1", 100000, loc);
        TiqueteSimple t2 = new TiqueteSimple("T2", 100000, loc);

        p.agregar(t1);
        p.agregar(t2);

        var field = PaqueteTiquetes.class.getDeclaredFields()[2]; 
        field.setAccessible(true);

        try {
            var lista = (java.util.List<Tiquete>) field.get(p);
            assertEquals(2, lista.size());
        } catch (Exception e) {
            fail("Error leyendo la lista interna de tiquetes del paquete: " + e.getMessage());
        }
    }

    @Test
    void esTemporada_funciona() {
        PaqueteDeluxe p = new PaqueteDeluxe("P1", 450000, "Backstage");

        Venue v = new Venue("V1", "Coliseo", "Bogotá", 5000);
        Evento e1 = new Evento("E1", "Concierto 1", v, java.time.LocalDate.now(), java.time.LocalTime.of(18, 0), "MUSICAL");
        Evento e2 = new Evento("E2", "Concierto 2", v, java.time.LocalDate.now().plusDays(1), java.time.LocalTime.of(18, 0), "MUSICAL");

        Localidad l1 = new Localidad("L1", "VIP", 200000, true, 100, e1);
        Localidad l2 = new Localidad("L2", "VIP", 200000, true, 100, e2);

        TiqueteSimple t1 = new TiqueteSimple("T1", 100000, l1);
        TiqueteSimple t2 = new TiqueteSimple("T2", 100000, l2);

        p.agregar(t1);
        p.agregar(t2);

        assertTrue(p.esTemporada());
    }
}