package test;

import Boletamaster.Administrador;
import Boletamaster.Cliente;
import Boletamaster.Localidad;
import Boletamaster.Tiquete;
import Boletamaster.TiqueteNumerado;
import Boletamaster.TiqueteSimple;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TiquetesTest {

    @Test
    void calcularPrecioTotal_aplicaTarifasAdministrador() {
        Administrador admin = new Administrador("A1", "admin", "123");
        admin.configurarTarifas(0.10, 3000);

        Localidad localidad = new Localidad("L1", "VIP", 200000, true, 100, null);
        TiqueteSimple tiquete = new TiqueteSimple("T1", 200000, localidad);

        assertEquals(223000, tiquete.calcularPrecioTotal(admin), 0.001);
    }

    @Test
    void marcarVendido_actualizaEstadoYLocalidad() {
        Localidad localidad = new Localidad("L2", "General", 80000, false, 50, null);
        TiqueteSimple tiquete = new TiqueteSimple("T2", 80000, localidad);
        Cliente comprador = new Cliente("C1", "cliente", "123");

        tiquete.marcarVendido(comprador);

        assertEquals("VENDIDO", tiquete.getEstado());
        assertEquals(comprador, tiquete.getPropietario());
        assertEquals(1, localidad.getVendidos());
    }

    @Test
    void transferir_soloFuncionaCuandoEstaVendidoYTransferible() {
        Localidad localidad = new Localidad("L3", "PALCO", 150000, true, 10, null);
        TiqueteSimple tiquete = new TiqueteSimple("T3", 150000, localidad);
        Cliente origen = new Cliente("C2", "origen", "abc");
        Cliente destino = new Cliente("C3", "destino", "xyz");

        assertFalse(tiquete.transferir(destino), "No debería transferir un tiquete no vendido");

        tiquete.marcarVendido(origen);
        assertTrue(tiquete.transferir(destino));
        assertEquals("TRANSFERIDO", tiquete.getEstado());
        assertEquals(destino, tiquete.getPropietario());
    }

    @Test
    void fromCsv_reconstruyeTiqueteNumerado() {
        Cliente cliente = new Cliente("C4", "comprador", "pass");
        Localidad localidad = new Localidad("L4", "VIP BOX", 300000, true, 20, null);
        Map<String, Localidad> locById = new HashMap<>();
        locById.put("L4", localidad);

        String[] row = {
                "T10", "NUM", "VENDIDO", "false", "300000", "A-10", "C4", "L4"
        };

        Tiquete tiquete = Tiquete.fromCsv(row, locById, id -> "C4".equals(id) ? cliente : null);
        assertNotNull(tiquete);
        assertTrue(tiquete instanceof TiqueteNumerado);
        assertEquals("VENDIDO", tiquete.getEstado());
        assertEquals(cliente, tiquete.getPropietario());
        assertEquals(localidad, tiquete.getLocalidad());
        assertEquals(1, cliente.getTiquetes().size(), "fromCsv debería enlazar el tiquete con el cliente");
    }
}
