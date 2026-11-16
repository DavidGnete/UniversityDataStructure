package estructuras;

/** Lista doblemente ligada propia; no utiliza colecciones de Java. */
public class ListaDoble {
    private Nodo primero;
    private Nodo ultimo;
    private int cantidad;

    public boolean estaVacia() { return cantidad == 0; }
    public int getCantidad() { return cantidad; }
    public Nodo getPrimero() { return primero; }
    public Nodo getUltimo() { return ultimo; }

    public void agregarInicio(Object dato) {
        Nodo nuevo = new Nodo(dato);
        nuevo.siguiente = primero;
        if (primero == null) {
            ultimo = nuevo;
        } else {
            primero.anterior = nuevo;
        }
        primero = nuevo;
        cantidad++;
    }

    public void agregarFinal(Object dato) {
        Nodo nuevo = new Nodo(dato);
        nuevo.anterior = ultimo;
        if (ultimo == null) {
            primero = nuevo;
        } else {
            ultimo.siguiente = nuevo;
        }
        ultimo = nuevo;
        cantidad++;
    }

    public Object retirarInicio() {
        if (estaVacia()) {
            throw new IllegalStateException("La estructura está vacía.");
        }
        Nodo retirado = primero;
        primero = retirado.siguiente;
        if (primero == null) {
            ultimo = null;
        } else {
            primero.anterior = null;
        }
        retirado.siguiente = null;
        cantidad--;
        return retirado.getDato();
    }
}
