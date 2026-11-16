package logica;

import estructuras.Cola;
import estructuras.ListaDoble;
import estructuras.Nodo;
import java.time.LocalDateTime;
import modelo.Persona;

/** Administra dos filas, una ventanilla y el historial de atenciones terminadas. */
public class GestorAtencion {
    private final Cola filaUno = new Cola();
    private final Cola filaDos = new Cola();
    private final ListaDoble historial = new ListaDoble();
    private Persona enAtencion;
    private long anteriorFibonacci = 1;
    private long actualFibonacci = 1;
    private double segundosAcumulados;

    private Cola obtenerFila(int numero) {
        if (numero == 1) { return filaUno; }
        if (numero == 2) { return filaDos; }
        throw new IllegalArgumentException("La fila debe ser 1 o 2.");
    }

    /** Genera 1, 2, 3, 5, 8... sin repetir el primer 1 y controla desbordamientos. */
    private long siguienteFibonacci() {
        if (actualFibonacci == 0) {
            throw new IllegalStateException("Se agotaron los turnos Fibonacci de tipo long.");
        }
        long asignado = actualFibonacci;
        if (anteriorFibonacci > Long.MAX_VALUE - actualFibonacci) {
            actualFibonacci = 0;
        } else {
            long siguiente = anteriorFibonacci + actualFibonacci;
            anteriorFibonacci = actualFibonacci;
            actualFibonacci = siguiente;
        }
        return asignado;
    }

    public Persona registrarPersona(String nombre, boolean especial, int numeroFila) {
        Cola fila = obtenerFila(numeroFila);
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        long turno = especial ? siguienteFibonacci() : 0;
        Persona persona = new Persona(nombre, especial, turno, LocalDateTime.now());
        if (especial) {
            fila.encolarPrioritario(persona);
        } else {
            fila.encolar(persona);
        }
        return persona;
    }

    public int getCantidadEnFila(int numeroFila) {
        return obtenerFila(numeroFila).getCantidad();
    }

    public Nodo getFrenteFila(int numeroFila) {
        return obtenerFila(numeroFila).getFrente();
    }

    public Persona consultarSiguiente(int numeroFila) {
        return (Persona) obtenerFila(numeroFila).consultarFrente();
    }

    public Persona getEnAtencion() { return enAtencion; }
    public Nodo getInicioHistorial() { return historial.getPrimero(); }
    public int getTotalAtendidos() { return historial.getCantidad(); }

    /** El turno se verifica antes de retirar a la persona de la fila. */
    public Persona iniciarAtencion(int numeroFila, long turnoSuministrado) {
        if (enAtencion != null) {
            throw new IllegalStateException("Finalice primero la atención en curso.");
        }
        Cola fila = obtenerFila(numeroFila);
        Persona persona = (Persona) fila.consultarFrente();
        if (persona.esEspecial() && persona.getNumeroFibonacci() != turnoSuministrado) {
            throw new IllegalArgumentException("El número Fibonacci no coincide. La fila se conserva.");
        }
        persona.iniciarAtencion(LocalDateTime.now());
        fila.desencolar();
        enAtencion = persona;
        return persona;
    }

    /** Separar inicio y fin permite medir atención y espera como tiempos distintos. */
    public Persona finalizarAtencion() {
        if (enAtencion == null) {
            throw new IllegalStateException("No hay ninguna atención en curso.");
        }
        Persona persona = enAtencion;
        persona.finalizarAtencion(LocalDateTime.now());
        historial.agregarFinal(persona);
        segundosAcumulados += persona.getSegundosAtencion();
        enAtencion = null;
        return persona;
    }

    public double getPromedioSegundosAtencion() {
        if (historial.estaVacia()) { return 0; }
        return segundosAcumulados / historial.getCantidad();
    }

    public int trasladarFila(int origen, int destino) {
        Cola filaOrigen = obtenerFila(origen);
        Cola filaDestino = obtenerFila(destino);
        int cantidad = filaOrigen.getCantidad();
        filaOrigen.trasladarAlFinal(filaDestino);
        return cantidad;
    }

    /** Cuenta atenciones finalizadas dentro de un intervalo inclusivo. */
    public int contarAtendidos(LocalDateTime desde, LocalDateTime hasta) {
        if (desde == null || hasta == null || desde.isAfter(hasta)) {
            throw new IllegalArgumentException("El intervalo de tiempo no es válido.");
        }
        int cantidad = 0;
        Nodo actual = historial.getPrimero();
        while (actual != null) {
            Persona persona = (Persona) actual.getDato();
            LocalDateTime fecha = persona.getFechaHoraAtencion();
            if (!fecha.isBefore(desde) && !fecha.isAfter(hasta)) {
                cantidad++;
            }
            actual = actual.getSiguiente();
        }
        return cantidad;
    }
}
