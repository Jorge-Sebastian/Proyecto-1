package Boletamaster;

import java.util.ArrayList;

public class Cliente extends Usuario {

    private double saldo;
    private ArrayList<Tiquete> tiquetes;
    private ArrayList<Tiquete> carrito;

    public Cliente(String id, String login, String password) {
        super(id, login, password);
        this.saldo = 0;
        this.tiquetes = new ArrayList<>();
        this.carrito = new ArrayList<>();
    }

    public void abonarSaldo(double valor) { if (valor > 0) saldo += valor; }
    public double getSaldo() { return saldo; }

    public ArrayList<Tiquete> getTiquetes() { return tiquetes; }
    void agregarTiquete(Tiquete t) { if (t != null) tiquetes.add(t); }

    public boolean agregarAlCarrito(Tiquete t) {
        if (t == null) return false;
        if (!"DISPONIBLE".equals(t.getEstado())) return false;
        if (carrito.contains(t)) return false;
        carrito.add(t);
        return true;
    }
    public ArrayList<Tiquete> getCarrito() { return carrito; }
    public void limpiarCarrito() { carrito.clear(); }

    public Pago comprarTiquetes(Administrador admin) {
        if (carrito.isEmpty()) return null;
        double total = 0;
        for (Tiquete t : carrito) {
            if (!"DISPONIBLE".equals(t.getEstado())) return null;
            total += t.calcularPrecioTotal(admin);
        }
        if (saldo < total) return null;
        Pago p = new Pago(total, "SALDO");
        for (Tiquete t : carrito) {
            t.marcarVendido(this);
            agregarTiquete(t);
            p.agregarItem(t);
        }
        saldo -= total;
        carrito.clear();
        return p;
    }

    public Pago comprarTiquetes(ArrayList<Tiquete> lista, Administrador admin) {
        if (lista == null || lista.isEmpty()) return null;
        double total = 0;
        for (Tiquete t : lista) {
            if (!"DISPONIBLE".equals(t.getEstado())) return null;
            total += t.calcularPrecioTotal(admin);
        }
        if (saldo < total) return null;
        Pago p = new Pago(total, "SALDO");
        for (Tiquete t : lista) {
            t.marcarVendido(this);
            agregarTiquete(t);
            p.agregarItem(t);
        }
        saldo -= total;
        return p;
    }

    public boolean transferirTiquete(Tiquete t, Cliente destino, String pass) {
        if (t == null || destino == null) return false;
        if (!this.password.equals(pass)) return false;
        if (t.getPropietario() != this) return false;
        return t.transferir(destino);
    }

    // ---- CSV ---- (no guardamos listas aquí; se reconstruyen con tiquetes.csv)
    public String[] toCsv(){ return new String[]{ id, login, password, String.valueOf(saldo) }; }
    public static Cliente fromCsv(String[] r){
        if (r.length<4) return null;
        Cliente c = new Cliente(r[0], r[1], r[2]);
        try { c.saldo = Double.parseDouble(r[3]); } catch(Exception ignored){}
        return c;
    }
}