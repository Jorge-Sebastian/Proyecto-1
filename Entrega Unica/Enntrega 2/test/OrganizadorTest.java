package test;

import Boletamaster.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class OrganizadorTest {

    private Organizador org;
    private Venue v;
    private Evento e;

    @BeforeEach
    void setUp() {
        org = new Organizador("O1", "org", "333");
        v = new Venue("V1", "Coliseo", "Bogotá", 1000);
        e = org.crearEvento("E1", "Concierto", v,
                LocalDate.now().plusDays(5),
                LocalTime.of(18, 0), "MUSICAL");
    }

    @Test
    void crearEvento_yLocalidad() {
        assertNotNull(e);
        assertEquals(1, org.getEventos().size());
        Localidad l = org.crearLocalidad(e, "L1", "VIP", 150000, true, 100);
        assertEquals(e, l.getEvento());
    }

    @Test
    void crearOferta_asignaBackRefParaPersistencia() {
        Localidad l = new Localidad("L1", "VIP", 100000, true, 100, e);
        Oferta o = org.crearOferta(l, "OF1", 0.1);
        assertNotNull(o);
        assertEquals(0.1, o.getDescuento());
        // No podemos verificar o.localidad (package-private). Verificamos el efecto:
        String[] csv = o.toCsv();
        assertEquals("L1", csv[3], "Debe persistir localidadId en oferta");
    }

    @Test
    void verGanancias_calculaTotalesConReflexion() throws Exception {
        // Admin y tarifas
        Administrador admin = new Administrador("A1", "admin", "123");
        admin.configurarTarifas(0.10, 3000);

        // Tiquete vendido en un evento del organizador
        Localidad l = new Localidad("L1", "VIP", 100000, true, 100, e);
        Tiquete t = new TiqueteSimple("T1", 100000, l);
        t.marcarVendido(new Cliente("C9", "pablo", "111"));

        // --- REFLEXIÓN: setear Main.admin y Main.inventario aun estando en otro paquete
        Field fAdmin = Main.class.getDeclaredField("admin");
        fAdmin.setAccessible(true);
        fAdmin.set(null, admin);

        Field fInv = Main.class.getDeclaredField("inventario");
        fInv.setAccessible(true);
        ArrayList<Tiquete> inv = new ArrayList<>();
        inv.add(t);
        fInv.set(null, inv);

        String rep = org.verGanancias();
        assertNotNull(rep);
        assertTrue(rep.contains("Ganancias organizador"));
        assertTrue(rep.contains("Total transado"));
        // Debe incluir el nombre del evento
        assertTrue(rep.contains("Concierto"));
    }
}