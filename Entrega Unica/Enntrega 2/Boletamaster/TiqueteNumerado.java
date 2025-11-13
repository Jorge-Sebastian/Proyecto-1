package Boletamaster;

public class TiqueteNumerado extends Tiquete {
    private String asiento;

    public TiqueteNumerado(String id, double precio, Localidad l, String asiento) {
        super(id, precio, l);
        this.asiento = asiento;
    }
    public String getAsiento() { return asiento; }
}