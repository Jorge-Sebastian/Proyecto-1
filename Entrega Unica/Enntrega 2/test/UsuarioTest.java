package test;

import Boletamaster.Cliente;
import Boletamaster.Usuario;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UsuarioTest {

    @Test
    void crearUsuario_guardarDatosBasicos() {
        Usuario u = new Cliente("C1", "pablo", "123");
        assertEquals("C1", u.getId());
        assertEquals("pablo", u.getLogin());
        assertEquals("123", u.getPassword());
    }
}