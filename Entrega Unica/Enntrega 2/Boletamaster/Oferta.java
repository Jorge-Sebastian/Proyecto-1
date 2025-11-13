package Boletamaster;

import java.util.Map;

public class Oferta {
    String id;
    double descuento; 
    boolean activa;
    Localidad localidad; // para reconstruir relación

    public Oferta(String id, double descuento) {
        this.id = id; this.descuento = descuento; this.activa = true;
    }

    public String getId() { return id; }
    public double getDescuento() { return descuento; }
    public boolean isActiva() { return activa; }

    // ---- CSV ----
    public String[] toCsv(){
        return new String[]{ id, String.valueOf(descuento), String.valueOf(activa),
                (localidad!=null?localidad.getId():"") };
    }
    public static Oferta fromCsv(String[] r, Map<String,Localidad> locById){
        if (r.length<4) return null;
        Oferta o = new Oferta(r[0], parseD(r[1],0));
        try { o.activa = Boolean.parseBoolean(r[2]); } catch(Exception ignored){}
        Localidad l = locById.get(r[3]);
        o.localidad = l;
        return o;
    }
    private static double parseD(String x,double d){ try{ return Double.parseDouble(x);}catch(Exception e){return d;} }
}