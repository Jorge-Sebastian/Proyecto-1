package Boletamaster;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Venue {
    private String id;
    private String nombre;
    private String ubicacion;
    private int capacidad;
    private String restricciones;

    private ArrayList<LocalDate> fechasOcupadas = new ArrayList<>();

    public Venue(String id, String nombre, String ubicacion, int capacidad) {
        this.id = id; this.nombre = nombre; this.ubicacion = ubicacion; this.capacidad = capacidad;
    }

    public boolean reservarFecha(LocalDate fecha) {
        if (fecha == null) return false;
        if (fechasOcupadas.contains(fecha)) return false;
        fechasOcupadas.add(fecha);
        return true;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getUbicacion() { return ubicacion; }
    public int getCapacidad() { return capacidad; }
    public String getRestricciones() { return restricciones; }
    public void setRestricciones(String r) { this.restricciones = r; }
    public List<LocalDate> getFechasOcupadas(){ return fechasOcupadas; }

    // ---- CSV ---- (fechas se guardan aparte en venue_fechas.csv)
    public String[] toCsv(){ return new String[]{ id, nombre, ubicacion, String.valueOf(capacidad), restricciones==null?"":restricciones }; }
    public static Venue fromCsv(String[] r){
        if (r.length<4) return null;
        Venue v = new Venue(r[0], r[1], r[2], parseI(r[3],0));
        if (r.length>4 && r[4]!=null && !r[4].isBlank()) v.setRestricciones(r[4]);
        return v;
    }
    private static int parseI(String x,int d){ try{ return Integer.parseInt(x);}catch(Exception e){return d;} }
}