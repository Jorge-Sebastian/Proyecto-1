package test;

import Boletamaster.Cliente;
import Boletamaster.PaqueteDeluxe;
import Boletamaster.PaqueteTiquetes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PaquetesTest {

    @Test
    void toCsv_identificaPaqueteDeluxe() {
        PaqueteDeluxe paquete = new PaqueteDeluxe("P2", 450000, "Backstage");
        String[] row = paquete.toCsv();

        assertEquals("P2", row[0]);
        assertEquals("450000.0", row[1]);
        assertEquals("Backstage", row[2]);
        assertEquals("DELUXE", row[3]);
        assertEquals("false", row[4], "Los paquetes deluxe no son transferibles");
    }

    @Test
    void fromCsv_paqueteNormalPermiteTransferir() {
        String[] row = {"P1", "300000.0", "Beneficios", "NORMAL", "true"};
        PaqueteTiquetes paquete = PaqueteTiquetes.fromCsv(row);
        assertNotNull(paquete);

        Cliente origen = new Cliente("C1", "origen", "123");
        Cliente destino = new Cliente("C2", "destino", "456");

        paquete.marcarVendido(origen);
        assertTrue(paquete.transferir(destino));
        assertEquals(destino, paquete.getPropietario());
    }

    @Test
    void fromCsv_paqueteDeluxeBloqueaTransferencia() {
        String[] row = {"P3", "600000.0", "VIP", "DELUXE", "false"};
        PaqueteTiquetes paquete = PaqueteTiquetes.fromCsv(row);
        assertNotNull(paquete);
        assertTrue(paquete instanceof PaqueteDeluxe);

        Cliente origen = new Cliente("C3", "origen", "abc");
        Cliente destino = new Cliente("C4", "destino", "xyz");

        paquete.marcarVendido(origen);
        assertFalse(paquete.transferir(destino));
        assertEquals(origen, paquete.getPropietario());
    }
}
