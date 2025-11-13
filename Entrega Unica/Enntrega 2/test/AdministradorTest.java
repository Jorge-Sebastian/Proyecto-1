package test;

import Boletamaster.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class AdministradorTest {

    private Administrador admin;
    private Cliente cliente;
    private Evento evento;
    private Localidad loc;

    @BeforeEach
    void setUp() {
        admin = new Administrador("A1", "admin", "123");
        admin.configurarTarifas(0.10, 3000);
        cliente = new Cliente("C1", "pablo", "111");
        cliente.abonarSaldo(500000);

        Venue v = new Venue("V1", "Coliseo", "Bogotá", 1000);
        evento = new Evento("E1", "Show", v,
                LocalDate.now().plusDays(5),
                LocalTime.of(18, 0), "MUSICAL");
        loc = new Localidad("L1", "VIP", 200000, true, 200, evento);
    }

    @Test
    void configurarTarifas_funciona() {
        assertEquals(0.10, admin.getPorcentajeServicio());
        assertEquals(3000, admin.getCuotaEmision());
    }

    @Test
    void cancelarEvento_cambiaEstado() {
        boolean ok = admin.cancelarEvento(evento);
        assertTrue(ok);
        assertEquals("CANCELADO", evento.getEstado());
    }

    @Test
    void procesarReembolso_abonaSaldoCliente() {
        Tiquete t = new TiqueteSimple("T1", loc.getPrecioVigente(), loc);
        t.marcarVendido(cliente);
        cliente.getTiquetes().add(t);
        double antes = cliente.getSaldo();

        boolean ok = admin.procesarReembolso(cliente, evento);
        assertTrue(ok);
        assertTrue(cliente.getSaldo() > antes);
    }
}