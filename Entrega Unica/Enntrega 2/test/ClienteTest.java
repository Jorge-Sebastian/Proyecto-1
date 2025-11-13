package test;

import Boletamaster.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ClienteTest {

    private Administrador admin;
    private Cliente cliente;
    private Localidad loc;

    @BeforeEach
    void setUp() {
        admin = new Administrador("A1", "admin", "123");
        admin.configurarTarifas(0.10, 3000);
        cliente = new Cliente("C1", "pablo", "111");
        cliente.abonarSaldo(500000);

        Venue v = new Venue("V1", "Coliseo", "Bogotá", 8000);
        Evento ev = new Evento("E1", "Concierto", v,
                LocalDate.now().plusDays(1), LocalTime.of(20, 0), "MUSICAL");
        loc = new Localidad("L1", "VIP", 100000, true, 100, ev);
    }

    @Test
    void abonarSaldo_incrementa() {
        cliente.abonarSaldo(100000);
        assertEquals(600000, cliente.getSaldo(), 1e-6);
    }

    @Test
    void comprarTiquetes_exitoCreaPagoYDescuentaSaldo() {
        Tiquete t1 = new TiqueteSimple("T1", loc.getPrecioVigente(), loc);
        ArrayList<Tiquete> lista = new ArrayList<>();
        lista.add(t1);

        double saldoAntes = cliente.getSaldo();
        Pago p = cliente.comprarTiquetes(lista, admin);

        assertNotNull(p, "Debe crear un pago");
        assertEquals("APROBADO", p.getEstado());
        assertTrue(cliente.getTiquetes().contains(t1));
        assertEquals("VENDIDO", t1.getEstado());
        assertTrue(saldoAntes > cliente.getSaldo(), "Debe descontar el saldo");
    }

    @Test
    void comprarTiquetes_fallaPorSaldoInsuficiente() {
        Cliente pobre = new Cliente("C2", "pobre", "000");
        pobre.abonarSaldo(1000); // insuficiente
        Tiquete t = new TiqueteSimple("T1", loc.getPrecioVigente(), loc);
        ArrayList<Tiquete> lista = new ArrayList<>();
        lista.add(t);

        Pago p = pobre.comprarTiquetes(lista, admin);
        assertNull(p, "No debe aprobar con saldo insuficiente");
    }

    @Test
    void transferirTiquete_exitoso() {
        Tiquete t = new TiqueteSimple("T1", loc.getPrecioVigente(), loc);
        t.marcarVendido(cliente);
        cliente.getTiquetes().add(t);

        Cliente destino = new Cliente("C2", "juan", "222");
        boolean ok = cliente.transferirTiquete(t, destino, "111");

        assertTrue(ok);
        assertEquals(destino, t.getPropietario());
        assertEquals("TRANSFERIDO", t.getEstado());
    }

    @Test
    void transferirTiquete_fallaPorPassword() {
        Tiquete t = new TiqueteSimple("T1", loc.getPrecioVigente(), loc);
        t.marcarVendido(cliente);
        cliente.getTiquetes().add(t);
        Cliente destino = new Cliente("C2", "juan", "222");

        boolean ok = cliente.transferirTiquete(t, destino, "malaClave");
        assertFalse(ok);
    }
}