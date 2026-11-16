package pruebas;

import estructuras.Cola;
import estructuras.ListaDoble;
import estructuras.Nodo;
import estructuras.Pila;
import java.time.LocalDate;
import java.time.LocalDateTime;
import logica.GestorAtencion;
import logica.GestorInventario;
import modelo.Persona;
import modelo.Producto;

/** Pruebas sin bibliotecas externas; cubren orden, tiempos y cambios de inventario. */
public class Pruebas {
    private static int comprobaciones;

    public static void main(String[] args) {
        probarEstructuras();
        probarTiempos();
        probarAtencion();
        probarFibonacci();
        probarInventario();
        System.out.println("PRUEBAS CORRECTAS: " + comprobaciones + " comprobaciones.");
    }

    private static void verificar(boolean condicion, String mensaje) {
        comprobaciones++;
        if (!condicion) { throw new AssertionError(mensaje); }
    }

    private static void probarEstructuras() {
        ListaDoble lista = new ListaDoble();
        verificar(lista.estaVacia(), "La lista debe iniciar vacía.");
        lista.agregarFinal("B");
        lista.agregarInicio("A");
        lista.agregarFinal("C");
        verificar(lista.getCantidad() == 3, "Cantidad de nodos incorrecta.");
        Nodo nodo = lista.getPrimero();
        verificar(nodo.getAnterior() == null, "El primero no debe tener anterior.");
        verificar(nodo.getDato().equals("A"), "Primer nodo incorrecto.");
        verificar(nodo.getSiguiente().getAnterior() == nodo, "Enlace doble incorrecto.");
        verificar(lista.getUltimo().getDato().equals("C"), "Último nodo incorrecto.");
        verificar(lista.getUltimo().getAnterior().getDato().equals("B"), "Recorrido inverso incorrecto.");
        verificar(lista.getUltimo().getSiguiente() == null, "El último no debe tener siguiente.");
        verificar(lista.retirarInicio().equals("A"), "Retiro de inicio incorrecto.");
        verificar(lista.getPrimero().getAnterior() == null, "Enlace anterior sobrante.");
        lista.retirarInicio();
        lista.retirarInicio();
        verificar(lista.getPrimero() == null && lista.getUltimo() == null
                && lista.getCantidad() == 0, "Extremos incorrectos al vaciar.");
        boolean rechazo = false;
        try { lista.retirarInicio(); } catch (IllegalStateException error) { rechazo = true; }
        verificar(rechazo, "Una lista vacía no puede retirar datos.");

        Cola origen = new Cola();
        Cola destino = new Cola();
        origen.encolar("A");
        origen.encolar("B");
        destino.encolar("X");
        origen.trasladarAlFinal(destino);
        verificar(origen.estaVacia(), "El traslado debe vaciar el origen.");
        verificar(destino.desencolar().equals("X"), "Debe conservarse el destino previo.");
        verificar(destino.desencolar().equals("A"), "Se alteró el orden trasladado.");
        verificar(destino.desencolar().equals("B"), "Se alteró el orden trasladado.");
        rechazo = false;
        try { origen.trasladarAlFinal(origen); } catch (IllegalArgumentException error) { rechazo = true; }
        verificar(rechazo, "No se permite trasladar una fila a sí misma.");

        Pila pila = new Pila();
        pila.apilar("A");
        pila.apilar("B");
        verificar(pila.desapilar().equals("B"), "La pila debe retirar el último ingreso.");
        verificar(pila.desapilar().equals("A"), "Orden LIFO incorrecto.");
    }

    private static void probarTiempos() {
        LocalDateTime llegada = LocalDateTime.of(2026, 11, 15, 8, 0);
        Persona persona = new Persona("Ana", false, 0, llegada);
        verificar(!persona.estaAtendida(), "El estado inicial debe ser no atendida.");
        persona.iniciarAtencion(llegada.plusMinutes(5));
        verificar(persona.getSegundosEspera() == 300, "Se confundió espera con atención.");
        verificar(!persona.estaAtendida(), "No debe finalizarse la atención al iniciarla.");
        persona.finalizarAtencion(llegada.plusMinutes(7));
        verificar(persona.getSegundosAtencion() == 120, "Duración de atención incorrecta.");
        verificar(persona.estaAtendida(), "Debe marcarse atendida al finalizar.");
        boolean rechazo = false;
        try { persona.finalizarAtencion(llegada.plusMinutes(8)); }
        catch (IllegalArgumentException error) { rechazo = true; }
        verificar(rechazo, "No se puede finalizar dos veces la misma atención.");
    }

    private static void probarAtencion() {
        GestorAtencion gestor = new GestorAtencion();
        LocalDateTime antes = LocalDateTime.now().minusSeconds(1);
        Persona ana = gestor.registrarPersona("Ana", false, 1);
        Persona beto = gestor.registrarPersona("Beto", false, 1);
        Persona especial = gestor.registrarPersona("Luz", true, 1);
        gestor.registrarPersona("Eva", false, 2);
        verificar(gestor.consultarSiguiente(1) == especial, "La atención especial debe ir al frente.");
        verificar(especial.getNumeroFibonacci() == 1, "Primer Fibonacci incorrecto.");
        boolean rechazo = false;
        try { gestor.iniciarAtencion(1, 999); } catch (IllegalArgumentException error) { rechazo = true; }
        verificar(rechazo && gestor.getCantidadEnFila(1) == 3,
                "Un turno inválido no debe retirar personas.");
        gestor.iniciarAtencion(1, especial.getNumeroFibonacci());
        verificar(gestor.getCantidadEnFila(1) == 2, "Debe salir de la fila al iniciar atención.");
        verificar(gestor.getTotalAtendidos() == 0, "El historial solo incluye trámites terminados.");
        rechazo = false;
        try { gestor.iniciarAtencion(2, 0); } catch (IllegalStateException error) { rechazo = true; }
        verificar(rechazo, "Una ventanilla no debe atender dos personas simultáneamente.");
        gestor.finalizarAtencion();
        gestor.iniciarAtencion(1, 0);
        gestor.finalizarAtencion();
        verificar(ana.estaAtendida(), "Se perdió el orden de llegada normal.");
        verificar(gestor.getTotalAtendidos() == 2, "Total de atendidos incorrecto.");
        double promedioEsperado = (especial.getSegundosAtencion() + ana.getSegundosAtencion()) / 2;
        verificar(gestor.getPromedioSegundosAtencion() == promedioEsperado, "Promedio incorrecto.");
        LocalDateTime finEspecial = especial.getFechaHoraAtencion();
        verificar(gestor.contarAtendidos(finEspecial, finEspecial) >= 1,
                "Los límites del intervalo deben incluirse.");
        verificar(gestor.contarAtendidos(antes, LocalDateTime.now().plusSeconds(1)) == 2,
                "Conteo de atenciones incorrecto.");
        verificar(gestor.contarAtendidos(antes.minusDays(2), antes.minusDays(1)) == 0,
                "Se contaron atenciones fuera del intervalo.");
        verificar(gestor.trasladarFila(1, 2) == 1, "Cantidad trasladada incorrecta.");
        verificar(gestor.getCantidadEnFila(1) == 0, "La fila de origen debe quedar vacía.");
        Nodo frente = gestor.getFrenteFila(2);
        verificar(((Persona) frente.getDato()).getNombre().equals("Eva"), "Se alteró el destino previo.");
        verificar(frente.getSiguiente().getDato() == beto, "Se perdió una persona al trasladarla.");
        rechazo = false;
        try { gestor.contarAtendidos(LocalDateTime.now(), antes); }
        catch (IllegalArgumentException error) { rechazo = true; }
        verificar(rechazo, "Debe rechazarse un intervalo invertido.");
    }

    private static void probarFibonacci() {
        GestorAtencion gestor = new GestorAtencion();
        long[] esperados = {1, 2, 3, 5, 8, 13};
        for (int i = 0; i < esperados.length; i++) {
            Persona persona = gestor.registrarPersona("Especial " + i, true, 1);
            verificar(persona.getNumeroFibonacci() == esperados[i], "Secuencia Fibonacci incorrecta.");
            verificar(gestor.consultarSiguiente(1) == persona, "Una nueva prioridad debe quedar primera.");
        }
        Persona ultima = null;
        for (int i = esperados.length; i < 91; i++) {
            ultima = gestor.registrarPersona("Especial " + i, true, 1);
        }
        verificar(ultima.getNumeroFibonacci() == 7540113804746346429L,
                "Último Fibonacci representable incorrecto.");
        boolean rechazo = false;
        try { gestor.registrarPersona("Sin turno", true, 1); }
        catch (IllegalStateException error) { rechazo = true; }
        verificar(rechazo && gestor.getCantidadEnFila(1) == 91, "No debe desbordarse el turno.");
    }

    private static void probarInventario() {
        GestorInventario gestor = new GestorInventario();
        LocalDate hoy = LocalDate.now();
        LocalDateTime desde = LocalDateTime.now().minusSeconds(1);
        verificar(gestor.consultarMasProximo() == null, "No hay producto próximo en un inventario vacío.");
        Producto a1 = gestor.registrarProducto("A1", 'A', 100, hoy.plusDays(10));
        Producto a2 = gestor.registrarProducto("A2", 'A', 200, hoy.plusDays(1));
        Producto a3 = gestor.registrarProducto("A3", 'A', 300, hoy.plusDays(20));
        Producto a4 = gestor.registrarProducto("A4", 'A', 400, hoy.plusDays(2));
        Producto b1 = gestor.registrarProducto("B1", 'B', 500, hoy);
        verificar(gestor.getCantidad('A') == 4 && gestor.getCantidad('B') == 1,
                "Los tipos deben tener inventarios independientes.");
        verificar(gestor.consultarMasProximo() == b1, "Debe buscarse el próximo entre ambas estanterías.");
        verificar(gestor.getCima('A').getDato() == a4, "La estantería debe funcionar como pila.");
        verificar(gestor.priorizarVencimientos('A', hoy.plusDays(2)) == 2,
                "Debe incluirse la fecha límite del vencimiento.");
        Nodo actual = gestor.getCima('A');
        verificar(actual.getDato() == a4, "Orden incorrecto en el grupo prioritario.");
        actual = actual.getSiguiente();
        verificar(actual.getDato() == a2, "Se perdió un producto prioritario.");
        actual = actual.getSiguiente();
        verificar(actual.getDato() == a3, "Se alteró el orden de los restantes.");
        verificar(actual.getSiguiente().getDato() == a1, "Se perdió un producto del fondo.");
        LocalDateTime hasta = LocalDateTime.now().plusSeconds(1);
        verificar(gestor.contarMovimientos(true, desde, hasta) == 5, "Conteo de entradas incorrecto.");
        verificar(gestor.contarMovimientos(false, desde, hasta) == 0,
                "Reorganizar una estantería no debe registrar salidas.");
        verificar(gestor.contarMovimientos(true, a1.getRegistro(), a1.getRegistro()) >= 1,
                "La fecha exacta de registro debe incluirse en el intervalo.");
        boolean rechazo = false;
        try { gestor.alistarVenta('A', 5); } catch (IllegalArgumentException error) { rechazo = true; }
        verificar(rechazo && gestor.getCantidad('A') == 4, "Una venta inválida no debe cambiar stock.");
        verificar(gestor.contarMovimientos(false, desde, hasta) == 0,
                "Una venta inválida no debe dejar movimientos.");
        ListaDoble venta = gestor.alistarVenta('A', 2);
        verificar(venta.getCantidad() == 2, "Cantidad alistada incorrecta.");
        verificar(venta.getPrimero().getDato() == a4
                && venta.getUltimo().getDato() == a2, "La venta no respetó la cima de la pila.");
        verificar(gestor.getCantidad('A') == 2 && gestor.getCantidad('B') == 1,
                "La venta debe descontar únicamente el tipo seleccionado.");
        hasta = LocalDateTime.now().plusSeconds(1);
        verificar(gestor.contarMovimientos(false, desde, hasta) == 2, "Conteo de salidas incorrecto.");
        verificar(gestor.contarMovimientos(true, desde, hasta) == 5,
                "Las ventas no deben borrar el historial de entradas.");
        rechazo = false;
        try { gestor.registrarProducto("a2", 'A', 10, hoy.plusDays(1)); }
        catch (IllegalArgumentException error) { rechazo = true; }
        verificar(rechazo, "Un código ya utilizado no debe duplicarse, aunque su unidad se haya vendido.");
        rechazo = false;
        try { gestor.registrarProducto("X", 'A', Double.NaN, hoy); }
        catch (IllegalArgumentException error) { rechazo = true; }
        verificar(rechazo, "El precio NaN no debe admitirse.");
        rechazo = false;
        try { gestor.registrarProducto("X", 'A', Double.POSITIVE_INFINITY, hoy); }
        catch (IllegalArgumentException error) { rechazo = true; }
        verificar(rechazo, "El precio infinito no debe admitirse.");
        rechazo = false;
        try { gestor.registrarProducto("X", 'A', -1, hoy); }
        catch (IllegalArgumentException error) { rechazo = true; }
        verificar(rechazo, "Un precio negativo no debe admitirse.");
        rechazo = false;
        try { gestor.registrarProducto("X", 'A', 100, hoy.minusDays(1)); }
        catch (IllegalArgumentException error) { rechazo = true; }
        verificar(rechazo && gestor.getCantidad('A') == 2,
                "Un producto ya vencido no debe ingresar al inventario.");
        verificar(gestor.contarMovimientos(true, desde, LocalDateTime.now().plusSeconds(1)) == 5,
                "Un registro inválido no debe generar movimientos.");
    }
}
