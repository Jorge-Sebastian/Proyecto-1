package Boletamaster;

import java.util.ArrayList;

public class PaqueteTiquetes extends Tiquete {
    private double precioTotal;
    private String beneficios;
    private ArrayList<Tiquete> tiquetesIncluidos;

    public PaqueteTiquetes(String id, double precioTotal, String beneficios) {
        super(id, precioTotal, null);
        this.precioTotal = precioTotal; this.beneficios = beneficios;
        this.tiquetesIncluidos = new ArrayList<>();
    }
    public void agregar(Tiquete t) { tiquetesIncluidos.add(t); }

    public boolean esTemporada() {
        Evento e = null;
        for (Tiquete t : tiquetesIncluidos) {
            if (t.getLocalidad() == null) continue;
            Evento ev = t.getLocalidad().getEvento();
            if (e == null) e = ev; else if (e != ev) return true;
        }
        return false;
    }
    public String getId(){ return id; }

    // CSV (paquetes.csv + paquete_items.csv)
    public String[] toCsv(){
        String tipo = (this instanceof PaqueteDeluxe) ? "DELUXE" : "NORMAL";
        return new String[]{ id, String.valueOf(precioTotal), beneficios, tipo, String.valueOf(transferible) };
    }
    public static PaqueteTiquetes fromCsv(String[] r){
        if (r.length<5) return null;
        boolean deluxe = "DELUXE".equalsIgnoreCase(r[3]);
        PaqueteTiquetes p = deluxe ? new PaqueteDeluxe(r[0], parseD(r[1],0), r[2])
                                   : new PaqueteTiquetes(r[0], parseD(r[1],0), r[2]);
        p.transferible = Boolean.parseBoolean(r[4]);
        return p;
    }
    private static double parseD(String x,double d){ try{ return Double.parseDouble(x);}catch(Exception e){return d;} }
}