package app;

import estructuras.ListaDoble;
import estructuras.Nodo;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;
import logica.GestorAtencion;
import logica.GestorInventario;
import modelo.Persona;
import modelo.Producto;
import util.Entrada;

/** Aplicación de consola con un menú para cada problema del taller. */
public class Main {
    private static final GestorAtencion ATENCION = new GestorAtencion();
    private static final GestorInventario INVENTARIO = new GestorInventario();
    private static final DateTimeFormatter FECHA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm:ss");

    public static void main(String[] args) {
        System.out.println("TAREA 2 - PILAS Y COLAS SOBRE LISTAS DOBLEMENTE LIGADAS");
        System.out.println("Los datos se conservan durante esta ejecución.");
        try {
            int opcion;
            do {
                System.out.println("\nMENÚ PRINCIPAL");
                System.out.println("1. Admisiones y Registro");
                System.out.println("2. Inventario de la tienda");
                System.out.println("0. Salir");
                opcion = Entrada.leerEntero("Seleccione: ", 0, 2);
                switch (opcion) {
                    case 1: menuAtencion(); break;
                    case 2: menuInventario(); break;
                    case 0: System.out.println("Programa finalizado."); break;
                    default: break;
                }
            } while (opcion != 0);
        } catch (NoSuchElementException error) {
            System.out.println("\nSe cerró la entrada de teclado. Programa finalizado.");
        }
    }

    private static void menuAtencion() {
        int opcion;
        do {
            System.out.println("\nADMISIONES Y REGISTRO");
            System.out.println("1. Registrar llegada de una persona");
            System.out.println("2. Mostrar filas y personas en espera");
            System.out.println("3. Iniciar atención de la siguiente persona");
            System.out.println("4. Finalizar atención en curso");
            System.out.println("5. Consultar promedio del tiempo de atención");
            System.out.println("6. Trasladar una fila a la otra conservando el orden");
            System.out.println("7. Contar personas atendidas en un intervalo");
            System.out.println("8. Mostrar historial y atención en curso");
            System.out.println("0. Volver");
            opcion = Entrada.leerEntero("Seleccione: ", 0, 8);
            try {
                switch (opcion) {
                    case 1: registrarPersona(); break;
                    case 2: mostrarFilas(); break;
                    case 3: iniciarAtencion(); break;
                    case 4: mostrarAtencionTerminada(ATENCION.finalizarAtencion()); break;
                    case 5: mostrarPromedio(); break;
                    case 6: trasladarFila(); break;
                    case 7: contarAtenciones(); break;
                    case 8: mostrarHistorial(); break;
                    default: break;
                }
            } catch (IllegalArgumentException | IllegalStateException error) {
                System.out.println("No se pudo realizar la operación: " + error.getMessage());
            }
        } while (opcion != 0);
    }

    private static void registrarPersona() {
        String nombre = Entrada.leerTexto("Nombre: ");
        boolean especial = Entrada.leerEntero("¿Atención especial? 1. Sí / 0. No: ", 0, 1) == 1;
        int fila = Entrada.leerEntero("Fila de ingreso (1/2): ", 1, 2);
        Persona persona = ATENCION.registrarPersona(nombre, especial, fila);
        System.out.println("Ingreso registrado: " + persona);
        if (especial) {
            System.out.println("Guarde el turno Fibonacci " + persona.getNumeroFibonacci()
                    + ": debe suministrarlo al iniciar la atención.");
        }
    }

    private static void mostrarFilas() {
        int total = 0;
        for (int fila = 1; fila <= 2; fila++) {
            int cantidad = ATENCION.getCantidadEnFila(fila);
            total += cantidad;
            System.out.println("Fila " + fila + " - En espera: " + cantidad);
            Nodo actual = ATENCION.getFrenteFila(fila);
            int posicion = 1;
            while (actual != null) {
                System.out.println(posicion + ". " + actual.getDato());
                posicion++;
                actual = actual.getSiguiente();
            }
        }
        System.out.println("Total de personas esperando: " + total);
    }

    private static void iniciarAtencion() {
        if (ATENCION.getEnAtencion() != null) {
            throw new IllegalStateException("Finalice la atención en curso con la opción 4.");
        }
        int fila = Entrada.leerEntero("Fila a atender (1/2): ", 1, 2);
        Persona siguiente = ATENCION.consultarSiguiente(fila);
        System.out.println("Siguiente persona: " + siguiente.getNombre());
        long turno = siguiente.esEspecial() ? Entrada.leerTurno("Suministre su número Fibonacci: ") : 0;
        Persona persona = ATENCION.iniciarAtencion(fila, turno);
        System.out.println("Inicio de atención: " + persona.getInicioAtencion().format(FECHA_HORA));
        System.out.printf("Tiempo en la fila: %.3f segundos.%n", persona.getSegundosEspera());
        System.out.println("Cuando termine el trámite, use la opción 4 para finalizar la atención.");
    }

    private static void mostrarAtencionTerminada(Persona persona) {
        System.out.println("Persona atendida: " + persona.getNombre());
        System.out.println("Llegada: " + persona.getLlegada().format(FECHA_HORA));
        System.out.println("Inicio: " + persona.getInicioAtencion().format(FECHA_HORA));
        System.out.println("Finalización: " + persona.getFechaHoraAtencion().format(FECHA_HORA));
        System.out.printf("Espera: %.3f segundos | Atención: %.3f segundos.%n",
                persona.getSegundosEspera(), persona.getSegundosAtencion());
    }

    private static void mostrarPromedio() {
        if (ATENCION.getTotalAtendidos() == 0) {
            System.out.println("Todavía no hay atenciones finalizadas para calcular el promedio.");
        } else {
            System.out.printf("Personas atendidas: %d | Promedio de atención: %.3f segundos.%n",
                    ATENCION.getTotalAtendidos(), ATENCION.getPromedioSegundosAtencion());
        }
    }

    private static void trasladarFila() {
        int origen = Entrada.leerEntero("Fila de origen (1/2): ", 1, 2);
        int destino = Entrada.leerEntero("Fila de destino (1/2): ", 1, 2);
        int cantidad = ATENCION.trasladarFila(origen, destino);
        System.out.println("Personas trasladadas: " + cantidad
                + ". Se agregaron al final del destino conservando su orden.");
    }

    private static void contarAtenciones() {
        LocalDateTime[] intervalo = Entrada.leerIntervalo();
        System.out.println("Atenciones finalizadas en el intervalo: "
                + ATENCION.contarAtendidos(intervalo[0], intervalo[1]));
    }

    private static void mostrarHistorial() {
        Persona actual = ATENCION.getEnAtencion();
        System.out.println("En atención: " + (actual == null ? "Nadie" : actual.getNombre()));
        Nodo nodo = ATENCION.getInicioHistorial();
        if (nodo == null) { System.out.println("No hay atenciones finalizadas."); }
        while (nodo != null) {
            mostrarAtencionTerminada((Persona) nodo.getDato());
            nodo = nodo.getSiguiente();
        }
    }

    private static void menuInventario() {
        int opcion;
        do {
            System.out.println("\nINVENTARIO DE LA TIENDA");
            System.out.println("1. Ingresar un producto a su estantería");
            System.out.println("2. Listar productos por tipo y cantidad disponible");
            System.out.println("3. Consultar el producto más próximo a vencerse");
            System.out.println("4. Contar entradas y salidas en un intervalo");
            System.out.println("5. Alistar productos para la venta");
            System.out.println("6. Llevar a la cima productos próximos a vencer");
            System.out.println("0. Volver");
            opcion = Entrada.leerEntero("Seleccione: ", 0, 6);
            try {
                switch (opcion) {
                    case 1: registrarProducto(); break;
                    case 2: listarInventario(); break;
                    case 3: mostrarProximo(); break;
                    case 4: contarMovimientos(); break;
                    case 5: alistarVenta(); break;
                    case 6: priorizarVencimientos(); break;
                    default: break;
                }
            } catch (IllegalArgumentException | IllegalStateException error) {
                System.out.println("No se pudo realizar la operación: " + error.getMessage());
            }
        } while (opcion != 0);
    }

    private static void registrarProducto() {
        char tipo = Entrada.leerTipo();
        String codigo = Entrada.leerTexto("Código único de la unidad: ");
        double precio = Entrada.leerPrecio("Precio: ");
        LocalDate vencimiento = Entrada.leerFecha("Fecha de vencimiento");
        Producto producto = INVENTARIO.registrarProducto(codigo, tipo, precio, vencimiento);
        System.out.println("Ingreso registrado automáticamente: " + producto);
    }

    private static void listarInventario() {
        char tipo = Entrada.leerTipo();
        System.out.println("Estantería " + tipo + " | Disponibles: " + INVENTARIO.getCantidad(tipo));
        System.out.println("Orden: desde la cima hacia el fondo.");
        Nodo actual = INVENTARIO.getCima(tipo);
        while (actual != null) {
            System.out.println(actual.getDato());
            actual = actual.getSiguiente();
        }
    }

    private static void mostrarProximo() {
        Producto producto = INVENTARIO.consultarMasProximo();
        System.out.println(producto == null ? "No hay productos disponibles."
                : "Menor fecha de vencimiento entre A y B: " + producto);
    }

    private static void contarMovimientos() {
        LocalDateTime[] intervalo = Entrada.leerIntervalo();
        System.out.println("Entradas: " + INVENTARIO.contarMovimientos(true, intervalo[0], intervalo[1]));
        System.out.println("Salidas: " + INVENTARIO.contarMovimientos(false, intervalo[0], intervalo[1]));
    }

    private static void alistarVenta() {
        char tipo = Entrada.leerTipo();
        int disponibles = INVENTARIO.getCantidad(tipo);
        if (disponibles == 0) {
            System.out.println("No hay productos de ese tipo para alistar.");
            return;
        }
        System.out.println("Disponibles: " + disponibles);
        int cantidad = Entrada.leerCantidadPorDefecto();
        ListaDoble alistados = INVENTARIO.alistarVenta(tipo, cantidad);
        System.out.println("Productos retirados del inventario para la venta:");
        Nodo actual = alistados.getPrimero();
        while (actual != null) {
            System.out.println(actual.getDato());
            actual = actual.getSiguiente();
        }
    }

    private static void priorizarVencimientos() {
        char tipo = Entrada.leerTipo();
        LocalDate limite = Entrada.leerFecha("Priorizar vencimientos hasta esta fecha, inclusive");
        int cantidad = INVENTARIO.priorizarVencimientos(tipo, limite);
        System.out.println("Productos ubicados en el grupo superior: " + cantidad);
        System.out.println("Se conserva el orden dentro de cada grupo. No se registran entradas ni salidas.");
    }
}
