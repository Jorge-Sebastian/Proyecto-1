package test;

import Boletamaster.Evento;
import Boletamaster.Localidad;
import Boletamaster.PaqueteTiquetes;
import Boletamaster.Tiquete;
import Boletamaster.TiqueteSimple;
import Boletamaster.Venue;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class PaqueteItemsTest {

    @Test
    void esTemporada_esFalsoCuandoTodoPerteneceAlMismoEvento() {
        Venue venue = new Venue("V1", "Arena Central", "Bogotá", 10000);
        Evento evento = new Evento("E1", "Show 1", venue,
                LocalDate.of(2025, 6, 10), LocalTime.of(19, 0), "MUSICAL");
        Localidad localidad = new Localidad("L1", "VIP", 200000, true, 100, evento);
        evento.agregarLocalidad(localidad);

        Tiquete tiquete = new TiqueteSimple("T1", 200000, localidad);

        PaqueteTiquetes paquete = new PaqueteTiquetes("P1", 200000, "Beneficios");
        paquete.agregar(tiquete);

        assertFalse(paquete.esTemporada());
    }

    @Test
    void esTemporada_esVerdaderoConEventosDistintos() {
        Venue venue1 = new Venue("V2", "Teatro A", "Medellín", 5000);
        Evento evento1 = new Evento("E2", "Obra A", venue1,
                LocalDate.of(2025, 7, 5), LocalTime.of(20, 0), "TEATRO");
        Localidad loc1 = new Localidad("L2", "Platea", 100000, false, 80, evento1);
        evento1.agregarLocalidad(loc1);

        Venue venue2 = new Venue("V3", "Coliseo B", "Cali", 7000);
        Evento evento2 = new Evento("E3", "Concierto B", venue2,
                LocalDate.of(2025, 8, 12), LocalTime.of(21, 0), "MUSICAL");
        Localidad loc2 = new Localidad("L3", "General", 60000, false, 200, evento2);
        evento2.agregarLocalidad(loc2);

        Tiquete t1 = new TiqueteSimple("T2", 100000, loc1);
        Tiquete t2 = new TiqueteSimple("T3", 60000, loc2);

        PaqueteTiquetes paquete = new PaqueteTiquetes("P2", 160000, "Mixto");
        paquete.agregar(t1);
        paquete.agregar(t2);

        assertTrue(paquete.esTemporada());
    }
}
