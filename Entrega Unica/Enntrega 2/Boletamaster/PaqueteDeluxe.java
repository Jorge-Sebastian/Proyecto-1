package Boletamaster;

public class PaqueteDeluxe extends PaqueteTiquetes {
    private String beneficioExtra;
    public PaqueteDeluxe(String id, double precioTotal, String beneficioExtra) {
        super(id, precioTotal, beneficioExtra);
        this.beneficioExtra = beneficioExtra;
        this.transferible = false;
    }
}