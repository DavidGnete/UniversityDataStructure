# Tarea 2: atención de personas e inventario

Solución de referencia en Java para los dos problemas de la Tarea 2, unidades 3 y 4 de Estructura de Datos. Usa pilas y colas construidas sobre una lista doblemente ligada propia, sin `ArrayList`, `LinkedList`, `Stack`, `Queue`, streams ni funciones de ordenación de colecciones para resolver los algoritmos.

**Condición pendiente del taller:** el enunciado exige usar estrictamente el proyecto desarrollado en clase y adaptar ligeramente sus clases y paquetes. Esta implementación es independiente: es necesario contrastarla y adaptarla a esa base antes de afirmar que cumple esa condición. También se debe confirmar que `Object`, las conversiones de tipo y las API de fechas utilizadas están dentro de lo permitido por el docente.

## Requisitos y ejecución

JDK 17 o superior. No se necesita instalar Visual Studio Code, Maven ni Gradle.

Desde la carpeta donde está este README:

```bash
javac -encoding UTF-8 -d bin -sourcepath src src/app/Main.java
java -Duser.timezone=America/Bogota -cp bin app.Main
```

El primer comando compila; el segundo ejecuta el menú interactivo. La zona horaria se fija a Colombia. En Windows puede usarse PowerShell o CMD con estos mismos comandos si el JDK está instalado. Si no se reconoce `javac`, falta instalar el JDK o agregarlo al PATH.

Para compilar y ejecutar las pruebas:

```bash
javac -encoding UTF-8 -d bin -sourcepath src src/pruebas/Pruebas.java
java -Duser.timezone=America/Bogota -cp bin pruebas.Pruebas
```

También se incluye `.devcontainer/devcontainer.json`, que configura Java 17 para GitHub Codespaces y permite ejecutar estos comandos desde el navegador.

## Archivos

| Ruta | Responsabilidad |
| --- | --- |
| `src/estructuras/Nodo.java` | Dato y enlaces anterior/siguiente. |
| `src/estructuras/ListaDoble.java` | Lista propia, extremos y contador. |
| `src/estructuras/Cola.java` | Fila FIFO, ingreso prioritario y traslado. |
| `src/estructuras/Pila.java` | Estantería LIFO. |
| `src/modelo/Persona.java` | Nombre, fechas, prioridad, Fibonacci y estado. |
| `src/modelo/Producto.java` | Código, tipo, precio, vencimiento y registro. |
| `src/modelo/Movimiento.java` | Registro histórico de entrada o salida. |
| `src/logica/GestorAtencion.java` | Operaciones de admisiones. |
| `src/logica/GestorInventario.java` | Operaciones de estanterías y movimientos. |
| `src/util/Entrada.java` | Lectura y validación del teclado. |
| `src/app/Main.java` | Menús y presentación de resultados. |
| `src/pruebas/Pruebas.java` | Pruebas ejecutables sin dependencias. |

## Correspondencia con el taller

| Literal | Función y uso |
| --- | --- |
| 1a | Admisiones, opción 1: llegada automática y prioridad al frente. |
| 1b | Admisiones, opción 2: filas y cantidad en espera. |
| 1c | Admisiones, opciones 3 y 4: inicio/final, fecha/hora y espera. |
| 1d | Admisiones, opción 5: promedio real del tiempo de atención. |
| 1e | Admisiones, opción 6: traslado manteniendo el orden. |
| 1f | Admisiones, opción 7: atenciones finalizadas en intervalo. |
| 2a | Inventario, opción 1: ingreso a la pila de su tipo. |
| 2b | Inventario, opción 2: lista completa y cantidad por tipo. |
| 2c | Inventario, opción 3: menor fecha de vencimiento entre A y B. |
| 2d | Inventario, opción 4: entradas y salidas en intervalo. |
| 2e | Inventario, opción 5: tipo y cantidad; Enter selecciona uno. |
| 2f | Inventario, opción 6: productos hasta una fecha límite a la cima. |

## Decisiones para explicar en la sustentación

- Se modelan dos filas y una ventanilla. Las personas normales se atienden por orden de llegada. Cada nueva persona especial se coloca primera, tal como dice el literal 1a.
- Los turnos especiales siguen 1, 2, 3, 5, 8, 13...: Fibonacci sin repetir el primer 1, para evitar turnos duplicados. Se controla el límite del tipo `long`.
- Al iniciar atención se solicita y comprueba el número Fibonacci. Si no coincide, no se retira a nadie de la fila.
- La espera es llegada → inicio de atención. El tiempo de atención es inicio → finalización. Se registran ambos instantes para no confundir estas duraciones.
- El promedio es global para las atenciones finalizadas de la ventanilla. Quienes están esperando o tienen un trámite en curso no entran en ese promedio.
- El conteo de personas atendidas utiliza la fecha/hora de finalización. El enunciado no precisa inicio o fin: aquí se utiliza fin y se informa expresamente.
- Al trasladar una fila se agrega su grupo al final de la otra, conservando el orden del grupo y de las personas que ya estaban en el destino. No se permite trasladar una fila a sí misma.
- Cada registro de inventario representa una unidad que puede alistarse. Su código es único, incluso después de venderse; no se implementan capacidades de cajas porque el taller no las define.
- El registro de ingreso se toma del sistema. Una salida registra su propio instante de salida: los conteos usan la fecha del movimiento correspondiente, conservando siempre la fecha original del producto.
- Los intervalos son inclusivos. Se solicitan con segundos y se incluye todo el segundo final escrito.
- Los productos próximos a vencer son los que vencen hasta la fecha límite indicada, inclusive. Se agrupan arriba, conservando su orden relativo; no se ordenan completamente por fecha porque el literal 2f solo pide llevarlos a la parte superior. Si el docente solicita orden ascendente por vencimiento, deberá adaptarse ese criterio.
- La consulta de próximo vencimiento considera todos los productos disponibles y devuelve el de menor fecha. Si alguno llega a vencer durante la ejecución se señala como VENCIDO para que no quede oculto.
- No se admiten nuevos ingresos ya vencidos, códigos vacíos o repetidos, precios negativos/no finitos, tipos distintos de A/B ni ventas superiores al stock.
- Los datos permanecen en memoria durante la ejecución. No hay archivos ni base de datos porque el enunciado no pide persistencia. Reiniciar el programa borra la sesión.
- Apilar, desapilar, encolar y desencolar tienen costo O(1). Los listados, las consultas por intervalo y las reorganizaciones recorren O(n) elementos. Los historiales también usan la lista propia.

## Recorrido de prueba manual

1. Entre a Admisiones y registre Ana (normal), Beto (normal) y Luz (especial) en fila 1.
2. Muestre las filas: Luz debe aparecer antes de Ana y Beto. Guarde el turno de Luz.
3. Inicie atención con un turno equivocado: la fila no debe cambiar. Repita con el correcto.
4. Espere mientras simula un trámite, finalice la atención y consulte el promedio.
5. Registre Eva en fila 2 y traslade fila 1 a fila 2. Debe quedar Eva, Ana, Beto.
6. Consulte el conteo de atenciones usando un intervalo que incluya la hora actual.
7. En Inventario registre A1 con vencimiento lejano, A2 con vencimiento cercano y A3 con vencimiento lejano. Use fechas futuras reales y códigos diferentes.
8. Liste A: debe verse A3, A2, A1. Priorice una fecha que incluya A2 y excluya A1/A3: A2 debe quedar arriba.
9. Registre B1 con una fecha más cercana y consulte el próximo vencimiento: debe aparecer B1.
10. Aliste tipo A dejando cantidad vacía: debe salir una unidad, A2. Intente alistar más del stock: debe rechazarse sin retirar nada.
11. Consulte movimientos: deben existir cuatro entradas y una salida. Reorganizar no debe agregar movimientos.

## Entrega

Revise primero la adaptación al proyecto de clase y complete los nombres reales de los integrantes. La entrega solicitada es un ZIP con nombre como `NombreApellido_OtroNombreApellido.zip`; crear un repositorio no sustituye el envío a la plataforma. Incluya el código de la solución, su documentación y pruebas; excluya `bin`, archivos `.class` y elementos ajenos a esta actividad. Cada integrante debe enviar la misma versión.
