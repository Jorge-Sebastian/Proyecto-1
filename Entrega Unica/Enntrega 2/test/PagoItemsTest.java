package test;

import Boletamaster.Administrador;
import Boletamaster.Cliente;
import Boletamaster.Localidad;
import Boletamaster.Pago;
import Boletamaster.Tiquete;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class PagoItemsTest {

    @Test
    void comprarTiquetes_registraItemsEnElPago() throws Exception {
        Administrador admin = new Administrador("A1", "admin", "123");
        admin.configurarTarifas(0, 0);

        Cliente cliente = new Cliente("C1", "cliente", "pass");
        cliente.abonarSaldo(500000);

        Localidad localidad = new Localidad("L1", "General", 100000, false, 100, null);
        Tiquete t1 = localidad.crearTiqueteSimple("T1");
        Tiquete t2 = localidad.crearTiqueteSimple("T2");

        assertTrue(cliente.agregarAlCarrito(t1));
        assertTrue(cliente.agregarAlCarrito(t2));

        Pago pago = cliente.comprarTiquetes(admin);
        assertNotNull(pago);

        Field itemsField = Pago.class.getDeclaredField("items");
        itemsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        ArrayList<Tiquete> items = (ArrayList<Tiquete>) itemsField.get(pago);

        assertEquals(2, items.size());
        assertTrue(items.contains(t1));
        assertTrue(items.contains(t2));
        assertEquals("VENDIDO", t1.getEstado());
        assertEquals("VENDIDO", t2.getEstado());
    }
}
