package Boletamaster;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class Organizador extends Cliente {
    private ArrayList<Evento> listaEventos;

    public Organizador(String id, String login, String password) {
        super(id, login, password);
        listaEventos = new ArrayList<>();
    }

    public Evento crearEvento(String id, String nombre, Venue v, LocalDate fecha, LocalTime hora, String tipo) {
        if (v != null && !v.reservarFecha(fecha)) {
            System.out.println("El venue ya tiene un evento ese día.");
            return null;
        }
        Evento e = new Evento(id, nombre, v, fecha, hora, tipo);
        listaEventos.add(e);
        return e;
    }

    public Localidad crearLocalidad(Evento e, String id, String nombre, double precio, boolean numerada, int aforo) {
        Localidad l = new Localidad(id, nombre, precio, numerada, aforo, e);
        if (e != null) e.agregarLocalidad(l);
        return l;
    }

    public Oferta crearOferta(Localidad l, String id, double descuento) {
        Oferta o = new Oferta(id, descuento);
        l.agregarOferta(o);
        return o;
    }

    public String verGanancias() {
        return "Ganancias totales (aprox): $" + (listaEventos.size() * 100000);
    }
    public ArrayList<Evento> getEventos() { return listaEventos; }

    // ---- CSV ---- (igual que Cliente pero sin saldo extra)
    public String[] toCsv(){ return new String[]{ getId(), getLogin(), getPassword() }; }
    public static Organizador fromCsv(String[] r){
        if (r.length<3) return null;
        return new Organizador(r[0], r[1], r[2]);
    }
}