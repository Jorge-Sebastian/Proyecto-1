package Boletamaster;

public class Administrador extends Usuario {
    private double porcentajeServicio; 
    private double cuotaEmision;      

    public Administrador(String id, String login, String password) {
        super(id, login, password);
    }

    public void configurarTarifas(double porcentaje, double cuota) {
        this.porcentajeServicio = porcentaje;
        this.cuotaEmision = cuota;
    }

    public boolean cancelarEvento(Evento e) {
        if (e == null) return false;
        e.setEstado("CANCELADO");
        return true;
    }

    public boolean procesarReembolso(Cliente c, Evento e) {
        if (c == null || e == null) return false;
        double total = 0;
        for (Tiquete t : c.getTiquetes()) {
            if (t.getLocalidad() != null && t.getLocalidad().getEvento() == e) {
                total += t.calcularPrecioTotal(this);
            }
        }
        if (total > 0) c.abonarSaldo(total);
        return true;
    }

    public double getPorcentajeServicio() { return porcentajeServicio; }
    public double getCuotaEmision() { return cuotaEmision; }

    // ---- CSV ----
    public String[] toCsv(){
        return new String[]{ id, login, password,
                String.valueOf(porcentajeServicio), String.valueOf(cuotaEmision) };
    }
    public static Administrador fromCsv(String[] r){
        if (r.length<5) return null;
        Administrador a = new Administrador(r[0], r[1], r[2]);
        try { a.configurarTarifas(Double.parseDouble(r[3]), Double.parseDouble(r[4])); } catch(Exception ignored){}
        return a;
    }
}