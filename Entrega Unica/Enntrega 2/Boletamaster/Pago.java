package Boletamaster;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Pago {
    private static int SEC = 1;

    private int idPago;
    private LocalDateTime fecha;
    private double total;
    private String metodo;
    private String estado;           
    private ArrayList<Tiquete> items;

    public Pago(double total, String metodo) {
        this.idPago = SEC++;
        this.fecha = LocalDateTime.now();
        this.total = total; this.metodo = metodo;
        this.estado = "APROBADO"; this.items = new ArrayList<>();
    }

    public void agregarItem(Tiquete t) { items.add(t); }
    public double getTotal() { return total; }
    public int getIdPago() { return idPago; }
    public String getEstado() { return estado; }
    public String getMetodo() { return metodo; }
    public LocalDateTime getFecha() { return fecha; }

    // CSV (pago_items.csv guarda el detalle)
    public String[] toCsv(){
        return new String[]{ String.valueOf(idPago), fecha.toString(),
                String.valueOf(total), metodo, estado };
    }
    public static Pago fromCsv(String[] r){
        try{
            Pago p = new Pago(0,"CARGA");
            p.idPago = Integer.parseInt(r[0]);
            p.fecha = LocalDateTime.parse(r[1]);
            p.total = Double.parseDouble(r[2]);
            p.metodo = r[3];
            p.estado = r[4];
            return p;
        }catch(Exception e){ return null; }
    }
    public static void setNextId(int next){ SEC = Math.max(SEC, next); }
}