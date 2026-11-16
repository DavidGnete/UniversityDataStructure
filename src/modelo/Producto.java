package modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Cada registro representa una unidad de inventario con código único. */
public class Producto {
    private final String codigo;
    private final char tipo;
    private final double precio;
    private final LocalDate vencimiento;
    private final LocalDateTime registro;

    public Producto(String codigo, char tipo, double precio, LocalDate vencimiento,
                    LocalDateTime registro) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El código es obligatorio.");
        }
        if (tipo != 'A' && tipo != 'B') {
            throw new IllegalArgumentException("El tipo debe ser A o B.");
        }
        if (Double.isNaN(precio) || Double.isInfinite(precio) || precio < 0) {
            throw new IllegalArgumentException("El precio debe ser finito y no negativo.");
        }
        if (vencimiento == null || registro == null
                || vencimiento.isBefore(registro.toLocalDate())) {
            throw new IllegalArgumentException("No se pueden ingresar productos ya vencidos.");
        }
        this.codigo = codigo.trim();
        this.tipo = tipo;
        this.precio = precio;
        this.vencimiento = vencimiento;
        this.registro = registro;
    }

    public String getCodigo() { return codigo; }
    public char getTipo() { return tipo; }
    public double getPrecio() { return precio; }
    public LocalDate getVencimiento() { return vencimiento; }
    public LocalDateTime getRegistro() { return registro; }

    @Override
    public String toString() {
        DateTimeFormatter fecha = DateTimeFormatter.ofPattern("dd/MM/uuuu");
        DateTimeFormatter fechaHora = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm:ss");
        return "Código: " + codigo + " | Tipo: " + tipo + " | Precio: " + precio
                + " | Vence: " + vencimiento.format(fecha)
                + " | Registro: " + registro.format(fechaHora)
                + (vencimiento.isBefore(LocalDate.now()) ? " | VENCIDO" : "");
    }
}
