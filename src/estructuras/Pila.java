package estructuras;

/** Pila LIFO: el último producto ingresado se encuentra en la cima. */
public class Pila {
    private final ListaDoble datos = new ListaDoble();

    public void apilar(Object dato) { datos.agregarInicio(dato); }
    public Object desapilar() { return datos.retirarInicio(); }
    public boolean estaVacia() { return datos.estaVacia(); }
    public int getCantidad() { return datos.getCantidad(); }
    public Nodo getCima() { return datos.getPrimero(); }
}
