package Boletamaster;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    // “Base de datos” en memoria
    static Administrador admin;
    static ArrayList<Usuario> usuarios = new ArrayList<>();
    static ArrayList<Evento> eventos = new ArrayList<>();
    static ArrayList<Localidad> localidades = new ArrayList<>();
    static ArrayList<Tiquete> inventario = new ArrayList<>();

    // Persistencia ultra simple
    static DataStore DS = new DataStore();

    public static void main(String[] args) {
        // 1) Cargar desde CSV (si existen)
        DS.loadAll();

        // 2) Si está vacío, creamos datos de ejemplo y guardamos
        if (usuarios.isEmpty() && eventos.isEmpty() && inventario.isEmpty()) {
            seed();
            DS.saveAll();
        }

        // 3) Menú de la app
        loopPrincipal();
    }

    // --------- Datos de ejemplo ----------
    private static void seed() {
        // admin del sistema
        admin = new Administrador("A1", "admin", "123");
        admin.configurarTarifas(0.10, 3000); // 10% y $3000

        // un organizador
        Organizador org = new Organizador("O1", "org", "333");
        usuarios.add(org);

        // un cliente con saldo
        Cliente pablo = new Cliente("C1", "pablo", "111");
        pablo.abonarSaldo(800000);
        usuarios.add(pablo);

        // un evento
        Venue v = new Venue("V1", "Coliseo", "Bogotá", 8000);
        Evento e = org.crearEvento("E1", "Concierto 1", v,
                LocalDate.now().plusDays(10), LocalTime.of(19, 0), "MUSICAL");
        if (e != null) eventos.add(e);

        // una localidad
        Localidad vip = org.crearLocalidad(e, "L1", "VIP", 200000, true, 200);
        if (vip != null) {
            localidades.add(vip);
            org.crearOferta(vip, "OF1", 0.10); // 10% simple
            // inventario base
            inventario.add(new TiqueteNumerado("T1", vip.getPrecioVigente(), vip, "A1"));
            inventario.add(new TiqueteNumerado("T2", vip.getPrecioVigente(), vip, "A2"));
            inventario.add(new TiqueteSimple("T3", vip.getPrecioVigente(), vip));
        }
    }

    // --------- Loop principal ----------
    private static void loopPrincipal() {
        Scanner sc = new Scanner(System.in);
        int op = -1;

        while (op != 0) {
            System.out.println("\n=== BOLETAMASTER ===");
            System.out.println("1) Iniciar sesión");
            System.out.println("2) Registrarse (Cliente u Organizador)");
            System.out.println("0) Salir");
            System.out.print("Opción: ");
            try { op = Integer.parseInt(sc.nextLine().trim()); } catch (Exception e) { op = -1; }

            if (op == 1) login(sc);
            else if (op == 2) registro(sc);
            else if (op == 0) System.out.println("Adiós :)");
            else System.out.println("Opción inválida.");
        }
        sc.close();
    }

    // --------- Registro y Login ----------
    private static void registro(Scanner sc) {
        System.out.println("\n--- Registro ---");
        System.out.println("1) Cliente");
        System.out.println("2) Organizador");
        System.out.print("Tipo: ");
        int t; try { t = Integer.parseInt(sc.nextLine().trim()); } catch (Exception e) { t = 0; }

        System.out.print("Nuevo login: ");
        String l = sc.nextLine().trim();
        System.out.print("Password: ");
        String p = sc.nextLine().trim();

        String id = "U" + (usuarios.size() + 1);
        if (t == 1) {
            usuarios.add(new Cliente(id, l, p));
            DS.saveAll();
            System.out.println("Cliente creado.");
        } else if (t == 2) {
            usuarios.add(new Organizador(id, l, p));
            DS.saveAll();
            System.out.println("Organizador creado.");
        } else {
            System.out.println("Tipo inválido.");
        }
    }

    private static void login(Scanner sc) {
        System.out.print("Login: ");
        String l = sc.nextLine().trim();
        System.out.print("Password: ");
        String p = sc.nextLine().trim();

        // admin
        if (admin != null && admin.getLogin().equals(l) && admin.getPassword().equals(p)) {
            menuAdmin(sc);
            return;
        }
        // usuarios
        for (Usuario u : usuarios) {
            if (u.getLogin().equals(l) && u.getPassword().equals(p)) {
                if (u instanceof Organizador) menuOrganizador(sc, (Organizador) u);
                else if (u instanceof Cliente)  menuCliente(sc, (Cliente) u);
                return;
            }
        }
        System.out.println("Credenciales inválidas.");
    }

    // --------- Menú Admin ----------
    private static void menuAdmin(Scanner sc) {
        int op = -1;
        while (op != 0) {
            System.out.println("\n--- ADMIN ---");
            System.out.println("1) Ver eventos");
            System.out.println("2) Configurar tarifas");
            System.out.println("3) Cancelar evento + reembolso");
            System.out.println("0) Cerrar sesión");
            System.out.print("Opción: ");
            try { op = Integer.parseInt(sc.nextLine().trim()); } catch (Exception e) { op = -1; }

            if (op == 1) verEventos();
            else if (op == 2) configurarTarifas(sc);
            else if (op == 3) cancelarYReembolsar(sc);
        }
    }

    private static void configurarTarifas(Scanner sc) {
        System.out.print("Porcentaje servicio (0.10 = 10%): ");
        double p; try { p = Double.parseDouble(sc.nextLine().trim()); } catch (Exception e) { p = 0; }
        System.out.print("Cuota de emisión: ");
        double c; try { c = Double.parseDouble(sc.nextLine().trim()); } catch (Exception e) { c = 0; }
        admin.configurarTarifas(p, c);
        DS.saveAll();
        System.out.println("Tarifas actualizadas.");
    }

    private static void cancelarYReembolsar(Scanner sc) {
        if (eventos.isEmpty()) { System.out.println("No hay eventos."); return; }
        verEventos();
        System.out.print("ID/nombre de evento a cancelar: ");
        String nombre = sc.nextLine().trim();
        Evento e = buscarEventoPorNombre(nombre);
        if (e == null) { System.out.println("No existe."); return; }

        admin.cancelarEvento(e);
        // reembolsar a todos los clientes
        int cont = 0;
        for (Usuario u : usuarios) {
            if (u instanceof Cliente) {
                admin.procesarReembolso((Cliente) u, e);
                cont++;
            }
        }
        DS.saveAll();
        System.out.println("Evento cancelado. Reembolsos procesados para " + cont + " clientes.");
    }

    // --------- Menú Organizador ----------
    private static void menuOrganizador(Scanner sc, Organizador o) {
        int op = -1;
        while (op != 0) {
            System.out.println("\n--- ORGANIZADOR (" + o.getLogin() + ") ---");
            System.out.println("1) Crear evento");
            System.out.println("2) Crear localidad");
            System.out.println("3) Crear oferta");
            System.out.println("4) Crear tiquete");
            System.out.println("5) Ver mis ganancias");
            System.out.println("0) Cerrar sesión");
            System.out.print("Opción: ");
            try { op = Integer.parseInt(sc.nextLine().trim()); } catch (Exception e) { op = -1; }

            if (op == 1) crearEvento(sc, o);
            else if (op == 2) crearLocalidad(sc, o);
            else if (op == 3) crearOferta(sc, o);
            else if (op == 4) crearTiquete(sc);
            else if (op == 5) System.out.println(o.verGanancias());
        }
    }

    private static void crearEvento(Scanner sc, Organizador o) {
        System.out.print("Nombre: "); String nombre = sc.nextLine().trim();
        System.out.print("Tipo (MUSICAL/DEPORTIVO/...): "); String tipo = sc.nextLine().trim();
        System.out.print("Fecha (YYYY-MM-DD): "); LocalDate fecha = LocalDate.parse(sc.nextLine().trim());
        System.out.print("Hora  (HH:MM): "); LocalTime hora = LocalTime.parse(sc.nextLine().trim());

        Venue v = new Venue("V" + System.currentTimeMillis(), "Venue-" + nombre, "Ciudad", 5000);
        Evento e = o.crearEvento("E" + (eventos.size()+1), nombre, v, fecha, hora, tipo);
        if (e != null) { eventos.add(e); DS.saveAll(); System.out.println("Evento creado."); }
    }

    private static void crearLocalidad(Scanner sc, Organizador o) {
        if (eventos.isEmpty()) { System.out.println("No hay eventos."); return; }
        verEventos();
        System.out.print("Nombre del evento: ");
        String nom = sc.nextLine().trim();
        Evento e = buscarEventoPorNombre(nom);
        if (e == null) { System.out.println("No existe."); return; }

        System.out.print("Nombre localidad: "); String ln = sc.nextLine().trim();
        System.out.print("Precio base: "); double pb = Double.parseDouble(sc.nextLine().trim());
        System.out.print("¿Numerada? (s/n): "); boolean num = sc.nextLine().trim().equalsIgnoreCase("s");
        System.out.print("Aforo: "); int af = Integer.parseInt(sc.nextLine().trim());

        Localidad l = o.crearLocalidad(e, "L" + (localidades.size()+1), ln, pb, num, af);
        localidades.add(l);
        DS.saveAll();
        System.out.println("Localidad creada.");
    }

    private static void crearOferta(Scanner sc, Organizador o) {
        if (localidades.isEmpty()) { System.out.println("No hay localidades."); return; }
        listarLocalidades();
        System.out.print("Nombre de localidad: ");
        String ln = sc.nextLine().trim();
        Localidad l = buscarLocalidadPorNombre(ln);
        if (l == null) { System.out.println("No existe."); return; }

        System.out.print("Descuento (0.0 a 1.0): ");
        double d = Double.parseDouble(sc.nextLine().trim());
        o.crearOferta(l, "OF" + System.currentTimeMillis(), d);
        DS.saveAll();
        System.out.println("Oferta creada (activa).");
    }

    private static void crearTiquete(Scanner sc) {
        if (localidades.isEmpty()) { System.out.println("No hay localidades."); return; }
        listarLocalidades();
        System.out.print("Localidad: ");
        String ln = sc.nextLine().trim();
        Localidad l = buscarLocalidadPorNombre(ln);
        if (l == null) { System.out.println("No existe."); return; }

        double precio = l.getPrecioVigente();
        String id = "T" + (inventario.size() + 1);

        System.out.print("¿Numerado? (s/n): "); boolean num = sc.nextLine().trim().equalsIgnoreCase("s");
        if (num) {
            System.out.print("Asiento (ej A1): "); String asiento = sc.nextLine().trim();
            inventario.add(new TiqueteNumerado(id, precio, l, asiento));
        } else {
            inventario.add(new TiqueteSimple(id, precio, l));
        }
        DS.saveAll();
        System.out.println("Tiquete creado: " + id);
    }

    // --------- Menú Cliente ----------
    private static void menuCliente(Scanner sc, Cliente c) {
        int op = -1;
        while (op != 0) {
            System.out.println("\n--- CLIENTE (" + c.getLogin() + ") ---  Saldo: $" + (int)c.getSaldo());
            System.out.println("1) Ver eventos");
            System.out.println("2) Ver inventario");
            System.out.println("3) Abonar saldo");
            System.out.println("4) Comprar tiquetes (por ID, con saldo)");
            System.out.println("5) Transferir tiquete");
            System.out.println("0) Cerrar sesión");
            System.out.print("Opción: ");
            try { op = Integer.parseInt(sc.nextLine().trim()); } catch (Exception e) { op = -1; }

            if (op == 1) verEventos();
            else if (op == 2) verInventario();
            else if (op == 3) abonar(sc, c);
            else if (op == 4) comprar(sc, c);
            else if (op == 5) transferir(sc, c);
        }
    }

    private static void abonar(Scanner sc, Cliente c) {
        System.out.print("Valor a abonar: ");
        double v; try { v = Double.parseDouble(sc.nextLine().trim()); } catch (Exception e) { v = 0; }
        c.abonarSaldo(v);
        DS.saveAll();
        System.out.println("Nuevo saldo: $" + (int)c.getSaldo());
    }

    private static void comprar(Scanner sc, Cliente c) {
        verInventario();
        System.out.print("IDs separados por coma (T1,T2,...): ");
        String[] ids = sc.nextLine().trim().split(",");
        ArrayList<Tiquete> items = new ArrayList<>();
        for (String s : ids) {
            Tiquete t = buscarTiquetePorId(s.trim());
            if (t != null) items.add(t);
        }
        if (items.isEmpty()) { System.out.println("Nada para comprar."); return; }

        Pago p = c.comprarTiquetes(items, admin);
        if (p == null) {
            System.out.println("Compra rechazada (saldo o disponibilidad).");
        } else {
            DS.saveAll();
            System.out.println("Compra aprobada. Total: $" + (int)p.getTotal());
        }
    }

    private static void transferir(Scanner sc, Cliente origen) {
        if (origen.getTiquetes().isEmpty()) { System.out.println("No tienes tiquetes."); return; }
        System.out.println("Tus tiquetes:");
        for (Tiquete t : origen.getTiquetes())
            System.out.println("- " + t.getId() + " | " + t.getEstado());

        System.out.print("ID a transferir: ");
        String id = sc.nextLine().trim();
        Tiquete elegido = null;
        for (Tiquete t : origen.getTiquetes()) if (t.getId().equals(id)) elegido = t;
        if (elegido == null) { System.out.println("No lo tienes."); return; }

        System.out.print("Login destino: ");
        String login = sc.nextLine().trim();
        Usuario u = buscarUsuarioPorLogin(login);
        if (!(u instanceof Cliente)) { System.out.println("Destino inválido."); return; }
        Cliente destino = (Cliente) u;

        System.out.print("Tu contraseña: ");
        String pass = sc.nextLine().trim();
        boolean ok = origen.transferirTiquete(elegido, destino, pass);
        if (ok) DS.saveAll();
        System.out.println(ok ? "Transferencia exitosa" : "Falló la transferencia");
    }

    // --------- Listados ----------
    private static void verEventos() {
        if (eventos.isEmpty()) { System.out.println("No hay eventos."); return; }
        for (Evento e : eventos) {
            System.out.println("- " + e.getNombre() + " | " + e.getFecha()
                    + " @ " + e.getVenue().getNombre()
                    + " | Estado: " + e.getEstado());
        }
    }

    private static void verInventario() {
        if (inventario.isEmpty()) {
            System.out.println("No hay tiquetes.");
            return;
        }
        System.out.println("\n-- INVENTARIO --");
        for (Tiquete t : inventario) {
            String ev = (t.getLocalidad()!=null && t.getLocalidad().getEvento()!=null)
                    ? t.getLocalidad().getEvento().getNombre() : "-";
            String extra = (t instanceof TiqueteNumerado) ?
                    (" | asiento " + ((TiqueteNumerado)t).getAsiento()) : "";
            double base = t.getPrecio();
            double total = t.calcularPrecioTotal(admin);
            System.out.println(
                t.getId() + " | " + t.getEstado() + " | Evento: " + ev + extra +
                " | Precio: $" + (int)base + " | Total con tarifas: $" + (int)total
            );
        }
    }

    private static void listarLocalidades() {
        if (localidades.isEmpty()) { System.out.println("No hay localidades."); return; }
        for (Localidad l : localidades) {
            String ev = (l.getEvento()!=null)? l.getEvento().getNombre() : "-";
            System.out.println("- " + l.getNombre() + " (Evento: " + ev + ")");
        }
    }

    // --------- Helpers de búsqueda ----------
    private static Usuario buscarUsuarioPorLogin(String login) {
        for (Usuario u : usuarios) if (u.getLogin().equals(login)) return u;
        return null;
    }

    private static Evento buscarEventoPorNombre(String nombre) {
        for (Evento e : eventos) if (e.getNombre().equalsIgnoreCase(nombre) || e.getId().equalsIgnoreCase(nombre)) return e;
        return null;
    }

    private static Localidad buscarLocalidadPorNombre(String nombre) {
        for (Localidad l : localidades) if (l.getNombre().equalsIgnoreCase(nombre) || l.getId().equalsIgnoreCase(nombre)) return l;
        return null;
    }

    private static Tiquete buscarTiquetePorId(String id) {
        for (Tiquete t : inventario) if (t.getId().equalsIgnoreCase(id)) return t;
        return null;
    }
}