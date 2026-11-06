/* ============================================================
   PLATAFORMA DE MONITOREO AMBIENTAL URBANO
   TAD RepositorioLecturas - VERSION 0.1 INCOMPLETA

   Este es el Tipo Abstracto de Dato del semestre: quien lo usa
   solo conoce las operaciones publicas de abajo. NO deberia
   saber que por dentro hay un arreglo.

   Ese es el contrato. Respetalo mientras lo completas.

   ADVERTENCIA: esta version corre sin caerse y entrega
   resultados incorrectos. Tu trabajo de hoy es descubrir en
   que miente antes de arreglarla.
   ============================================================ */

package repository;

import model.LecturaSensor;

public class RepositorioLecturas {

    private static final int CAPACIDAD_INICIAL = 10;

    private LecturaSensor[] lecturas;
    private int cantidad;

    public RepositorioLecturas() {
        this.lecturas = new LecturaSensor[CAPACIDAD_INICIAL];
        this.cantidad = 0;
    }

    // ---------- OPERACIONES DEL CONTRATO ----------

    /**
     * Agrega una lectura al final del repositorio.
     * @return true si se agrego, false si no habia espacio
     */
    public boolean agregar(LecturaSensor lectura) {
        if (lectura == null) {
            throw new IllegalArgumentException("La lectura no puede ser null");
        }
        if (cantidad == lecturas.length) {
            redimensionar();
        }
        lecturas[cantidad] = lectura;
        cantidad++;
        return true;
    }

    /**
     * Devuelve la lectura que esta en la posicion indicada.
     */
    public LecturaSensor obtener(int posicion) {
        if (posicion < 0 ||
                posicion >= cantidad) {
            return null;
        }
        return lecturas[posicion];
    }

    /**
     * Cantidad de lecturas almacenadas actualmente.
     */
    public int tamano() {
        return cantidad;
    }

    /**
     * Elimina la lectura de la posicion indicada.
     *
     * Desplaza los elementos posteriores y libera la última posición ocupada.
     */
    public void eliminar(int posicion) {
        if (posicion < 0 || posicion >= cantidad) {
            throw new IndexOutOfBoundsException("Posición inválida: " + posicion);
        }
        // Correr todo una posición a la izquierda
        for (int i = posicion; i < cantidad - 1; i++) {
            lecturas[i] = lecturas[i + 1];
        }
        // Anular la última posición ocupada (evita el "duplicado fantasma")
        lecturas[cantidad - 1] = null;
        cantidad--;
    }

    /**
     * Busca la primera lectura de una estacion.
     * Devuelve null si no existe.
     */
    public LecturaSensor buscarPorEstacion(String idSensor) {
        for (int i = 0; i < cantidad; i++) {
            if (lecturas[i] != null
                    && lecturas[i].getIdSensor().equals(idSensor)) {
                return lecturas[i];
            }
        }
        return null;
    }

    /**
     * Reemplaza la lectura de una posicion por otra.
     * Verifica que la posición sea válida y que la nueva lectura no sea null.
     */
    public void actualizar(int posicion, LecturaSensor nueva) {
        if (posicion < 0 || posicion >= cantidad) {
            throw new IndexOutOfBoundsException(
                    "Posición inválida: " + posicion + " (rango 0.." + (cantidad - 1) + ")");
        }
        if (nueva == null) {
            throw new IllegalArgumentException("La lectura no puede ser null");
        }
        lecturas[posicion] = nueva;
    }

    /**
     * Duplica la capacidad interna del arreglo conservando el contenido.
     *
     * Duplica la capacidad interna y conserva todas las lecturas existentes.
     * Se invoca automáticamente desde agregar() cuando el arreglo se llena.
     */
    private void redimensionar() {
        LecturaSensor[] nuevo = new LecturaSensor[lecturas.length * 2];

        for(int i = 0; i < cantidad; i++) {
            nuevo[i] = lecturas[i];
        }
        lecturas = nuevo;
    }

    /**
     * Promedio de PM2.5 de todas las lecturas almacenadas.
     *
     * Calcula el promedio usando únicamente las posiciones ocupadas.
     */
    public double promedioPm25() {
        double suma = 0;
        for (int i = 0; i < cantidad; i++) {
            suma = suma + lecturas[i].getPm25();
        }
        return suma / cantidad;
    }
}
