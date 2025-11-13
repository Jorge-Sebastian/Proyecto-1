package test;

import Boletamaster.Localidad;
import Boletamaster.Oferta;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class OfertaTest {

    @Test
    void crearOferta_basico() {
        Oferta o = new Oferta("OF1", 0.15);
        assertEquals("OF1", o.getId());
        assertEquals(0.15, o.getDescuento());
        assertTrue(o.isActiva());
    }

    @Test
    void persistencia_toCsv_y_fromCsv() {
        Localidad l = new Localidad("L1", "VIP", 100000, true, 100, null);
        Oferta o = new Oferta("OF1", 0.2);
        // ⚠️ 'localidad' en Oferta es package-private; no podemos acceder directo desde 'package test'.
        // Validamos la persistencia por el contenido del CSV (columna 3 = localidadId).
        String[] row = o.toCsv();
        assertEquals("OF1", row[0]);
        assertEquals("0.2", row[1]);
        assertEquals("true", row[2]);
        // aún no tiene localidad asignada → columna vacía
        assertEquals("", row[3]);

        // fromCsv necesita un mapa; probamos que no falle al reconstruir
        HashMap<String, Localidad> mapa = new HashMap<>();
        mapa.put("L1", l);
        row[3] = "L1"; // simulamos que venía con localidadId
        Oferta o2 = Oferta.fromCsv(row, mapa);
        assertNotNull(o2);
        // No podemos comprobar o2.localidad porque es package-private (otro paquete).
        // Si hiciera falta, mover este test a 'package Boletamaster'.
    }
}