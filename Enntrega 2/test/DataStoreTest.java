/*
package test;

import Boletamaster.*;
import Boletamaster.Main;
import Boletamaster.DataStore;
import Boletamaster.Cliente;
import Boletamaster.Usuario;
import Boletamaster.Tiquete;
import Boletamaster.TiqueteSimple;
import Boletamaster.csv.Csv;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class DataStoreTest {

    @TempDir
    Path tempDir;

    private Path dataDir;
    private DataStore store;

    @BeforeEach
    void setUp() throws Exception {
        System.setProperty("user.dir", tempDir.toString());
        dataDir = tempDir.resolve("data");
        Files.createDirectories(dataDir);

        Main.admin = null;
        if (Main.usuarios != null) Main.usuarios.clear();
        if (Main.eventos != null) Main.eventos.clear();
        if (Main.localidades != null) Main.localidades.clear();
        if (Main.inventario != null) Main.inventario.clear();

        store = new DataStore();
    }

    @AfterEach
    void tearDown() {
        if (Main.usuarios != null) Main.usuarios.clear();
        if (Main.eventos != null) Main.eventos.clear();
        if (Main.localidades != null) Main.localidades.clear();
        if (Main.inventario != null) Main.inventario.clear();
        Main.admin = null;
    }

    @Test
    void loadAll_enVacio_creaAdminPorDefectoYListasVacias() {
        assertDoesNotThrow(() -> store.loadAll());
        assertNotNull(Main.admin);
        assertEquals("A1", Main.admin.getId());
        assertEquals("admin", Main.admin.getLogin());
        assertEquals(0, Main.usuarios.size());
        assertEquals(0, Main.eventos.size());
        assertEquals(0, Main.localidades.size());
        assertEquals(0, Main.inventario.size());
    }

    @Test
    void saveAll_escribeArchivosEsperados() throws Exception {
        Main.admin = new Administrador("A9","root","pw");

        Cliente c1 = new Cliente("C1","jorge","123");
        Cliente c2 = new Cliente("C2","luis","abc");
        Organizador o1 = new Organizador("O1","org","pw");
        Main.usuarios.add(c1);
        Main.usuarios.add(c2);
        Main.usuarios.add(o1);

        Venue v = new Venue("V1","Movistar Arena","Bogotá",12000);
        LocalDate extra = LocalDate.of(2027, 1, 5);
        v.reservarFecha(extra);

        Evento e = new Evento("E1","Concierto", v,
                LocalDate.of(2026,5,20), LocalTime.of(19,30), "CONCIERTO");
        Main.eventos.add(e);

        Localidad l = new Localidad("L1","Platea",150000.0,true,3000, e);
        Oferta of = new Oferta("OF1", 0.10);
        l.agregarOferta(of);
        Main.localidades.add(l);

        TiqueteSimple t = new TiqueteSimple("T1", 180000.0, l);
        t.setPropietario(c1);
        t.setTransferible(true);
        Main.inventario.add(t);

        assertDoesNotThrow(() -> store.saveAll());

        assertTrue(Files.exists(dataDir.resolve("administradores.csv")));
        assertTrue(Files.exists(dataDir.resolve("clientes.csv")));
        assertTrue(Files.exists(dataDir.resolve("organizadores.csv")));
        assertTrue(Files.exists(dataDir.resolve("venues.csv")));
        assertTrue(Files.exists(dataDir.resolve("venue_fechas.csv")));
        assertTrue(Files.exists(dataDir.resolve("eventos.csv")));
        assertTrue(Files.exists(dataDir.resolve("localidades.csv")));
        assertTrue(Files.exists(dataDir.resolve("ofertas.csv")));
        assertTrue(Files.exists(dataDir.resolve("tiquetes.csv")));
        assertTrue(Files.exists(dataDir.resolve("pagos.csv")));
        assertTrue(Files.exists(dataDir.resolve("pago_items.csv")));
        assertTrue(Files.exists(dataDir.resolve("paquetes.csv")));
        assertTrue(Files.exists(dataDir.resolve("paquete_items.csv")));

        long lenAdmin = Files.size(dataDir.resolve("administradores.csv"));
        long lenClientes = Files.size(dataDir.resolve("clientes.csv"));
        long lenOrg = Files.size(dataDir.resolve("organizadores.csv"));
        long lenVenues = Files.size(dataDir.resolve("venues.csv"));
        long lenFechas = Files.size(dataDir.resolve("venue_fechas.csv"));
        long lenEventos = Files.size(dataDir.resolve("eventos.csv"));
        long lenLocalidades = Files.size(dataDir.resolve("localidades.csv"));
        long lenOfertas = Files.size(dataDir.resolve("ofertas.csv"));
        long lenTiq = Files.size(dataDir.resolve("tiquetes.csv"));

        assertTrue(lenAdmin > 10);
        assertTrue(lenClientes > 10);
        assertTrue(lenOrg > 10);
        assertTrue(lenVenues > 10);
        assertTrue(lenFechas > 5);
        assertTrue(lenEventos > 10);
        assertTrue(lenLocalidades > 10);
        assertTrue(lenOfertas > 10);
        assertTrue(lenTiq > 10);
    }

    @Test
    void roundTrip_reconstruyeEntidadesYRelaciones() {
        Main.admin = new Administrador("A9","root","pw");

        Cliente c1 = new Cliente("C1","jorge","123");
        Organizador o1 = new Organizador("O1","org","pw");
        Main.usuarios.add(c1);
        Main.usuarios.add(o1);

        Venue v = new Venue("V1","Movistar Arena","Bogotá",12000);
        LocalDate fechaEvento = LocalDate.of(2026,5,20);
        v.reservarFecha(fechaEvento);

        Evento e = new Evento("E1","Concierto", v, fechaEvento, LocalTime.of(19,30), "CONCIERTO");
        Main.eventos.add(e);

        Localidad l = new Localidad("L1","Platea",150000.0,true,3000, e);
        Oferta of = new Oferta("OF1", 0.15);
        l.agregarOferta(of);
        Main.localidades.add(l);

        TiqueteSimple t = new TiqueteSimple("T1", 180000.0, l);
        t.setPropietario(c1);
        t.setTransferible(true);
        Main.inventario.add(t);

        store.saveAll();

        Main.admin = null;
        Main.usuarios.clear();
        Main.eventos.clear();
        Main.localidades.clear();
        Main.inventario.clear();

        store.loadAll();

        assertNotNull(Main.admin);
        assertEquals("A9", Main.admin.getId());

        assertEquals(2, Main.usuarios.size());
        Usuario u1 = Main.usuarios.stream().filter(u -> "C1".equals(u.getId())).findFirst().orElse(null);
        assertNotNull(u1);
        assertTrue(u1 instanceof Cliente);

        assertEquals(1, Main.eventos.size());
        Evento e2 = Main.eventos.get(0);
        assertEquals("E1", e2.getId());
        assertNotNull(e2.getVenue());
        assertEquals("V1", e2.getVenue().getId());
        assertTrue(e2.getVenue().getFechasOcupadas().contains(fechaEvento));

        assertEquals(1, Main.localidades.size());
        Localidad l2 = Main.localidades.get(0);
        assertEquals("L1", l2.getId());
        assertNotNull(l2.getEvento());
        assertEquals("E1", l2.getEvento().getId());
        List<Oferta> ofertas = l2.getOfertas();
        assertNotNull(ofertas);
        assertEquals(1, ofertas.size());
        assertEquals("OF1", ofertas.get(0).getId());

        assertEquals(1, Main.inventario.size());
        Tiquete t2 = Main.inventario.get(0);
        assertEquals("T1", t2.getId());
        assertNotNull(t2.getLocalidad());
        assertEquals("L1", t2.getLocalidad().getId());
        assertNotNull(t2.getPropietario());
        assertEquals("C1", t2.getPropietario().getId());
        assertTrue(t2.isTransferible());
    }

    @Test
    void loadAll_toleraArchivosVacios() throws Exception {
        String[][] headers = new String[][]{
            {"administradores.csv","id,login,password,porcentajeServicio,cuotaEmision"},
            {"clientes.csv","id,login,password,saldo"},
            {"organizadores.csv","id,login,password"},
            {"venues.csv","id,nombre,ubicacion,capacidad,restricciones"},
            {"venue_fechas.csv","venueId,fecha"},
            {"eventos.csv","id,nombre,tipo,estado,fechaISO,horaISO,venueId"},
            {"localidades.csv","id,nombre,precioBase,numerada,aforo,vendidos,eventoId"},
            {"ofertas.csv","id,descuento,activa,localidadId"},
            {"tiquetes.csv","id,tipo,estado,transferible,precio,asiento,propietarioId,localidadId"},
            {"pagos.csv","idPago,fechaISO,total,metodo,estado"},
            {"pago_items.csv","idPago,tiqueteId"},
            {"paquetes.csv","id,precioTotal,beneficios,tipo,transferible"},
            {"paquete_items.csv","paqueteId,tiqueteId"}
        };
        for (String[] h : headers) {
            Path f = dataDir.resolve(h[0]);
            Files.writeString(f, h[1] + System.lineSeparator());
        }

        assertDoesNotThrow(() -> store.loadAll());

        assertNotNull(Main.admin);
        assertEquals("A1", Main.admin.getId());

        assertEquals(0, Main.usuarios.size());
        assertEquals(0, Main.eventos.size());
        assertEquals(0, Main.localidades.size());
        assertEquals(0, Main.inventario.size());
    }
}
*/