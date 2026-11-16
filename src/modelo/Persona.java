package modelo;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Datos de una persona y de las etapas de su atención. */
public class Persona {
    private final String nombre;
    private final LocalDateTime llegada;
    private final boolean especial;
    private final long numeroFibonacci;
    private LocalDateTime inicioAtencion;
    private LocalDateTime fechaHoraAtencion;
    private boolean atendida = false;

    public Persona(String nombre, boolean especial, long numeroFibonacci,
                   LocalDateTime llegada) {
        if (nombre == null || nombre.trim().isEmpty() || llegada == null) {
            throw new IllegalArgumentException("El nombre y la fecha de llegada son obligatorios.");
        }
        if ((especial && numeroFibonacci <= 0) || (!especial && numeroFibonacci != 0)) {
            throw new IllegalArgumentException("El turno no corresponde al tipo de atención.");
        }
        this.nombre = nombre.trim();
        this.especial = especial;
        this.numeroFibonacci = numeroFibonacci;
        this.llegada = llegada;
    }

    public String getNombre() { return nombre; }
    public LocalDateTime getLlegada() { return llegada; }
    public boolean esEspecial() { return especial; }
    public long getNumeroFibonacci() { return numeroFibonacci; }
    public LocalDateTime getInicioAtencion() { return inicioAtencion; }
    public LocalDateTime getFechaHoraAtencion() { return fechaHoraAtencion; }
    public boolean estaAtendida() { return atendida; }

    public void iniciarAtencion(LocalDateTime fecha) {
        if (inicioAtencion != null || fecha == null || fecha.isBefore(llegada)) {
            throw new IllegalArgumentException("La fecha de inicio de atención no es válida.");
        }
        inicioAtencion = fecha;
    }

    public void finalizarAtencion(LocalDateTime fecha) {
        if (inicioAtencion == null || atendida || fecha == null
                || fecha.isBefore(inicioAtencion)) {
            throw new IllegalArgumentException("La fecha de finalización no es válida.");
        }
        fechaHoraAtencion = fecha;
        atendida = true;
    }

    public double getSegundosEspera() {
        if (inicioAtencion == null) {
            throw new IllegalStateException("La persona todavía no ha iniciado su atención.");
        }
        return Duration.between(llegada, inicioAtencion).toMillis() / 1000.0;
    }

    public double getSegundosAtencion() {
        if (!atendida) {
            throw new IllegalStateException("La atención no ha finalizado.");
        }
        return Duration.between(inicioAtencion, fechaHoraAtencion).toMillis() / 1000.0;
    }

    @Override
    public String toString() {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm:ss");
        return nombre + " | Llegada: " + llegada.format(formato)
                + " | Especial: " + (especial ? "Sí, turno " + numeroFibonacci : "No")
                + " | Atendida: " + (atendida ? "Sí" : "No");
    }
}
