package test;

import Boletamaster.Venue;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class VenueFechasTest {

    @Test
    void reservarFecha_noDuplicaFechas() {
        Venue venue = new Venue("V2", "Coliseo", "Medellín", 8000);
        LocalDate fecha = LocalDate.of(2025, 5, 20);

        assertTrue(venue.reservarFecha(fecha));
        assertFalse(venue.reservarFecha(fecha));
        assertEquals(1, venue.getFechasOcupadas().size());
        assertEquals(fecha, venue.getFechasOcupadas().get(0));
    }

    @Test
    void reservarFecha_rechazaNulos() {
        Venue venue = new Venue("V3", "Teatro", "Cali", 2000);
        assertFalse(venue.reservarFecha(null));
        assertTrue(venue.getFechasOcupadas().isEmpty());
    }
}