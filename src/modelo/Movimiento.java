package modelo;

import java.time.LocalDateTime;

/** Un movimiento conserva el instante real de entrada o salida de una unidad. */
public class Movimiento {
    private final Producto producto;
    private final boolean entrada;
    private final LocalDateTime fechaHora;

    public Movimiento(Producto producto, boolean entrada, LocalDateTime fechaHora) {
        if (producto == null || fechaHora == null) {
            throw new IllegalArgumentException("El movimiento necesita producto y fecha.");
        }
        this.producto = producto;
        this.entrada = entrada;
        this.fechaHora = fechaHora;
    }

    public Producto getProducto() { return producto; }
    public boolean esEntrada() { return entrada; }
    public LocalDateTime getFechaHora() { return fechaHora; }
}
