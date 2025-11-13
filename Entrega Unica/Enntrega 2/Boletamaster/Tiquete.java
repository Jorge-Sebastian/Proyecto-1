package Boletamaster;

import java.util.Map;
import java.util.function.Function;

public abstract class Tiquete {
    protected String id;
    protected String estado;          
    protected boolean transferible;
    protected Cliente propietario;
    protected double precio;
    protected Localidad localidad;

    public Tiquete(String id, double precio, Localidad localidad) {
        this.id = id; this.precio = precio; this.localidad = localidad;
        this.estado = "DISPONIBLE"; this.transferible = true;
    }

    public double calcularPrecioTotal(Administrador admin) {
        double total = precio;
        total += total * admin.getPorcentajeServicio();
        total += admin.getCuotaEmision();
        return total;
    }

    public void marcarVendido(Cliente c) {
        propietario = c; estado = "VENDIDO";
        if (localidad != null) localidad.marcarVendido();
    }

    public boolean transferir(Cliente destino) {
        if (!transferible || !"VENDIDO".equals(estado)) return false;
        propietario = destino; estado = "TRANSFERIDO";
        return true;
    }

    public String getId() { return id; }
    public String getEstado() { return estado; }
    public Cliente getPropietario() { return propietario; }
    public Localidad getLocalidad() { return localidad; }
    public double getPrecio() { return precio; }

    // ---- CSV común ----
    // formato: id,tipo,estado,transferible,precio,asiento,propietarioId,localidadId
    public String[] toCsv(){
        String tipo = (this instanceof TiqueteNumerado) ? "NUM" : "SIMPLE";
        String asiento = (this instanceof TiqueteNumerado) ? ((TiqueteNumerado)this).getAsiento() : "";
        return new String[]{
                id, tipo, estado, String.valueOf(transferible), String.valueOf(precio),
                asiento, propietario!=null?propietario.getId():"", localidad!=null?localidad.getId():""
        };
    }
    public static Tiquete fromCsv(String[] r, Map<String,Localidad> locById, Function<String,Cliente> findCliente){
        if (r.length<8) return null;
        String id = r[0]; String tipo = r[1]; String estado = r[2];
        boolean transf = Boolean.parseBoolean(r[3]);
        double precio = parseD(r[4],0); String asiento = r[5];
        Cliente prop = findCliente.apply(r[6]);
        Localidad loc = locById.get(r[7]);

        Tiquete t = "NUM".equalsIgnoreCase(tipo)
                ? new TiqueteNumerado(id, precio, loc, asiento)
                : new TiqueteSimple(id, precio, loc);
        t.estado = estado; t.transferible = transf; t.propietario = prop;
        if (prop!=null) prop.agregarTiquete(t);
        return t;
    }
    private static double parseD(String x,double d){ try{ return Double.parseDouble(x);}catch(Exception e){return d;} }
}