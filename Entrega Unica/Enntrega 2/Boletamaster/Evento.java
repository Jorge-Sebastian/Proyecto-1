package Boletamaster;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Map;

public class Evento {
    private String id;
    private String nombre;
    private String tipo;
    private String estado; 
    private LocalDate fecha;
    private LocalTime hora;
    private Venue venue;
    private ArrayList<Localidad> localidades;

    public Evento(String id, String nombre, Venue v, LocalDate fecha, LocalTime hora, String tipo) {
        this.id = id; this.nombre = nombre; this.venue = v; this.fecha = fecha; this.hora = hora; this.tipo = tipo;
        this.estado = "PROGRAMADO"; this.localidades = new ArrayList<>();
    }

    public void agregarLocalidad(Localidad l) { localidades.add(l); }
    public ArrayList<Localidad> getLocalidades() { return localidades; }

    public boolean estaDisponible() { return !"CANCELADO".equals(estado); }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getTipo() { return tipo; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDate getFecha() { return fecha; }
    public LocalTime getHora() { return hora; }
    public Venue getVenue() { return venue; }

    // ---- CSV ----
    public String[] toCsv(){
        return new String[]{ id, nombre, tipo, estado, fecha.toString(), hora.toString(), venue!=null?venue.getId():"" };
    }
    public static Evento fromCsv(String[] r, Map<String,Venue> venues){
        if (r.length<7) return null;
        Venue v = venues.get(r[6]);
        return new Evento(r[0], r[1], v, LocalDate.parse(r[4]), LocalTime.parse(r[5]), r[2]);
    }
}