package structures.management;

import model.LecturaSensor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * Gestor de las estructuras de datos del módulo integrador.
 *
 * Incluye historial de eventos, navegación circular de alertas,
 * planificación FIFO de sensores, topología de red y buffer de ingesta.
 */
public class GestorDeRed {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 1. Historial cronológico de eventos.
    private final List<String> historialEventos;

    // 2. Navegación circular de alertas. El elemento del frente es el actual.
    private final Deque<String> navegadorAlertas;
    private String alertaActual;

    // 3. Cola FIFO de sensores programados.
    private final Queue<String> colaMonitoreo;

    // 4. Grafo no dirigido representado mediante listas de adyacencia.
    private final Map<String, List<String>> topologiaRed;

    // 5. Buffer FIFO de lecturas entrantes.
    private final Queue<LecturaSensor> bufferIngesta;

    public GestorDeRed() {
        this.historialEventos = new ArrayList<>();
        this.navegadorAlertas = new ArrayDeque<>();
        this.colaMonitoreo = new ArrayDeque<>();
        this.topologiaRed = new HashMap<>();
        this.bufferIngesta = new ArrayDeque<>();
        this.alertaActual = null;
    }

    // =========================================================================
    // SECCIÓN 1: HISTORIAL Y EVENTOS
    // =========================================================================

    /**
     * Registra un evento con la fecha y hora actuales.
     */
    public void agregarEvento(String evento) {
        if (evento == null || evento.isBlank()) {
            throw new IllegalArgumentException("El evento no puede estar vacío");
        }

        String fechaHora = LocalDateTime.now().format(FORMATO_FECHA);
        historialEventos.add("[" + fechaHora + "] " + evento);
    }

    /**
     * Devuelve una copia de los últimos eventos, sin exponer la lista interna.
     */
    public List<String> obtenerEventosRecientes(int limite) {
        if (limite <= 0 || historialEventos.isEmpty()) {
            return new ArrayList<>();
        }

        int inicio = Math.max(0, historialEventos.size() - limite);
        return new ArrayList<>(historialEventos.subList(inicio, historialEventos.size()));
    }

    /**
     * Conserva únicamente los últimos 'mantener' eventos.
     */
    public void limpiarHistorialAntiguo(int mantener) {
        if (mantener < 0) {
            throw new IllegalArgumentException("El límite no puede ser negativo");
        }

        while (historialEventos.size() > mantener) {
            historialEventos.remove(0);
        }
    }

    // =========================================================================
    // SECCIÓN 2: NAVEGACIÓN CIRCULAR DE ALERTAS
    // =========================================================================

    /**
     * Agrega una alerta al historial. La primera alerta queda seleccionada;
     * las siguientes se agregan al final sin cambiar la posición actual.
     */
    public void registrarAlerta(String alerta) {
        validarTexto(alerta, "La alerta");

        navegadorAlertas.addLast(alerta);
        if (alertaActual == null) {
            alertaActual = navegadorAlertas.peekFirst();
        }
    }

    /**
     * Avanza a la siguiente alerta. La navegación es circular.
     * Retorna null si no hay alertas registradas.
     */
    public String navegarAlSiguiente() {
        if (navegadorAlertas.isEmpty()) {
            alertaActual = null;
            return null;
        }

        if (navegadorAlertas.size() > 1) {
            navegadorAlertas.addLast(navegadorAlertas.removeFirst());
        }

        alertaActual = navegadorAlertas.peekFirst();
        return alertaActual;
    }

    /**
     * Retrocede a la alerta anterior. La navegación es circular.
     * Retorna null si no hay alertas registradas.
     */
    public String navegarAlAnterior() {
        if (navegadorAlertas.isEmpty()) {
            alertaActual = null;
            return null;
        }

        if (navegadorAlertas.size() > 1) {
            navegadorAlertas.addFirst(navegadorAlertas.removeLast());
        }

        alertaActual = navegadorAlertas.peekFirst();
        return alertaActual;
    }

    public String getAlertaActual() {
        return alertaActual;
    }

    // =========================================================================
    // SECCIÓN 3: CICLO DE MONITOREO
    // =========================================================================

    /**
     * Programa un sensor una sola vez dentro de la cola de monitoreo.
     */
    public void programarConsulta(String idSensor) {
        validarTexto(idSensor, "El identificador del sensor");

        if (!colaMonitoreo.contains(idSensor)) {
            colaMonitoreo.offer(idSensor);
        }
    }

    /**
     * Atiende el sensor del frente y lo vuelve a ubicar al final para
     * mantener la rotación continua. Retorna null si la cola está vacía.
     */
    public String obtenerSiguienteSensor() {
        String siguiente = colaMonitoreo.poll();

        if (siguiente != null) {
            colaMonitoreo.offer(siguiente);
        }

        return siguiente;
    }

    // =========================================================================
    // SECCIÓN 4: TOPOLOGÍA DE RED
    // =========================================================================

    /**
     * Registra un sensor aunque todavía no tenga conexiones.
     * Es necesario para poder identificar estaciones aisladas.
     */
    public void registrarSensor(String idSensor) {
        validarTexto(idSensor, "El identificador del sensor");
        topologiaRed.computeIfAbsent(idSensor, clave -> new ArrayList<>());
    }

    /**
     * Registra una conexión bidireccional sin duplicar vecinos.
     */
    public void registrarConexion(String sensorA, String sensorB) {
        validarTexto(sensorA, "El identificador del primer sensor");
        validarTexto(sensorB, "El identificador del segundo sensor");

        List<String> vecinosA =
                topologiaRed.computeIfAbsent(sensorA, clave -> new ArrayList<>());
        List<String> vecinosB =
                topologiaRed.computeIfAbsent(sensorB, clave -> new ArrayList<>());

        if (Objects.equals(sensorA, sensorB)) {
            return;
        }

        if (!vecinosA.contains(sensorB)) {
            vecinosA.add(sensorB);
        }

        if (!vecinosB.contains(sensorA)) {
            vecinosB.add(sensorA);
        }
    }

    /**
     * Retorna los vecinos de un sensor. Si no existe, retorna una lista vacía.
     * Se entrega una copia para proteger la topología interna.
     */
    public List<String> obtenerVecinos(String idSensor) {
        if (idSensor == null) {
            return new ArrayList<>();
        }

        List<String> vecinos = topologiaRed.get(idSensor);
        return vecinos == null ? new ArrayList<>() : new ArrayList<>(vecinos);
    }

    /**
     * Retorna todos los sensores registrados que no tienen vecinos.
     */
    public List<String> detectarEstacionesAisladas() {
        return topologiaRed.entrySet().stream()
                .filter(entrada -> entrada.getValue().isEmpty())
                .map(Map.Entry::getKey)
                .sorted()
                .collect(Collectors.toList());
    }

    // =========================================================================
    // SECCIÓN 5: BUFFER DE INGESTA
    // =========================================================================

    /**
     * Agrega una lectura al final del buffer FIFO.
     */
    public void recibirLectura(LecturaSensor lectura) {
        if (lectura == null) {
            throw new IllegalArgumentException("La lectura no puede ser null");
        }

        bufferIngesta.offer(lectura);
    }

    /**
     * Procesa y devuelve la lectura más antigua del buffer.
     * Retorna null cuando no hay lecturas pendientes.
     */
    public LecturaSensor procesarSiguienteLectura() {
        return bufferIngesta.poll();
    }

    /**
     * Filtra las lecturas pendientes cuyo PM2.5 supera el límite indicado.
     * No elimina ni modifica las lecturas del buffer.
     */
    public List<LecturaSensor> filtrarAlertasCriticas(double limitePm25) {
        if (!Double.isFinite(limitePm25)) {
            throw new IllegalArgumentException("El límite PM2.5 debe ser un número finito");
        }

        return bufferIngesta.stream()
                .filter(lectura -> lectura.getPm25() > limitePm25)
                .collect(Collectors.toList());
    }

    private void validarTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " no puede estar vacío");
        }
    }
}
