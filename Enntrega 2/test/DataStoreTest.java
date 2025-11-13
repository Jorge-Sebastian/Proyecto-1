package test;

import Boletamaster.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class DataStoreTest {

    private DataStore ds;

    @BeforeEach
    void limpiarAntesDeCadaTest() throws Exception {
        ds = new DataStore();

        String dir = "data/";
        String[] files = {
                "administradores.csv", "clientes.csv", "organizadores.csv",
                "venues.csv", "venue_fechas.csv", "eventos.csv",
                "localidades.csv", "ofertas.csv", "tiquetes.csv",
                "pagos.csv", "pago_items.csv", "paquetes.csv", "paquete_items.csv"
        };
        for (String f : files) {
            File file = new File(dir + f);
            if (file.exists()) file.delete();
        }

        setStaticField(Main.class, "admin", null);
        clearStaticList(Main.class, "usuarios");
        clearStaticList(Main.class, "eventos");
        clearStaticList(Main.class, "localidades");
        clearStaticList(Main.class, "inventario");
    }

    // helpers

    private void setStaticField(Class<?> clazz, String fieldName, Object value) throws Exception {
        Field f = clazz.getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(null, value);
    }

    @SuppressWarnings("unchecked")
    private <T> ArrayList<T> getStaticList(Class<?> clazz, String fieldName) throws Exception {
        Field f = clazz.getDeclaredField(fieldName);
        f.setAccessible(true);
        return (ArrayList<T>) f.get(null);
    }

    private void clearStaticList(Class<?> clazz, String fieldName) throws Exception {
        ArrayList<?> list = getStaticList(clazz, fieldName);
        if (list != null) list.clear();
    }

    //  TEST PRINCIPAL


    @Test
    void saveAll_y_loadAll_reconstruyenModeloSeed() throws Exception {
        
        Method mSeed = Main.class.getDeclaredMethod("seed");
        mSeed.setAccessible(true);
        mSeed.invoke(null);


        ds.saveAll();

        setStaticField(Main.class, "admin", null);
        clearStaticList(Main.class, "usuarios");
        clearStaticList(Main.class, "eventos");
        clearStaticList(Main.class, "localidades");
        clearStaticList(Main.class, "inventario");


        ds.loadAll();

 
        Field fAdmin = Main.class.getDeclaredField("admin");
        fAdmin.setAccessible(true);
        Administrador admin = (Administrador) fAdmin.get(null);

        assertNotNull(admin, "El admin no se cargó");
        assertEquals("admin", admin.getLogin());
        assertEquals("123", admin.getPassword());
        assertEquals(0.10, admin.getPorcentajeServicio(), 1e-6);
        assertEquals(3000.0, admin.getCuotaEmision(), 1e-6);


        ArrayList<Usuario> usuarios = getStaticList(Main.class, "usuarios");
        assertEquals(2, usuarios.size(), "Debe haber 2 usuarios (org + cliente)");

        Organizador org = null;
        Cliente cli = null;
        for (Usuario u : usuarios) {
            if (u instanceof Organizador) org = (Organizador) u;
            if (u instanceof Cliente && !(u instanceof Organizador)) cli = (Cliente) u;
        }

        assertNotNull(org, "No se cargó el organizador");
        assertNotNull(cli, "No se cargó el cliente");
        assertEquals("org", org.getLogin());
        assertEquals("pablo", cli.getLogin());


        ArrayList<Evento> eventos = getStaticList(Main.class, "eventos");
        assertEquals(1, eventos.size(), "Debe haber 1 evento");
        Evento e = eventos.get(0);
        assertEquals("Concierto 1", e.getNombre());
        assertEquals("MUSICAL", e.getTipo());
        assertEquals("PROGRAMADO", e.getEstado());
        assertNotNull(e.getVenue());

  
        ArrayList<Localidad> localidades = getStaticList(Main.class, "localidades");
        assertEquals(1, localidades.size(), "Debe haber 1 localidad");
        Localidad loc = localidades.get(0);
        assertEquals("VIP", loc.getNombre());
        assertEquals(e, loc.getEvento(), "La localidad debe pertenecer al evento cargado");
        assertEquals(200000.0, loc.getPrecioBase(), 1e-6);
        assertTrue(loc.isNumerada());
        assertEquals(200, loc.getAforo());


        assertEquals(1, e.getLocalidades().size(), "El evento debe tener 1 localidad");
        assertEquals(loc, e.getLocalidades().get(0));


        assertEquals(1, loc.getOfertas().size(), "La localidad debe tener 1 oferta");
        Oferta of = loc.getOfertas().get(0);
        assertEquals(0.10, of.getDescuento(), 1e-6);
        assertTrue(of.isActiva());
        

        ArrayList<Tiquete> inventario = getStaticList(Main.class, "inventario");
        assertEquals(3, inventario.size(), "El seed crea 3 tiquetes");

        int numNumerados = 0;
        int numSimples = 0;
        for (Tiquete t : inventario) {
            if (t instanceof TiqueteNumerado) numNumerados++;
            else if (t instanceof TiqueteSimple) numSimples++;
            assertEquals("DISPONIBLE", t.getEstado());
            assertNull(t.getPropietario());
            assertEquals(loc, t.getLocalidad());
        }
        assertEquals(2, numNumerados);
        assertEquals(1, numSimples);
    }
}