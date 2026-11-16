package logica;

import estructuras.ListaDoble;
import estructuras.Nodo;
import estructuras.Pila;
import java.time.LocalDate;
import java.time.LocalDateTime;
import modelo.Movimiento;
import modelo.Producto;

/** Estanterías independientes A/B e historial de entradas y salidas. */
public class GestorInventario {
    private final Pila estanteriaA = new Pila();
    private final Pila estanteriaB = new Pila();
    private final ListaDoble movimientos = new ListaDoble();

    private Pila obtenerEstanteria(char tipo) {
        if (tipo == 'A') { return estanteriaA; }
        if (tipo == 'B') { return estanteriaB; }
        throw new IllegalArgumentException("El tipo debe ser A o B.");
    }

    private boolean existeCodigo(String codigo) {
        Nodo actual = movimientos.getPrimero();
        while (actual != null) {
            Movimiento movimiento = (Movimiento) actual.getDato();
            if (movimiento.getProducto().getCodigo().equalsIgnoreCase(codigo)) {
                return true;
            }
            actual = actual.getSiguiente();
        }
        return false;
    }

    public Producto registrarProducto(String codigo, char tipo, double precio,
                                      LocalDate vencimiento) {
        Pila estanteria = obtenerEstanteria(tipo);
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El código es obligatorio.");
        }
        if (existeCodigo(codigo.trim())) {
            throw new IllegalArgumentException("El código ya se usó. Cada unidad requiere código único.");
        }
        LocalDateTime ahora = LocalDateTime.now();
        Producto producto = new Producto(codigo, tipo, precio, vencimiento, ahora);
        estanteria.apilar(producto);
        movimientos.agregarFinal(new Movimiento(producto, true, ahora));
        return producto;
    }

    public int getCantidad(char tipo) { return obtenerEstanteria(tipo).getCantidad(); }
    public Nodo getCima(char tipo) { return obtenerEstanteria(tipo).getCima(); }

    /** Recorre ambas pilas sin retirar productos; la fecha menor tiene prioridad. */
    public Producto consultarMasProximo() {
        Producto proximo = null;
        for (char tipo = 'A'; tipo <= 'B'; tipo++) {
            Nodo actual = obtenerEstanteria(tipo).getCima();
            while (actual != null) {
                Producto producto = (Producto) actual.getDato();
                if (proximo == null || producto.getVencimiento().isBefore(proximo.getVencimiento())) {
                    proximo = producto;
                }
                actual = actual.getSiguiente();
            }
        }
        return proximo;
    }

    /** Valida primero toda la cantidad: una solicitud inválida no modifica stock. */
    public ListaDoble alistarVenta(char tipo, int cantidad) {
        Pila estanteria = obtenerEstanteria(tipo);
        if (cantidad <= 0 || cantidad > estanteria.getCantidad()) {
            throw new IllegalArgumentException("La cantidad debe estar entre 1 y el stock disponible.");
        }
        ListaDoble alistados = new ListaDoble();
        LocalDateTime salida = LocalDateTime.now();
        for (int i = 0; i < cantidad; i++) {
            Producto producto = (Producto) estanteria.desapilar();
            alistados.agregarFinal(producto);
            movimientos.agregarFinal(new Movimiento(producto, false, salida));
        }
        return alistados;
    }

    /**
     * Lleva a la cima los vencimientos hasta la fecha límite, inclusive.
     * Las pilas auxiliares conservan el orden relativo de cada grupo.
     * Reubicar productos no constituye una entrada o salida del inventario.
     */
    public int priorizarVencimientos(char tipo, LocalDate limite) {
        Pila estanteria = obtenerEstanteria(tipo);
        if (limite == null || limite.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha límite debe ser hoy o posterior.");
        }
        Pila proximos = new Pila();
        Pila restantes = new Pila();
        while (!estanteria.estaVacia()) {
            Producto producto = (Producto) estanteria.desapilar();
            if (!producto.getVencimiento().isAfter(limite)) {
                proximos.apilar(producto);
            } else {
                restantes.apilar(producto);
            }
        }
        int cantidad = proximos.getCantidad();
        while (!restantes.estaVacia()) { estanteria.apilar(restantes.desapilar()); }
        while (!proximos.estaVacia()) { estanteria.apilar(proximos.desapilar()); }
        return cantidad;
    }

    public int contarMovimientos(boolean entradas, LocalDateTime desde, LocalDateTime hasta) {
        if (desde == null || hasta == null || desde.isAfter(hasta)) {
            throw new IllegalArgumentException("El intervalo de tiempo no es válido.");
        }
        int cantidad = 0;
        Nodo actual = movimientos.getPrimero();
        while (actual != null) {
            Movimiento movimiento = (Movimiento) actual.getDato();
            LocalDateTime fecha = movimiento.getFechaHora();
            if (movimiento.esEntrada() == entradas
                    && !fecha.isBefore(desde) && !fecha.isAfter(hasta)) {
                cantidad++;
            }
            actual = actual.getSiguiente();
        }
        return cantidad;
    }
}
