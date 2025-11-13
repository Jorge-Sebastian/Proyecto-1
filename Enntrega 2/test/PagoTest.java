package test;

import Boletamaster.Pago;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class PagoTest {

    @Test
    void toCsv_incluyeCamposPrincipales() {
        Pago pago = new Pago(150000, "TARJETA");
        String[] row = pago.toCsv();

        assertEquals(String.valueOf(pago.getIdPago()), row[0]);
        assertDoesNotThrow(() -> LocalDateTime.parse(row[1]));
        assertEquals("150000.0", row[2]);
        assertEquals("TARJETA", row[3]);
        assertEquals("APROBADO", row[4]);
    }

    @Test
    void fromCsv_restauraIdTotalYMetodo() {
        String[] row = {"25", "2024-08-01T10:00:00", "500000.0", "SALDO", "APROBADO"};

        Pago pago = Pago.fromCsv(row);
        assertNotNull(pago);
        assertEquals(25, pago.getIdPago());
        assertEquals(500000.0, pago.getTotal());
        assertEquals("SALDO", pago.getMetodo());
        assertEquals("APROBADO", pago.getEstado());
    }

    @Test
    void setNextId_actualizaLaSecuencia() {
        Pago primerPago = new Pago(10, "SALDO");
        int siguienteEsperado = primerPago.getIdPago() + 5;

        Pago.setNextId(siguienteEsperado);
        Pago segundoPago = new Pago(20, "SALDO");

        assertEquals(siguienteEsperado, segundoPago.getIdPago());
    }
}
