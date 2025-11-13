package test;

import Boletamaster.Evento;
import Boletamaster.Localidad;
import Boletamaster.Venue;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class EventoTest {

    @Test
    void crearEvento_guardarDatosBasicos() {
        Venue v = new Venue("V1", "Coliseo", "Bogotá", 5000);
        Evento e = new Evento("E1", "Concierto", v,
                LocalDate.of(2025,12,25),
                LocalTime.of(19,0), "MUSICAL");

        assertEquals("E1", e.getId());
        assertEquals("Concierto", e.getNombre());
        assertEquals("MUSICAL", e.getTipo());
        assertEquals("PROGRAMADO", e.getEstado());
        assertEquals(v, e.getVenue());
    }

    @Test
    void agregarLocalidad_yRecuperar() {
        Venue v = new Venue("V1", "Coliseo", "Bogotá", 5000);
        Evento e = new Evento("E1", "Concierto", v,
                LocalDate.of(2025,12,25),
                LocalTime.of(19,0), "MUSICAL");

        Localidad l = new Localidad("L1", "VIP", 200000, true, 100, e);
        e.agregarLocalidad(l);

        assertEquals(1, e.getLocalidades().size());
        assertEquals(l, e.getLocalidades().get(0));
        assertTrue(e.estaDisponible());
    }
}