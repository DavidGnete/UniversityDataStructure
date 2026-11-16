package estructuras;

/** Cola FIFO. La inserción al frente se reserva para la atención especial. */
public class Cola {
    private final ListaDoble datos = new ListaDoble();

    public void encolar(Object dato) { datos.agregarFinal(dato); }
    public void encolarPrioritario(Object dato) { datos.agregarInicio(dato); }
    public Object desencolar() { return datos.retirarInicio(); }
    public boolean estaVacia() { return datos.estaVacia(); }
    public int getCantidad() { return datos.getCantidad(); }
    public Nodo getFrente() { return datos.getPrimero(); }

    public Object consultarFrente() {
        if (estaVacia()) {
            throw new IllegalStateException("La fila está vacía.");
        }
        return datos.getPrimero().getDato();
    }

    /** Conserva el orden de origen y agrega el grupo al final del destino. */
    public void trasladarAlFinal(Cola destino) {
        if (destino == null || destino == this) {
            throw new IllegalArgumentException("Seleccione una fila de destino diferente.");
        }
        while (!estaVacia()) {
            destino.encolar(desencolar());
        }
    }
}
