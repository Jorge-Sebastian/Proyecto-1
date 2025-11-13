package Boletamaster;

import java.util.ArrayList;
import java.util.Map;

public class Localidad {
    private String id;
    private String nombre;
    private double precioBase;
    private boolean numerada;
    private int aforo;
    private Evento evento;
    private ArrayList<Oferta> ofertas;
    private int vendidos = 0;

    public Localidad(String id, String nombre, double precioBase, boolean numerada, int aforo, Evento evento) {
        this.id = id; this.nombre = nombre; this.precioBase = precioBase; this.numerada = numerada; this.aforo = aforo;
        this.evento = evento; this.ofertas = new ArrayList<>();
    }

    public void agregarOferta(Oferta o) { if (o != null) ofertas.add(o); }
    public ArrayList<Oferta> getOfertas() { return ofertas; }

    public double getPrecioVigente() {
        for (Oferta o : ofertas) if (o.isActiva()) return precioBase * (1 - o.getDescuento());
        return precioBase;
    }

    public Tiquete crearTiqueteSimple(String id) { return new TiqueteSimple(id, getPrecioVigente(), this); }
    public Tiquete crearTiqueteNumerado(String id, String asiento) { return new TiqueteNumerado(id, getPrecioVigente(), this, asiento); }

    public int obtenerAsientosDisponibles() { return Math.max(0, aforo - vendidos); }
    public void marcarVendido() { vendidos++; }

    public String getId() { return id; }
    public Evento getEvento() { return evento; }
    public String getNombre() { return nombre; }
    public double getPrecioBase() { return precioBase; }
    public boolean isNumerada() { return numerada; }
    public int getAforo() { return aforo; }
    public int getVendidos() { return vendidos; }

    // ---- CSV ----
    public String[] toCsv(){
        return new String[]{ id, nombre, String.valueOf(precioBase),
                String.valueOf(numerada), String.valueOf(aforo), String.valueOf(vendidos),
                evento!=null?evento.getId():"" };
    }
    public static Localidad fromCsv(String[] r, Map<String,Evento> eventos){
        if (r.length<7) return null;
        Evento e = eventos.get(r[6]);
        Localidad l = new Localidad(r[0], r[1], parseD(r[2],0), Boolean.parseBoolean(r[3]), parseI(r[4],0), e);
        try { l.vendidos = Integer.parseInt(r[5]); } catch(Exception ignored){}
        if (e!=null) e.agregarLocalidad(l);
        return l;
    }
    private static int parseI(String x,int d){ try{ return Integer.parseInt(x);}catch(Exception e){return d;} }
    private static double parseD(String x,double d){ try{ return Double.parseDouble(x);}catch(Exception e){return d;} }
}