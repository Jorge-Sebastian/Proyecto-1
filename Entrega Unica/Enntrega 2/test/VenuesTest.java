package test;

import Boletamaster.Venue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VenuesTest {

    @Test
    void toCsv_y_fromCsv_conservanDatosBasicos() {
        Venue venue = new Venue("V1", "Movistar Arena", "Bogotá", 12000);
        venue.setRestricciones("Solo adultos");

        String[] row = venue.toCsv();
        assertEquals("V1", row[0]);
        assertEquals("Movistar Arena", row[1]);
        assertEquals("Bogotá", row[2]);
        assertEquals("12000", row[3]);
        assertEquals("Solo adultos", row[4]);

        Venue reconstruido = Venue.fromCsv(row);
        assertNotNull(reconstruido);
        assertEquals("V1", reconstruido.getId());
        assertEquals("Movistar Arena", reconstruido.getNombre());
        assertEquals("Bogotá", reconstruido.getUbicacion());
        assertEquals(12000, reconstruido.getCapacidad());
        assertEquals("Solo adultos", reconstruido.getRestricciones());
    }
}
