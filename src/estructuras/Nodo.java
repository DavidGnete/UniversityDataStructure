package estructuras;

/** Nodo con enlaces en ambas direcciones. La lista administra sus enlaces. */
public class Nodo {
    private final Object dato;
    Nodo anterior;
    Nodo siguiente;

    public Nodo(Object dato) {
        if (dato == null) {
            throw new IllegalArgumentException("El dato del nodo no puede ser nulo.");
        }
        this.dato = dato;
    }

    public Object getDato() { return dato; }
    public Nodo getAnterior() { return anterior; }
    public Nodo getSiguiente() { return siguiente; }
}
