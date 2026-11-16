package util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Scanner;

/** Validaciones de teclado. Las fechas de los registros se toman en los gestores. */
public class Entrada {
    private static final Scanner TECLADO = new Scanner(System.in);
    private static final DateTimeFormatter FECHA = DateTimeFormatter
            .ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter
            .ofPattern("dd/MM/uuuu HH:mm:ss").withResolverStyle(ResolverStyle.STRICT);

    public static String leerTexto(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = TECLADO.nextLine().trim();
            if (!texto.isEmpty()) { return texto; }
            System.out.println("El texto no puede quedar vacío.");
        }
    }

    public static int leerEntero(String mensaje, int minimo, int maximo) {
        while (true) {
            System.out.print(mensaje);
            try {
                int valor = Integer.parseInt(TECLADO.nextLine().trim());
                if (valor >= minimo && valor <= maximo) { return valor; }
            } catch (NumberFormatException error) {
                // La misma indicación explica tanto el formato como los límites.
            }
            System.out.println("Ingrese un entero entre " + minimo + " y " + maximo + ".");
        }
    }

    public static long leerTurno(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                long valor = Long.parseLong(TECLADO.nextLine().trim());
                if (valor > 0) { return valor; }
            } catch (NumberFormatException error) {
                // Se repite la lectura sin cambiar la fila de personas.
            }
            System.out.println("Ingrese un número entero positivo de tipo long.");
        }
    }

    public static double leerPrecio(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                String texto = TECLADO.nextLine().trim().replace(',', '.');
                double valor = Double.parseDouble(texto);
                if (!Double.isNaN(valor) && !Double.isInfinite(valor) && valor >= 0) {
                    return valor;
                }
            } catch (NumberFormatException error) {
                // Los puntos o comas se admiten como separador decimal, no de miles.
            }
            System.out.println("Ingrese un precio finito y no negativo, sin separadores de miles.");
        }
    }

    public static int leerCantidadPorDefecto() {
        while (true) {
            System.out.print("Cantidad a alistar [Enter = 1]: ");
            String texto = TECLADO.nextLine().trim();
            if (texto.isEmpty()) { return 1; }
            try {
                int valor = Integer.parseInt(texto);
                if (valor > 0) { return valor; }
            } catch (NumberFormatException error) {
                // Una cantidad inválida se solicita nuevamente.
            }
            System.out.println("Ingrese un entero positivo o presione Enter para alistar uno.");
        }
    }

    public static char leerTipo() {
        while (true) {
            String texto = leerTexto("Tipo de producto (A/B): ");
            if (texto.equalsIgnoreCase("A")) { return 'A'; }
            if (texto.equalsIgnoreCase("B")) { return 'B'; }
            System.out.println("El tipo debe ser A o B.");
        }
    }

    public static LocalDate leerFecha(String mensaje) {
        while (true) {
            try {
                return LocalDate.parse(leerTexto(mensaje + " (dd/MM/aaaa): "), FECHA);
            } catch (DateTimeParseException error) {
                System.out.println("La fecha no existe o no tiene el formato solicitado.");
            }
        }
    }

    private static LocalDateTime leerFechaHora(String mensaje) {
        while (true) {
            try {
                return LocalDateTime.parse(leerTexto(mensaje + " (dd/MM/aaaa HH:mm:ss): "), FECHA_HORA);
            } catch (DateTimeParseException error) {
                System.out.println("La fecha/hora no existe o no tiene el formato solicitado.");
            }
        }
    }

    /** El límite final incluye todas las fracciones del segundo escrito. */
    public static LocalDateTime[] leerIntervalo() {
        while (true) {
            LocalDateTime desde = leerFechaHora("Desde");
            LocalDateTime hasta = leerFechaHora("Hasta").withNano(999999999);
            if (!desde.isAfter(hasta)) { return new LocalDateTime[] { desde, hasta }; }
            System.out.println("El inicio no puede estar después del final. Intente nuevamente.");
        }
    }
}
